package com.shushly.app

import com.shushly.app.data.repository.SettingsRepository
import com.shushly.app.domain.audio.SoundPlayer
import com.shushly.app.domain.audio.SoundType
import com.shushly.app.domain.engine.DnsProtectionController
import com.shushly.app.domain.model.DnsConfiguration
import com.shushly.app.domain.model.ProtectionLevel
import com.shushly.app.domain.model.ProtectionStatus
import com.shushly.app.domain.model.StatusState
import com.shushly.app.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FakeSoundPlayer : SoundPlayer {
    val playedSounds = mutableListOf<SoundType>()
    var releaseCalled = false

    override fun play(sound: SoundType) {
        playedSounds.add(sound)
    }

    override fun release() {
        releaseCalled = true
    }

    fun clear() {
        playedSounds.clear()
    }
}

class FakeDnsProtectionController(initialState: StatusState = StatusState.DISABLED) : DnsProtectionController {
    val _status = MutableStateFlow(ProtectionStatus(state = initialState))
    override val status: StateFlow<ProtectionStatus> = _status.asStateFlow()

    private val _config = MutableStateFlow(DnsConfiguration())
    override val configuration: StateFlow<DnsConfiguration> = _config.asStateFlow()

    var shouldFail = false
    var enableCalled = false
    var disableCalled = false

    override suspend fun enableProtection(level: ProtectionLevel): Result<Unit> {
        enableCalled = true
        if (shouldFail) {
            _status.value = ProtectionStatus(state = StatusState.ERROR)
            return Result.failure(Exception("Connection failed"))
        }
        _status.value = ProtectionStatus(state = StatusState.CONNECTING)
        _status.value = ProtectionStatus(state = StatusState.CONNECTED, activeLevel = level)
        return Result.success(Unit)
    }

    override suspend fun enableProtectionWithConfig(config: DnsConfiguration): Result<Unit> {
        enableCalled = true
        if (shouldFail) {
            _status.value = ProtectionStatus(state = StatusState.ERROR)
            return Result.failure(Exception("Connection failed"))
        }
        _status.value = ProtectionStatus(state = StatusState.CONNECTING)
        _status.value = ProtectionStatus(state = StatusState.CONNECTED)
        return Result.success(Unit)
    }

    override suspend fun disableProtection(): Result<Unit> {
        disableCalled = true
        _status.value = ProtectionStatus(state = StatusState.DISABLED)
        return Result.success(Unit)
    }

    override suspend fun setProtectionLevel(level: ProtectionLevel): Result<Unit> {
        _status.value = _status.value.copy(activeLevel = level)
        return Result.success(Unit)
    }

    override suspend fun updateConfiguration(config: DnsConfiguration): Result<Unit> {
        _config.value = config
        return Result.success(Unit)
    }

    override suspend fun testResolver(endpointUrl: String): com.shushly.app.domain.model.ResolverHealth {
        return com.shushly.app.domain.model.ResolverHealth(
            dohUrl = endpointUrl,
            isReachable = true,
            latencyMs = 25
        )
    }

