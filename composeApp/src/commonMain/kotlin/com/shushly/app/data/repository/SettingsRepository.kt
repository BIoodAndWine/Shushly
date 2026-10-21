package com.shushly.app.data.repository

import com.russhwolf.settings.Settings
import com.shushly.app.domain.model.DnsConfiguration
import com.shushly.app.domain.model.PremiumEntitlement
import com.shushly.app.domain.model.ProtectionLevel
import com.shushly.app.domain.model.Region
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SettingsRepository(
    private val settings: Settings = Settings()
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val _configuration = MutableStateFlow(loadConfiguration())
    val configuration: StateFlow<DnsConfiguration> = _configuration.asStateFlow()

    private val _entitlement = MutableStateFlow(loadEntitlement())
    val entitlement: StateFlow<PremiumEntitlement> = _entitlement.asStateFlow()

    private val _themeMode = MutableStateFlow(settings.getString(KEY_THEME_MODE, "system"))
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _soundEnabled = MutableStateFlow(settings.getBoolean(KEY_SOUND_ENABLED, true))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    fun updateConfiguration(newConfig: DnsConfiguration) {
        _configuration.value = newConfig
        saveConfiguration(newConfig)
    }

    fun setProtectionLevel(level: ProtectionLevel) {
        val updated = _configuration.value.copy(
            protectionLevel = level,
            isCustomConfig = false
        )
        updateConfiguration(updated)
    }

    fun setProtectionEnabled(enabled: Boolean) {
        val updated = _configuration.value.copy(isProtectionEnabled = enabled)
        updateConfiguration(updated)
    }

    fun updateEntitlement(entitlement: PremiumEntitlement) {
        _entitlement.value = entitlement
        saveEntitlement(entitlement)
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
        settings.putString(KEY_THEME_MODE, mode)
    }

    fun setSoundEnabled(enabled: Boolean) {
        _soundEnabled.value = enabled
        settings.putBoolean(KEY_SOUND_ENABLED, enabled)
    }

    private fun loadConfiguration(): DnsConfiguration {
        val raw = settings.getStringOrNull(KEY_CONFIG)
        if (raw != null) {
            try {
                return json.decodeFromString(raw)
            } catch (_: Exception) {
                // Fallback to default
            }
        }
        return DnsConfiguration(
            isProtectionEnabled = false,
            protectionLevel = ProtectionLevel.LIGHT,
            isCustomConfig = false,
            primaryProviderId = "controld-oisd",
            secondaryProviderId = "adguard",
            selectedRegion = Region.NA,
            autoFailover = true,
            autoReconnect = true
        )
    }

    private fun saveConfiguration(config: DnsConfiguration) {
        try {
            val serialized = json.encodeToString(config)
            settings.putString(KEY_CONFIG, serialized)
        } catch (_: Exception) {
            // Safe fallback
        }
    }

    private fun loadEntitlement(): PremiumEntitlement {
        return PremiumEntitlement(
            isPremium = true,
            purchaseToken = "shushly_unlocked",
            purchaseTimestamp = 0L,
            storeSource = "FreeAndOpen"
        )
    }

    private fun saveEntitlement(entitlement: PremiumEntitlement) {
        // Obsolete: All advanced features unlocked forever
    }

    fun resetAllSettings() {
        settings.clear()
        _configuration.value = DnsConfiguration()
        _themeMode.value = "system"
        _soundEnabled.value = true
    }

    companion object {
        const val KEY_CONFIG = "shushly_dns_config_v1"
        const val KEY_ENTITLEMENT = "shushly_premium_entitlement_v1"
        const val KEY_THEME_MODE = "shushly_theme_mode"
        const val KEY_SOUND_ENABLED = "shushly_sound_enabled"
    }
}
