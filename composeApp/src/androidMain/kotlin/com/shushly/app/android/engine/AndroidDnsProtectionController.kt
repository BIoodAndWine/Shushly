package com.shushly.app.android.engine

import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import com.shushly.app.android.vpn.ShushlyVpnService
import com.shushly.app.data.repository.DnsDirectoryRepository
import com.shushly.app.domain.engine.DnsProtectionController
import com.shushly.app.domain.engine.DohClient
import com.shushly.app.domain.model.DnsConfiguration
import com.shushly.app.domain.model.ProtectionLevel
import com.shushly.app.domain.model.ProtectionStatus
import com.shushly.app.domain.model.ResolverHealth
import com.shushly.app.domain.model.StatusState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AndroidDnsProtectionController(
    private val context: Context,
    private val directoryRepository: DnsDirectoryRepository = DnsDirectoryRepository(),
    private val dohClient: DohClient = DohClient()
) : DnsProtectionController {

    override val status: StateFlow<ProtectionStatus> = ShushlyVpnService.serviceStatus

    private val _configuration = MutableStateFlow(DnsConfiguration())
    override val configuration: StateFlow<DnsConfiguration> = _configuration.asStateFlow()

    override suspend fun enableProtection(level: ProtectionLevel): Result<Unit> {
        val config = _configuration.value.copy(
            isProtectionEnabled = true,
            protectionLevel = level,
            isCustomConfig = false
        )
        return startVpnWithConfig(config, level.dohUrl, null, level.filterName)
    }

    override suspend fun enableProtectionWithConfig(config: DnsConfiguration): Result<Unit> {
        val primaryUrl = config.getEffectivePrimaryDohUrl(directoryRepository.providers)
        val backupUrl = config.getEffectiveSecondaryDohUrl(directoryRepository.providers)
        val filterName = if (config.isCustomConfig) "Custom DoH" else config.protectionLevel.filterName
        return startVpnWithConfig(config, primaryUrl, backupUrl, filterName)
    }

    private fun startVpnWithConfig(
        config: DnsConfiguration,
        dohUrl: String,
        backupDohUrl: String?,
        filterName: String
    ): Result<Unit> {
        return try {
            val prepareIntent = VpnService.prepare(context)
            if (prepareIntent != null) {
                // Return failure with specific code indicating permission required
                return Result.failure(VpnPermissionRequiredException(prepareIntent))
            }

            val intent = Intent(context, ShushlyVpnService::class.java).apply {
                action = ShushlyVpnService.ACTION_START
                putExtra(ShushlyVpnService.EXTRA_DOH_URL, dohUrl)
                putExtra(ShushlyVpnService.EXTRA_BACKUP_DOH_URL, backupDohUrl)
                putExtra(ShushlyVpnService.EXTRA_FILTER_NAME, filterName)
                putExtra(ShushlyVpnService.EXTRA_PROTECTION_LEVEL, config.protectionLevel.name)
                putExtra(ShushlyVpnService.EXTRA_IS_CUSTOM, config.isCustomConfig)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }

            _configuration.value = config
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun disableProtection(): Result<Unit> {
        return try {
            val intent = Intent(context, ShushlyVpnService::class.java).apply {
                action = ShushlyVpnService.ACTION_STOP
            }
            context.startService(intent)
            _configuration.value = _configuration.value.copy(isProtectionEnabled = false)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setProtectionLevel(level: ProtectionLevel): Result<Unit> {
        val updated = _configuration.value.copy(protectionLevel = level, isCustomConfig = false)
        _configuration.value = updated
        if (status.value.state == StatusState.CONNECTED) {
            return enableProtection(level)
        }
        return Result.success(Unit)
    }

    override suspend fun updateConfiguration(config: DnsConfiguration): Result<Unit> {
        _configuration.value = config
        if (status.value.state == StatusState.CONNECTED) {
            return enableProtectionWithConfig(config)
        }
        return Result.success(Unit)
    }

    override suspend fun testResolver(endpointUrl: String): ResolverHealth {
        return dohClient.checkHealth(endpointUrl)
    }

    override suspend fun checkHealth(): ResolverHealth {
        val currentEndpoint = status.value.activeResolverEndpoint
        return dohClient.checkHealth(currentEndpoint)
    }
}

class VpnPermissionRequiredException(val prepareIntent: Intent) : Exception("VPN authorization required")
