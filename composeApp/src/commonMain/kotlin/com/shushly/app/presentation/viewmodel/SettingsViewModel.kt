package com.shushly.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shushly.app.data.repository.SettingsRepository
import com.shushly.app.domain.engine.DnsProtectionController
import com.shushly.app.domain.model.DnsConfiguration
import com.shushly.app.domain.model.ProtectionLevel
import com.shushly.app.domain.model.Region
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val configuration: DnsConfiguration = DnsConfiguration(),
    val isPremium: Boolean = true,
    val themeMode: String = "dark",
    val soundEnabled: Boolean = true,
    val appVersion: String = "0.1.1"
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val protectionController: DnsProtectionController
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.configuration,
        settingsRepository.themeMode,
        settingsRepository.soundEnabled
    ) { config, theme, sound ->
        SettingsUiState(
            configuration = config,
            isPremium = true,
            themeMode = theme,
            soundEnabled = sound,
            appVersion = "0.1.1"
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setAutoReconnect(enabled: Boolean) {
        val updated = uiState.value.configuration.copy(autoReconnect = enabled)
        settingsRepository.updateConfiguration(updated)
    }

    fun setStartOnBoot(enabled: Boolean) {
        val updated = uiState.value.configuration.copy(startOnBoot = enabled)
        settingsRepository.updateConfiguration(updated)
    }

    fun setThemeMode(mode: String) {
        settingsRepository.setThemeMode(mode)
    }

    fun setSoundEnabled(enabled: Boolean) {
        settingsRepository.setSoundEnabled(enabled)
    }

    fun restoreDefaultConfiguration() {
        viewModelScope.launch {
            val defaultConfig = DnsConfiguration(
                isProtectionEnabled = false,
                protectionLevel = ProtectionLevel.LIGHT,
                isCustomConfig = false,
                primaryProviderId = "controld-oisd",
                secondaryProviderId = "adguard",
                selectedRegion = Region.NA,
                autoFailover = true,
                autoReconnect = true,
                startOnBoot = false
            )
            settingsRepository.updateConfiguration(defaultConfig)
            if (settingsRepository.configuration.value.isProtectionEnabled) {
                protectionController.updateConfiguration(defaultConfig)
            }
        }
    }
}
