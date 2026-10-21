package com.shushly.app.android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color as AndroidColor
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.shushly.app.App
import com.shushly.app.android.engine.AndroidDnsProtectionController
import com.shushly.app.data.repository.SettingsRepository
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var protectionController: AndroidDnsProtectionController
    private val settingsRepository by lazy { SettingsRepository() }

    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Permission granted, trigger enable
            lifecycleScope.launch {
                val config = settingsRepository.configuration.value
                if (config.isCustomConfig) {
                    protectionController.enableProtectionWithConfig(config)
                } else {
                    protectionController.enableProtection(config.protectionLevel)
                }
            }
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Notification permission handled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        protectionController = AndroidDnsProtectionController(applicationContext)

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            val themeMode by settingsRepository.themeMode.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val isDark = when (themeMode.lowercase()) {
                "light" -> false
                "dark" -> true
                else -> systemDark
            }

            DisposableEffect(isDark) {
                enableEdgeToEdge(
                    statusBarStyle = if (isDark) {
                        SystemBarStyle.dark(AndroidColor.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
                    },
                    navigationBarStyle = if (isDark) {
                        SystemBarStyle.dark(AndroidColor.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
                    }
                )
                onDispose {}
            }

            App(
                protectionController = object : com.shushly.app.domain.engine.DnsProtectionController by protectionController {
                    override suspend fun enableProtection(level: com.shushly.app.domain.model.ProtectionLevel): Result<Unit> {
                        val prepareIntent = VpnService.prepare(this@MainActivity)
                        if (prepareIntent != null) {
                            vpnPermissionLauncher.launch(prepareIntent)
                            return Result.success(Unit)
                        }
                        return protectionController.enableProtection(level)
                    }

                    override suspend fun enableProtectionWithConfig(config: com.shushly.app.domain.model.DnsConfiguration): Result<Unit> {
                        val prepareIntent = VpnService.prepare(this@MainActivity)
                        if (prepareIntent != null) {
                            vpnPermissionLauncher.launch(prepareIntent)
                            return Result.success(Unit)
                        }
                        return protectionController.enableProtectionWithConfig(config)
                    }
                },
                settingsRepository = settingsRepository,
                soundPlayer = remember { com.shushly.app.domain.audio.AndroidSoundPlayer(this@MainActivity) }
            )
        }
    }
}
