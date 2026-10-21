package com.shushly.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shushly.app.data.repository.BillingRepository
import com.shushly.app.data.repository.SettingsRepository
import com.shushly.app.domain.engine.DnsProtectionController
import com.shushly.app.domain.model.DnsConfiguration
import com.shushly.app.domain.model.ProtectionLevel
import com.shushly.app.domain.model.ProtectionStatus
import com.shushly.app.domain.model.StatusState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val status: ProtectionStatus = ProtectionStatus(),
    val configuration: DnsConfiguration = DnsConfiguration(),
    val isPremium: Boolean = false,
    val isBusy: Boolean = false,
    val feedbackMessage: String? = null
)

class HomeViewModel(
    private val protectionController: DnsProtectionController,
    private val settingsRepository: SettingsRepository,
    private val billingRepository: BillingRepository
) : ViewModel() {

    private val _isBusy = MutableStateFlow(false)
    private val _feedbackMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<HomeUiState> = combine(
        protectionController.status,
        settingsRepository.configuration,
        billingRepository.entitlement,
        _isBusy,
        _feedbackMessage
    ) { status, config, entitlement, busy, feedback ->
        HomeUiState(
            status = status,
            configuration = config,
            isPremium = entitlement.isPremium,
            isBusy = busy,
            feedbackMessage = feedback
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun toggleProtection() {
        val currentStatus = uiState.value.status
        if (uiState.value.isBusy) return

        viewModelScope.launch {
            _isBusy.value = true
            _feedbackMessage.value = null
            try {
                if (currentStatus.state == StatusState.CONNECTED || currentStatus.state == StatusState.CONNECTING) {
                    protectionController.disableProtection()
                    settingsRepository.setProtectionEnabled(false)
                } else {
                    val config = settingsRepository.configuration.value
                    if (config.isCustomConfig && uiState.value.isPremium) {
                        protectionController.enableProtectionWithConfig(config)
                    } else {
                        protectionController.enableProtection(config.protectionLevel)
                    }
                    settingsRepository.setProtectionEnabled(true)
                }
            } catch (e: Exception) {
                _feedbackMessage.value = e.message ?: "Failed to change protection state"
            } finally {
                _isBusy.value = false
            }
        }
    }

    fun selectProtectionLevel(level: ProtectionLevel) {
        if (uiState.value.configuration.protectionLevel == level && !uiState.value.configuration.isCustomConfig) return

        viewModelScope.launch {
            settingsRepository.setProtectionLevel(level)
            if (uiState.value.status.state == StatusState.CONNECTED) {
                _isBusy.value = true
                protectionController.setProtectionLevel(level)
                _isBusy.value = false
            } else {
                protectionController.setProtectionLevel(level)
            }
        }
    }

    fun retryConnection() {
        toggleProtection()
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }
}
