package com.shushly.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shushly.app.data.repository.SettingsRepository
import com.shushly.app.domain.audio.SoundPlayer
import com.shushly.app.domain.audio.SoundType
import com.shushly.app.domain.engine.DnsProtectionController
import com.shushly.app.domain.model.DnsConfiguration
import com.shushly.app.domain.model.ProtectionLevel
import com.shushly.app.domain.model.ProtectionStatus
import com.shushly.app.domain.model.StatusState
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val status: ProtectionStatus = ProtectionStatus(),
    val configuration: DnsConfiguration = DnsConfiguration(),
    val isBusy: Boolean = false,
    val feedbackMessage: String? = null
)

class HomeViewModel(
    private val protectionController: DnsProtectionController,
    private val settingsRepository: SettingsRepository,
    private val soundPlayer: SoundPlayer? = null
) : ViewModel() {

    private val _isBusy = MutableStateFlow(false)
    private val _feedbackMessage = MutableStateFlow<String?>(null)
    private var lastObservedState = StatusState.DISABLED
    private var isInitialized = false

    private var lastSoundPlayed: SoundType? = null
    private var lastSoundMark: TimeMark? = null

    val uiState: StateFlow<HomeUiState> = combine(
        protectionController.status,
        settingsRepository.configuration,
        _isBusy,
        _feedbackMessage
    ) { status, config, busy, feedback ->
        HomeUiState(
            status = status,
            configuration = config,
            isBusy = busy,
            feedbackMessage = feedback
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    init {
        // Observe status transitions to trigger verified state-change audio cues
        viewModelScope.launch {
            protectionController.status.collect { status ->
                val current = status.state

                // Ignore startup emission to keep application initialization completely silent
                if (!isInitialized) {
                    isInitialized = true
                    lastObservedState = current
                    return@collect
                }

                val prev = lastObservedState
                lastObservedState = current

                if (prev != current) {
                    when (current) {
                        StatusState.CONNECTED -> {
                            // Only play confirmation chime when transitioning from CONNECTING or DISABLED (user action),
                            // keeping automatic background reconnections (from RECONNECTING) silent.
                            if (prev != StatusState.RECONNECTING) {
                                playSound(SoundType.CONNECTED)
                            }
                        }
                        StatusState.DISABLED -> {
                            // Gentle disconnect sound only after confirmed disconnection
                            if (prev == StatusState.CONNECTED || prev == StatusState.CONNECTING) {
                                playSound(SoundType.TOGGLE_OFF)
                            }
                        }
                        StatusState.ERROR -> {
                            // Exactly one distinct error cue when failure is confirmed
                            if (prev != StatusState.ERROR) {
                                playSound(SoundType.ERROR)
                            }
                        }
                        StatusState.CONNECTING, StatusState.RECONNECTING -> {
                            // Silent status transitions during connection negotiation and auto-recovery
                        }
                    }
                }
            }
        }
    }

    private fun playSound(sound: SoundType) {
        if (!settingsRepository.soundEnabled.value) return

        // Debounce rapid identical sound cues within 80ms
        val now = TimeSource.Monotonic.markNow()
        val prevMark = lastSoundMark
        if (sound == lastSoundPlayed && prevMark != null && prevMark.elapsedNow().inWholeMilliseconds < 80L) {
            return
        }

        lastSoundMark = now
        lastSoundPlayed = sound
        soundPlayer?.play(sound)
    }

    fun toggleProtection() {
        if (_isBusy.value) return
        val currentStatus = protectionController.status.value

        // Immediate tactile press feedback on physical button press
        playSound(SoundType.CLICK)

        viewModelScope.launch {
            _isBusy.value = true
            _feedbackMessage.value = null
            try {
                if (currentStatus.state == StatusState.CONNECTED || currentStatus.state == StatusState.CONNECTING) {
                    protectionController.disableProtection()
                    settingsRepository.setProtectionEnabled(false)
                } else {
                    val config = settingsRepository.configuration.value
                    if (config.isCustomConfig) {
                        protectionController.enableProtectionWithConfig(config)
                    } else {
                        protectionController.enableProtection(config.protectionLevel)
                    }
                    settingsRepository.setProtectionEnabled(true)
                }
            } catch (e: Exception) {
                _feedbackMessage.value = e.message ?: "Failed to change protection state"
                // Only play error sound if controller didn't transition to ERROR state (prevents duplicate cues)
                if (protectionController.status.value.state != StatusState.ERROR) {
                    playSound(SoundType.ERROR)
                }
            } finally {
                _isBusy.value = false
            }
        }
    }

    fun selectProtectionLevel(level: ProtectionLevel) {
        val currentConfig = settingsRepository.configuration.value
        if (currentConfig.protectionLevel == level && !currentConfig.isCustomConfig) return

        // Crisp selection tick when accepted
        playSound(SoundType.MODE_CHANGE)
        viewModelScope.launch {
            settingsRepository.setProtectionLevel(level)
            if (protectionController.status.value.state == StatusState.CONNECTED) {
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

    val themeMode: StateFlow<String> = settingsRepository.themeMode

    fun toggleTheme(isCurrentlyDark: Boolean) {
        // Theme switching remains silent and visually fluid
        val next = if (isCurrentlyDark) "light" else "dark"
        settingsRepository.setThemeMode(next)
    }
}
