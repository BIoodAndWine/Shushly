package com.shushly.app.android.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.shushly.app.domain.engine.DnsPacketParser
import com.shushly.app.domain.model.ProtectionLevel
import com.shushly.app.domain.model.ProtectionStatus
import com.shushly.app.domain.model.StatusState
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.InetAddress
import java.nio.ByteBuffer

class ShushlyVpnService : VpnService() {

    private val serviceJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + serviceJob)

    private var vpnInterface: ParcelFileDescriptor? = null
    private var isRunning = false
    private var packetLoopJob: Job? = null

    private var activeDohUrl = ProtectionLevel.BALANCED.dohUrl
    private var activeBackupDohUrl: String? = null
    private var activeFilterName = ProtectionLevel.BALANCED.filterName
    private var activeProtectionLevel = ProtectionLevel.BALANCED
    private var isCustomResolver = false

    private val httpClient by lazy {
        HttpClient(CIO)
    }

    private val connectivityManager by lazy {
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            if (isRunning) {
                _serviceStatus.value = _serviceStatus.value.copy(
                    state = StatusState.CONNECTED,
                    activeLevel = activeProtectionLevel,
                    activeResolverName = activeFilterName,
                    activeResolverEndpoint = activeDohUrl,
                    isCustomResolver = isCustomResolver
                )
                updateNotification("Protection is On")
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            if (isRunning) {
                _serviceStatus.value = _serviceStatus.value.copy(state = StatusState.RECONNECTING)
                updateNotification("Reconnecting...")
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP) {
            stopVpn()
            return START_NOT_STICKY
        }

        val dohUrl = intent?.getStringExtra(EXTRA_DOH_URL) ?: ProtectionLevel.BALANCED.dohUrl
        val backupDohUrl = intent?.getStringExtra(EXTRA_BACKUP_DOH_URL)
        val filterName = intent?.getStringExtra(EXTRA_FILTER_NAME) ?: ProtectionLevel.BALANCED.filterName
        val levelName = intent?.getStringExtra(EXTRA_PROTECTION_LEVEL)
        val isCustom = intent?.getBooleanExtra(EXTRA_IS_CUSTOM, false) ?: false

        if (levelName != null) {
            try {
                activeProtectionLevel = ProtectionLevel.valueOf(levelName)
            } catch (_: Exception) {}
        }

        activeDohUrl = dohUrl
        activeBackupDohUrl = backupDohUrl
        activeFilterName = filterName
        isCustomResolver = isCustom

        if (isRunning) {
            // Already running: smoothly update resolver and live status without interrupting tunnel
            _serviceStatus.value = _serviceStatus.value.copy(
                activeResolverName = activeFilterName,
                activeResolverEndpoint = activeDohUrl,
                activeLevel = activeProtectionLevel,
                isCustomResolver = isCustomResolver
            )
            updateNotification("Protection is On ($activeFilterName)")
            return START_STICKY
        }

        startVpn()
        return START_STICKY
    }

    private fun startVpn() {
        if (isRunning) {
            // Already running, update notification
            updateNotification("Protection is On ($activeFilterName)")
            return
        }

        try {
            _serviceStatus.value = ProtectionStatus(
                state = StatusState.CONNECTING,
                activeResolverName = activeFilterName,
                activeResolverEndpoint = activeDohUrl,
                activeLevel = activeProtectionLevel,
                isCustomResolver = isCustomResolver
            )

            startForeground(NOTIFICATION_ID, buildNotification("Connecting..."))

            val builder = Builder()
                .setSession("Shushly DNS Protection")
                .setMtu(1500)
                // Assign local dummy IP to the TUN interface
                .addAddress("10.233.0.2", 32)
                .addAddress("fd00:1::2", 128)
                // Route ONLY DNS traffic through this TUN interface
                .addDnsServer("10.233.0.1")
                .addDnsServer("fd00:1::1")
                .addRoute("10.233.0.1", 32)
                .addRoute("fd00:1::1", 128)
                .setBlocking(false)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                builder.setMetered(false)
            }

            vpnInterface = builder.establish()

            if (vpnInterface == null) {
                _serviceStatus.value = ProtectionStatus(
                    state = StatusState.ERROR,
                    errorMessage = "Failed to establish VPN interface. Permission may have been revoked.",
                    activeLevel = activeProtectionLevel,
                    activeResolverName = activeFilterName,
                    activeResolverEndpoint = activeDohUrl,
                    isCustomResolver = isCustomResolver
                )
                stopSelf()
                return
            }

            isRunning = true
            _serviceStatus.value = ProtectionStatus(
                state = StatusState.CONNECTED,
                activeResolverName = activeFilterName,
                activeResolverEndpoint = activeDohUrl,
                activeLevel = activeProtectionLevel,
                isCustomResolver = isCustomResolver,
                connectedSinceEpochMs = System.currentTimeMillis()
            )

            updateNotification("Protection is On")
            startPacketLoop()

        } catch (e: Exception) {
            _serviceStatus.value = ProtectionStatus(
                state = StatusState.ERROR,
                errorMessage = e.message ?: "Failed to start DNS engine",
                activeLevel = activeProtectionLevel,
                activeResolverName = activeFilterName,
                activeResolverEndpoint = activeDohUrl,
                isCustomResolver = isCustomResolver
            )
            stopSelf()
        }
    }

    private fun startPacketLoop() {
        val pfd = vpnInterface ?: return
        packetLoopJob = scope.launch(Dispatchers.IO) {
            val inputStream = FileInputStream(pfd.fileDescriptor)
            val outputStream = FileOutputStream(pfd.fileDescriptor)
            val buffer = ByteBuffer.allocate(32768)

            while (isActive && isRunning) {
                try {
                    buffer.clear()
                    val length = inputStream.read(buffer.array())
                    if (length > 0) {
                        buffer.limit(length)
                        handlePacket(buffer, outputStream)
                    } else {
                        // Non-blocking wait
                        kotlinx.coroutines.delay(10)
                    }
                } catch (e: Exception) {
                    if (isRunning) {
                        // Safe error recovery
                    }
                }
            }
        }
    }

    private fun handlePacket(buffer: ByteBuffer, outputStream: FileOutputStream) {
        val packet = buffer.array()
        if (packet.size < 28) return // Minimum IPv4 (20) + UDP (8)

        val version = (packet[0].toInt() shr 4) and 0x0F
        if (version == 4) {
            handleIPv4Packet(packet, buffer.limit(), outputStream)
        }
    }

    private fun handleIPv4Packet(packet: ByteArray, length: Int, outputStream: FileOutputStream) {
        val ihl = (packet[0].toInt() and 0x0F) * 4
        val protocol = packet[9].toInt() and 0xFF

        // Protocol 17 = UDP
        if (protocol != 17 || length < ihl + 8) return

        val srcIp = packet.copyOfRange(12, 16)
        val dstIp = packet.copyOfRange(16, 20)

        val srcPort = ((packet[ihl].toInt() and 0xFF) shl 8) or (packet[ihl + 1].toInt() and 0xFF)
        val dstPort = ((packet[ihl + 2].toInt() and 0xFF) shl 8) or (packet[ihl + 3].toInt() and 0xFF)

        // Only handle DNS queries on port 53
        if (dstPort != 53) return

        val udpLength = ((packet[ihl + 4].toInt() and 0xFF) shl 8) or (packet[ihl + 5].toInt() and 0xFF)
        val dnsPayloadStart = ihl + 8
        val dnsPayloadLength = udpLength - 8

        if (dnsPayloadStart + dnsPayloadLength > length || dnsPayloadLength <= 0) return

        val dnsQuery = packet.copyOfRange(dnsPayloadStart, dnsPayloadStart + dnsPayloadLength)

        // Forward DNS query asynchronously via DoH
        scope.launch(Dispatchers.IO) {
            val responseDns = forwardDoH(dnsQuery)
            if (responseDns != null) {
                sendUdpResponse(
                    outputStream = outputStream,
                    srcIp = dstIp,
                    dstIp = srcIp,
                    srcPort = dstPort,
                    dstPort = srcPort,
                    payload = responseDns
                )
            }
        }
    }

    private suspend fun forwardDoH(queryBytes: ByteArray): ByteArray? {
        // Try Primary DoH
        val primaryRes = tryDoHPost(activeDohUrl, queryBytes)
        if (primaryRes != null) return primaryRes

        // Fallback to Secondary DoH if configured
        val backupUrl = activeBackupDohUrl
        if (!backupUrl.isNullOrBlank()) {
            return tryDoHPost(backupUrl, queryBytes)
        }
        return null
    }

    private suspend fun tryDoHPost(url: String, queryBytes: ByteArray): ByteArray? {
        return try {
            val response = httpClient.post(url) {
                contentType(ContentType("application", "dns-message"))
                header("Accept", "application/dns-message")
                setBody(queryBytes)
            }
            if (response.status == HttpStatusCode.OK) {
                response.bodyAsBytes()
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun sendUdpResponse(
        outputStream: FileOutputStream,
        srcIp: ByteArray,
        dstIp: ByteArray,
        srcPort: Int,
        dstPort: Int,
        payload: ByteArray
    ) {
        try {
            val ipHeaderLen = 20
            val udpHeaderLen = 8
            val totalLen = ipHeaderLen + udpHeaderLen + payload.size
            val packet = ByteArray(totalLen)

            // IPv4 Header
            packet[0] = 0x45.toByte() // Version 4, IHL 5 (20 bytes)
            packet[1] = 0x00.toByte() // DSCP/ECN
            packet[2] = ((totalLen shr 8) and 0xFF).toByte()
            packet[3] = (totalLen and 0xFF).toByte()
            packet[4] = 0x00.toByte() // ID
            packet[5] = 0x00.toByte()
            packet[6] = 0x40.toByte() // Don't Fragment
            packet[7] = 0x00.toByte()
            packet[8] = 64.toByte() // TTL
            packet[9] = 17.toByte() // Protocol = UDP
            // Checksum computed later (bytes 10, 11)

            // Source IP & Destination IP
            System.arraycopy(srcIp, 0, packet, 12, 4)
            System.arraycopy(dstIp, 0, packet, 16, 4)

            // Calculate IPv4 Header Checksum
            val ipChecksum = computeIpChecksum(packet, 0, ipHeaderLen)
            packet[10] = ((ipChecksum shr 8) and 0xFF).toByte()
            packet[11] = (ipChecksum and 0xFF).toByte()

            // UDP Header
            val udpLen = udpHeaderLen + payload.size
            packet[20] = ((srcPort shr 8) and 0xFF).toByte()
            packet[21] = (srcPort and 0xFF).toByte()
            packet[22] = ((dstPort shr 8) and 0xFF).toByte()
            packet[23] = (dstPort and 0xFF).toByte()
            packet[24] = ((udpLen shr 8) and 0xFF).toByte()
            packet[25] = (udpLen and 0xFF).toByte()
            packet[26] = 0x00.toByte() // UDP checksum optional in IPv4, 0 is valid
            packet[27] = 0x00.toByte()

            // Copy DNS Response Payload
            System.arraycopy(payload, 0, packet, 28, payload.size)

            synchronized(outputStream) {
                outputStream.write(packet)
            }
        } catch (_: Exception) {
            // Ignore packet write drops
        }
    }

    private fun computeIpChecksum(buf: ByteArray, offset: Int, length: Int): Int {
        var sum = 0
        var i = offset
        while (i < offset + length) {
            val high = buf[i].toInt() and 0xFF
            val low = buf[i + 1].toInt() and 0xFF
            sum += (high shl 8) or low
            i += 2
        }
        while ((sum shr 16) > 0) {
            sum = (sum and 0xFFFF) + (sum shr 16)
        }
        return sum.inv() and 0xFFFF
    }

    private fun stopVpn() {
        isRunning = false
        packetLoopJob?.cancel()
        packetLoopJob = null

        try {
            vpnInterface?.close()
        } catch (_: Exception) {}
        vpnInterface = null

        _serviceStatus.value = ProtectionStatus(
            state = StatusState.DISABLED,
            activeLevel = activeProtectionLevel,
            activeResolverName = activeFilterName,
            activeResolverEndpoint = activeDohUrl,
            isCustomResolver = isCustomResolver
        )
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        stopVpn()
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (_: Exception) {}
        scope.cancel()
        super.onDestroy()
    }

    override fun onRevoke() {
        stopVpn()
        super.onRevoke()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "DNS Protection Status",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows live Shushly ad and tracker protection status"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(statusText: String): Notification {
        val stopIntent = Intent(this, ShushlyVpnService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentTitle("Shushly Ad Blocker")
            .setContentText(statusText)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Disconnect", stopPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(statusText: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, buildNotification(statusText))
    }

    companion object {
        const val ACTION_START = "com.shushly.app.START_VPN"
        const val ACTION_STOP = "com.shushly.app.STOP_VPN"
        const val EXTRA_DOH_URL = "extra_doh_url"
        const val EXTRA_BACKUP_DOH_URL = "extra_backup_doh_url"
        const val EXTRA_FILTER_NAME = "extra_filter_name"
        const val EXTRA_PROTECTION_LEVEL = "extra_protection_level"
        const val EXTRA_IS_CUSTOM = "extra_is_custom"

        private const val CHANNEL_ID = "shushly_protection_channel"
        private const val NOTIFICATION_ID = 4242

        private val _serviceStatus = MutableStateFlow(ProtectionStatus(state = StatusState.DISABLED))
        val serviceStatus: StateFlow<ProtectionStatus> = _serviceStatus.asStateFlow()
    }
}
