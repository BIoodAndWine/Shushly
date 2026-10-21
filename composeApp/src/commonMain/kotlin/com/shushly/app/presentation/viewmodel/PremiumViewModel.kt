package com.shushly.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shushly.app.data.repository.BillingRepository
import com.shushly.app.data.repository.DnsDirectoryRepository
import com.shushly.app.data.repository.SettingsRepository
import com.shushly.app.domain.engine.DnsProtectionController
import com.shushly.app.domain.model.DnsConfiguration
import com.shushly.app.domain.model.DnsProvider
import com.shushly.app.domain.model.Region
import com.shushly.app.domain.model.ResolverHealth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PremiumUiState(
    val isPremium: Boolean = false,
    val draftConfig: DnsConfiguration = DnsConfiguration(),
    val allProviders: List<DnsProvider> = emptyList(),
    val recommendedProviders: List<DnsProvider> = emptyList(),
    val latencies: Map<String, Long> = emptyMap(),
    val isTestingLatency: Boolean = false,
    val isPurchasing: Boolean = false,
    val customDohInput: String = "",
    val customDohError: String? = null,
    val customDohSuccess: Boolean = false,
    val statusMessage: String? = null
)

class PremiumViewModel(
    private val directoryRepository: DnsDirectoryRepository,
    private val settingsRepository: SettingsRepository,
    private val billingRepository: BillingRepository,
    private val protectionController: DnsProtectionController
) : ViewModel() {

    private val _draftConfig = MutableStateFlow(settingsRepository.configuration.value.copy(isCustomConfig = true))
    private val _latencies = MutableStateFlow<Map<String, Long>>(emptyMap())
    private val _isTestingLatency = MutableStateFlow(false)
    private val _isPurchasing = MutableStateFlow(false)
    private val _customDohInput = MutableStateFlow("")
    private val _customDohError = MutableStateFlow<String?>(null)
    private val _customDohSuccess = MutableStateFlow(false)
    private val _statusMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PremiumUiState> = combine(
        billingRepository.entitlement,
        _draftConfig,
        _latencies,
        _isTestingLatency,
        _isPurchasing
    ) { entitlement, draft, latencies, testing, purchasing ->
        val recommended = directoryRepository.getRecommendedForRegion(draft.selectedRegion)
        PremiumUiState(
            isPremium = entitlement.isPremium,
            draftConfig = draft,
            allProviders = directoryRepository.providers,
            recommendedProviders = recommended,
            latencies = latencies,
            isTestingLatency = testing,
            isPurchasing = purchasing,
            customDohInput = _customDohInput.value,
            customDohError = _customDohError.value,
            customDohSuccess = _customDohSuccess.value,
            statusMessage = _statusMessage.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PremiumUiState()
    )

    fun setRegion(region: Region) {
        _draftConfig.value = _draftConfig.value.copy(selectedRegion = region)
    }

    fun setPrimaryProvider(providerId: String) {
        _draftConfig.value = _draftConfig.value.copy(
            primaryProviderId = providerId,
            primaryCustomDohUrl = null
        )
    }

    fun setSecondaryProvider(providerId: String) {
        _draftConfig.value = _draftConfig.value.copy(
            secondaryProviderId = providerId,
            secondaryCustomDohUrl = null
        )
    }

    fun onCustomDohInputChanged(input: String) {
        _customDohInput.value = input
        _customDohError.value = null
        _customDohSuccess.value = false
    }

    fun validateAndAddCustomDoh(asPrimary: Boolean) {
        val input = _customDohInput.value.trim()
        if (input.isBlank()) {
            _customDohError.value = "Please enter a valid DNS-over-HTTPS URL"
            return
        }

        viewModelScope.launch {
            _statusMessage.value = "Validating resolver with live DNS query..."
            val result = directoryRepository.validateCustomDohUrl(input)
            result.onSuccess { health ->
                _customDohSuccess.value = true
                _customDohError.value = null
                _statusMessage.value = "Verified! Response time: ${health.latencyMs} ms"
                if (asPrimary) {
                    _draftConfig.value = _draftConfig.value.copy(primaryCustomDohUrl = input)
                } else {
                    _draftConfig.value = _draftConfig.value.copy(secondaryCustomDohUrl = input)
                }
            }.onFailure { err ->
                _customDohError.value = err.message ?: "Failed to validate custom DoH endpoint"
                _statusMessage.value = null
            }
        }
    }

    fun testAllLatencies() {
        if (_isTestingLatency.value) return
        viewModelScope.launch {
            _isTestingLatency.value = true
            _statusMessage.value = "Measuring DNS query latency..."
            val map = _latencies.value.toMutableMap()
            val providersToTest = directoryRepository.getRecommendedForRegion(_draftConfig.value.selectedRegion)

            for (provider in providersToTest) {
                try {
                    val health = directoryRepository.testProviderLatency(provider)
                    if (health.isReachable) {
                        map[provider.id] = health.latencyMs
                    } else {
                        map[provider.id] = -1L
                    }
                    _latencies.value = map.toMap()
                } catch (_: Exception) {
                    map[provider.id] = -1L
                }
            }
            _isTestingLatency.value = false
            _statusMessage.value = "DNS latency test completed"
        }
    }

    fun purchaseLifetime() {
        if (_isPurchasing.value) return
        viewModelScope.launch {
            _isPurchasing.value = true
            _statusMessage.value = "Processing lifetime upgrade..."
            val result = billingRepository.purchaseLifetime()
            result.onSuccess {
                _statusMessage.value = "Premium unlocked! Lifetime access activated."
            }.onFailure { err ->
                _statusMessage.value = err.message ?: "Purchase cancelled or unavailable."
            }
            _isPurchasing.value = false
        }
    }

    fun saveAndApply(onSaved: () -> Unit) {
        val config = _draftConfig.value.copy(isCustomConfig = true)
        viewModelScope.launch {
            settingsRepository.updateConfiguration(config)
            if (settingsRepository.configuration.value.isProtectionEnabled) {
                protectionController.updateConfiguration(config)
            }
            _statusMessage.value = "Settings saved & applied"
            onSaved()
        }
    }

    fun clearStatus() {
        _statusMessage.value = null
    }
}