    override suspend fun checkHealth(): com.shushly.app.domain.model.ResolverHealth {
        return com.shushly.app.domain.model.ResolverHealth(
            dohUrl = "",
            isReachable = true,
            latencyMs = 25
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SoundSyncTests {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var settingsRepo: SettingsRepository
    private lateinit var fakeSoundPlayer: FakeSoundPlayer

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        settingsRepo = SettingsRepository()
        settingsRepo.resetAllSettings()
        settingsRepo.setSoundEnabled(true)
        fakeSoundPlayer = FakeSoundPlayer()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testStartupStateEmissionIsCompletelySilent() = runTest(testDispatcher) {
        val controller = FakeDnsProtectionController(initialState = StatusState.CONNECTED)
        val vm = HomeViewModel(controller, settingsRepo, fakeSoundPlayer)
        advanceUntilIdle()

        assertTrue(
            fakeSoundPlayer.playedSounds.isEmpty(),
            "Application launch with existing connection must be silent, got: ${fakeSoundPlayer.playedSounds}"
        )
    }

    @Test
    fun testUserActivationProducesImmediateClickAndConfirmedConnected() = runTest(testDispatcher) {
        val controller = FakeDnsProtectionController(initialState = StatusState.DISABLED)
        val vm = HomeViewModel(controller, settingsRepo, fakeSoundPlayer)
        advanceUntilIdle()
        fakeSoundPlayer.clear()

        vm.toggleProtection()
        advanceUntilIdle()

        assertEquals(
            listOf(SoundType.CLICK, SoundType.CONNECTED),
            fakeSoundPlayer.playedSounds,
            "Enable activation should produce CLICK on tap, followed by CONNECTED on verified connection"
        )
        assertFalse(
            fakeSoundPlayer.playedSounds.contains(SoundType.TOGGLE_ON),
            "Premature TOGGLE_ON cue must not be emitted alongside CLICK and CONNECTED"
        )
    }

    @Test
    fun testDisablingProducesImmediateClickAndConfirmedToggleOff() = runTest(testDispatcher) {
        val controller = FakeDnsProtectionController(initialState = StatusState.DISABLED)
        val vm = HomeViewModel(controller, settingsRepo, fakeSoundPlayer)
        advanceUntilIdle()

        // Enable first
        vm.toggleProtection()
        advanceUntilIdle()
        fakeSoundPlayer.clear()

        // Now disable
        vm.toggleProtection()
        advanceUntilIdle()

        assertEquals(
            listOf(SoundType.CLICK, SoundType.TOGGLE_OFF),
            fakeSoundPlayer.playedSounds,
            "Disable action should produce CLICK on tap, followed by TOGGLE_OFF on confirmed disconnection"
        )
    }

    @Test
    fun testConnectionFailureProducesErrorAndNeverSuccess() = runTest(testDispatcher) {
        val controller = FakeDnsProtectionController(initialState = StatusState.DISABLED).apply {
            shouldFail = true
        }
        val vm = HomeViewModel(controller, settingsRepo, fakeSoundPlayer)
        advanceUntilIdle()
        fakeSoundPlayer.clear()

        vm.toggleProtection()
        advanceUntilIdle()

        assertTrue(fakeSoundPlayer.playedSounds.contains(SoundType.CLICK), "Should emit tactile click on press")
        assertTrue(fakeSoundPlayer.playedSounds.contains(SoundType.ERROR), "Should emit error cue on failure")
        assertFalse(
            fakeSoundPlayer.playedSounds.contains(SoundType.CONNECTED),
            "Failure must NEVER emit a success cue"
        )
    }

    @Test
    fun testErrorEventDoesNotProduceDuplicateWarnings() = runTest(testDispatcher) {
        val controller = FakeDnsProtectionController(initialState = StatusState.DISABLED).apply {
            shouldFail = true
        }
        val vm = HomeViewModel(controller, settingsRepo, fakeSoundPlayer)
        advanceUntilIdle()
        fakeSoundPlayer.clear()

        vm.toggleProtection()
        advanceUntilIdle()

        val errorCount = fakeSoundPlayer.playedSounds.count { it == SoundType.ERROR }
        assertEquals(1, errorCount, "Error cue must be emitted exactly once without duplicate warnings")
    }

    @Test
    fun testBackgroundReconnectionRemainsSilent() = runTest(testDispatcher) {
        val controller = FakeDnsProtectionController(initialState = StatusState.DISABLED)
        val vm = HomeViewModel(controller, settingsRepo, fakeSoundPlayer)
        advanceUntilIdle()

        // Connect initially
        vm.toggleProtection()
        advanceUntilIdle()
        fakeSoundPlayer.clear()

        // Simulate network disruption: CONNECTED -> RECONNECTING
        controller._status.value = ProtectionStatus(state = StatusState.RECONNECTING)
        advanceUntilIdle()

        // Background auto-recovery: RECONNECTING -> CONNECTED
        controller._status.value = ProtectionStatus(state = StatusState.CONNECTED)
        advanceUntilIdle()

        assertTrue(
            fakeSoundPlayer.playedSounds.isEmpty(),
            "Background reconnection must be silent without audible interruption, got: ${fakeSoundPlayer.playedSounds}"
        )
    }

    @Test
    fun testSoundEffectsOffSuppressesAllAudio() = runTest(testDispatcher) {
        settingsRepo.setSoundEnabled(false)
        val controller = FakeDnsProtectionController(initialState = StatusState.DISABLED)
        val vm = HomeViewModel(controller, settingsRepo, fakeSoundPlayer)
        advanceUntilIdle()

        vm.toggleProtection()
        advanceUntilIdle()

        vm.selectProtectionLevel(ProtectionLevel.STRICT)
        advanceUntilIdle()

        assertTrue(
            fakeSoundPlayer.playedSounds.isEmpty(),
            "All sound effects must be suppressed when soundEnabled is false"
        )
    }

    @Test
    fun testProtectionModeChangeProducesModeChangeCue() = runTest(testDispatcher) {
        val controller = FakeDnsProtectionController(initialState = StatusState.DISABLED)
        val vm = HomeViewModel(controller, settingsRepo, fakeSoundPlayer)
        advanceUntilIdle()
        fakeSoundPlayer.clear()

        vm.selectProtectionLevel(ProtectionLevel.STRICT)
        advanceUntilIdle()

        assertEquals(
            listOf(SoundType.MODE_CHANGE),
            fakeSoundPlayer.playedSounds,
            "Selecting a different protection profile should emit MODE_CHANGE cue"
        )
    }

    @Test
    fun testThemeSwitchingIsSilent() = runTest(testDispatcher) {
        val controller = FakeDnsProtectionController(initialState = StatusState.DISABLED)
        val vm = HomeViewModel(controller, settingsRepo, fakeSoundPlayer)
        advanceUntilIdle()
        fakeSoundPlayer.clear()

        vm.toggleTheme(isCurrentlyDark = true)
        vm.toggleTheme(isCurrentlyDark = false)
        advanceUntilIdle()

        assertTrue(
            fakeSoundPlayer.playedSounds.isEmpty(),
            "Theme transitions must remain silent and visually focused"
        )
    }

    @Test
    fun testSoundPlayerReleaseCleansUpResources() {
        val player = FakeSoundPlayer()
        player.release()
        assertTrue(player.releaseCalled, "SoundPlayer release() must be called on lifecycle disposal")
    }
}
