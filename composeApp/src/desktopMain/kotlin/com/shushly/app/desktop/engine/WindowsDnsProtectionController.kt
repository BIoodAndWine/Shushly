package com.shushly.app.desktop.engine

import com.shushly.app.domain.engine.DnsPacketParser
import com.shushly.app.domain.engine.DnsProtectionController
import com.shushly.app.domain.engine.DohClient
import com.shushly.app.domain.model.DnsConfiguration
import com.shushly.app.domain.model.ProtectionLevel
import com.shushly.app.domain.model.ProtectionStatus
import com.shushly.app.domain.model.ResolverHealth
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress

class WindowsDnsProtectionController(
    initialConfig: DnsConfiguration = DnsConfiguration(),
    private val dohClient: DohClient = DohClient()
) : DnsProtectionController {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _status = MutableStateFlow(
        ProtectionStatus(
            state = StatusState.DISABLED,
            activeLevel = initialConfig.protectionLevel,
            activeResolverName = initialConfig.protectionLevel.filterName,
            activeResolverEndpoint = initialConfig.protectionLevel.dohUrl,
            isCustomResolver = initialConfig.isCustomConfig
        )
    )
    override val status: StateFlow<ProtectionStatus> = _status.asStateFlow()

    private val _configuration = MutableStateFlow(initialConfig)
    override val configuration: StateFlow<DnsConfiguration> = _configuration.asStateFlow()

    private var forwarderSocket: DatagramSocket? = null
    private var forwarderJob: Job? = null
    private var originalDnsBackup: SavedAdapterDns? = null

    private var activeDohUrl = initialConfig.protectionLevel.dohUrl
    private var activeBackupDohUrl: String? = null

    private val httpClient by lazy {
        HttpClient(CIO)
    }

    init {
        // Register shutdown hook to guarantee original DNS restoration on exit/crash
        Runtime.getRuntime().addShutdownHook(Thread {
            restoreOriginalDnsSync()
        })
    }

    override suspend fun enableProtection(level: ProtectionLevel): Result<Unit> = withContext(Dispatchers.IO) {
        val config = _configuration.value.copy(
            isProtectionEnabled = true,
            protectionLevel = level,
            isCustomConfig = false
        )
        enableWithConfigInternal(config, level.dohUrl, null, level.filterName)
    }

    override suspend fun enableProtectionWithConfig(config: DnsConfiguration): Result<Unit> = withContext(Dispatchers.IO) {
        val primaryUrl = if (config.isCustomConfig && !config.primaryCustomDohUrl.isNullOrBlank()) {
            config.primaryCustomDohUrl.trim()
        } else {
            config.protectionLevel.dohUrl
        }
        val backupUrl = if (config.isCustomConfig && !config.secondaryCustomDohUrl.isNullOrBlank()) {
            config.secondaryCustomDohUrl.trim()
        } else null

        val filterName = if (config.isCustomConfig) "Custom DoH" else config.protectionLevel.filterName
        enableWithConfigInternal(config, primaryUrl, backupUrl, filterName)
    }

    private suspend fun enableWithConfigInternal(
        config: DnsConfiguration,
        dohUrl: String,
        backupDohUrl: String?,
        filterName: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        _status.value = ProtectionStatus(
            state = StatusState.CONNECTING,
            activeResolverName = filterName,
            activeResolverEndpoint = dohUrl,
            activeLevel = config.protectionLevel,
            isCustomResolver = config.isCustomConfig
        )

        activeDohUrl = dohUrl
        activeBackupDohUrl = backupDohUrl

        // 1. Start local DNS-over-HTTPS forwarder on 127.0.0.1:53
        val startForwarderRes = startLocalForwarder()
        if (startForwarderRes.isFailure) {
            val err = startForwarderRes.exceptionOrNull()?.message ?: "Failed to start local DNS forwarder on port 53"
            _status.value = ProtectionStatus(
                state = StatusState.ERROR,
                errorMessage = err,
                activeLevel = config.protectionLevel,
                activeResolverName = filterName,
                activeResolverEndpoint = dohUrl,
                isCustomResolver = config.isCustomConfig
            )
            return@withContext Result.failure(Exception(err))
        }

        // 2. Configure Windows active network adapter to route DNS to 127.0.0.1
        val adapterRes = configureWindowsAdapterDns(forwarderIp = "127.0.0.1")
        if (adapterRes.isFailure) {
            val err = adapterRes.exceptionOrNull()?.message ?: "Failed to configure Windows DNS client settings"
            stopLocalForwarder()
            _status.value = ProtectionStatus(
                state = StatusState.ERROR,
                errorMessage = err,
                activeLevel = config.protectionLevel,
                activeResolverName = filterName,
                activeResolverEndpoint = dohUrl,
                isCustomResolver = config.isCustomConfig
            )
            return@withContext Result.failure(Exception(err))
        }

        // 3. Verify resolution
        val health = dohClient.checkHealth(dohUrl)

        _configuration.value = config
        _status.value = ProtectionStatus(
            state = StatusState.CONNECTED,
            activeResolverName = filterName,
            activeResolverEndpoint = dohUrl,
            activeLevel = config.protectionLevel,
            isCustomResolver = config.isCustomConfig,
            connectedSinceEpochMs = System.currentTimeMillis(),
            latencyMs = health.latencyMs
        )

        Result.success(Unit)
    }

    override suspend fun disableProtection(): Result<Unit> = withContext(Dispatchers.IO) {
        restoreWindowsAdapterDns()
        stopLocalForwarder()
        _configuration.value = _configuration.value.copy(isProtectionEnabled = false)
        _status.value = ProtectionStatus(
            state = StatusState.DISABLED,
            activeLevel = _configuration.value.protectionLevel,
            activeResolverName = _configuration.value.protectionLevel.filterName,
            activeResolverEndpoint = _configuration.value.protectionLevel.dohUrl,
            isCustomResolver = _configuration.value.isCustomConfig
        )
        Result.success(Unit)
    }

    override suspend fun setProtectionLevel(level: ProtectionLevel): Result<Unit> {
        val updated = _configuration.value.copy(protectionLevel = level, isCustomConfig = false)
        _configuration.value = updated
        if (status.value.state == StatusState.CONNECTED) {
            return enableProtection(level)
        } else {
            _status.value = _status.value.copy(
                activeLevel = level,
                activeResolverName = level.filterName,
                activeResolverEndpoint = level.dohUrl,
                isCustomResolver = false
            )
        }
        return Result.success(Unit)
    }

    override suspend fun updateConfiguration(config: DnsConfiguration): Result<Unit> {
        _configuration.value = config
        if (status.value.state == StatusState.CONNECTED) {
            return enableProtectionWithConfig(config)
        } else {
            _status.value = _status.value.copy(
                activeLevel = config.protectionLevel,
                activeResolverName = if (config.isCustomConfig) "Custom DoH" else config.protectionLevel.filterName,
                activeResolverEndpoint = if (config.isCustomConfig && !config.primaryCustomDohUrl.isNullOrBlank()) config.primaryCustomDohUrl else config.protectionLevel.dohUrl,
                isCustomResolver = config.isCustomConfig
            )
        }
        return Result.success(Unit)
    }

    override suspend fun testResolver(endpointUrl: String): ResolverHealth {
        return dohClient.checkHealth(endpointUrl)
    }

    override suspend fun checkHealth(): ResolverHealth {
        return dohClient.checkHealth(status.value.activeResolverEndpoint)
    }

    private fun startLocalForwarder(): Result<Unit> {
        return try {
            stopLocalForwarder()
            val socket = DatagramSocket(null)
            socket.reuseAddress = true
            socket.bind(InetSocketAddress(InetAddress.getByName("127.0.0.1"), 53))
            forwarderSocket = socket

            forwarderJob = scope.launch(Dispatchers.IO) {
                val receiveBuffer = ByteArray(4096)
                while (isActive && !socket.isClosed) {
                    try {
                        val packet = DatagramPacket(receiveBuffer, receiveBuffer.size)
                        socket.receive(packet)
                        val queryBytes = packet.data.copyOfRange(0, packet.length)
                        val clientAddress = packet.address
                        val clientPort = packet.port

                        // Forward asynchronously to DoH
                        launch(Dispatchers.IO) {
                            val response = forwardDoH(queryBytes)
                            if (response != null && !socket.isClosed) {
                                val replyPacket = DatagramPacket(response, response.size, clientAddress, clientPort)
                                synchronized(socket) {
                                    if (!socket.isClosed) {
                                        socket.send(replyPacket)
                                    }
                                }
                            }
                        }
                    } catch (_: Exception) {
                        // Socket closed or packet drop
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun forwardDoH(queryBytes: ByteArray): ByteArray? {
        val primary = tryDoHPost(activeDohUrl, queryBytes)
        if (primary != null) return primary

        val backup = activeBackupDohUrl
        if (!backup.isNullOrBlank()) {
            return tryDoHPost(backup, queryBytes)
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

    private fun stopLocalForwarder() {
        try {
            forwarderJob?.cancel()
            forwarderJob = null
            forwarderSocket?.close()
            forwarderSocket = null
        } catch (_: Exception) {}
    }

    private fun configureWindowsAdapterDns(forwarderIp: String): Result<Unit> {
        val activeInterfaceIndex = getActiveInterfaceIndex()
            ?: return Result.failure(Exception("Could not identify active network interface index"))

        // Backup existing DNS settings
        val currentDns = getInterfaceDns(activeInterfaceIndex)
        originalDnsBackup = SavedAdapterDns(
            interfaceIndex = activeInterfaceIndex,
            isDhcp = currentDns.isEmpty(),
            savedServers = currentDns
        )

        // Set DNS to 127.0.0.1
        val script = "Set-DnsClientServerAddress -InterfaceIndex $activeInterfaceIndex -ServerAddresses ('$forwarderIp'); Clear-DnsClientCache"
        val res = runPowerShell(script)
        if (res.exitCode != 0) {
            return Result.failure(Exception("PowerShell failed to set DNS: ${res.output}"))
        }
        return Result.success(Unit)
    }

    private fun restoreWindowsAdapterDns() {
        val backup = originalDnsBackup ?: return
        val script = if (backup.isDhcp || backup.savedServers.isEmpty()) {
            "Set-DnsClientServerAddress -InterfaceIndex ${backup.interfaceIndex} -ResetServerAddresses; Clear-DnsClientCache"
        } else {
            val serverList = backup.savedServers.joinToString(",") { "'$it'" }
            "Set-DnsClientServerAddress -InterfaceIndex ${backup.interfaceIndex} -ServerAddresses ($serverList); Clear-DnsClientCache"
        }
        runPowerShell(script)
        originalDnsBackup = null
    }

    private fun restoreOriginalDnsSync() {
        restoreWindowsAdapterDns()
        stopLocalForwarder()
    }

    private fun getActiveInterfaceIndex(): Int? {
        val script = "Get-NetAdapter | Where-Object { \$_.Status -eq 'Up' -and \$_.InterfaceAlias -notmatch 'vEthernet|Loopback|WSL' } | Select-Object -First 1 -ExpandProperty ifIndex"
        val res = runPowerShell(script)
        return res.output.trim().toIntOrNull()
    }

    private fun getInterfaceDns(interfaceIndex: Int): List<String> {
        val script = "Get-DnsClientServerAddress -InterfaceIndex $interfaceIndex -AddressFamily IPv4 | Select-Object -ExpandProperty ServerAddresses"
        val res = runPowerShell(script)
        return res.output.lines().map { it.trim() }.filter { it.isNotBlank() && it != "127.0.0.1" }
    }

    private fun runPowerShell(command: String): CommandResult {
        return try {
            val process = ProcessBuilder("powershell", "-NoProfile", "-NonInteractive", "-Command", command)
                .redirectErrorStream(true)
                .start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readText()
            val exitCode = process.waitFor()
            CommandResult(exitCode, output)
        } catch (e: Exception) {
            CommandResult(-1, e.message ?: "Failed to run powershell command")
        }
    }

    private data class SavedAdapterDns(
        val interfaceIndex: Int,
        val isDhcp: Boolean,
        val savedServers: List<String>
    )

    private data class CommandResult(
        val exitCode: Int,
        val output: String
    )
}
