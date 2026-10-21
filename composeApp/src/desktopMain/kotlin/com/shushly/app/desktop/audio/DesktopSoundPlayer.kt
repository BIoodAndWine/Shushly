package com.shushly.app.domain.audio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.sound.sampled.AudioSystem

class DesktopSoundPlayer : SoundPlayer {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val cachedBuffers = mutableMapOf<SoundType, ByteArray>()

    init {
        preloadAll()
    }

    private fun preloadAll() {
        scope.launch {
            SoundType.entries.forEach { sound ->
                loadBuffer(sound)
            }
        }
    }

    private fun loadBuffer(sound: SoundType): ByteArray? {
        cachedBuffers[sound]?.let { return it }
        val fileName = getFileName(sound)
        val resourcePath = "composeResources/shushly.composeapp.generated.resources/files/sounds/$fileName"
        val stream: InputStream? = javaClass.classLoader?.getResourceAsStream(resourcePath)
            ?: Thread.currentThread().contextClassLoader.getResourceAsStream(resourcePath)
        val bytes = stream?.use { it.readBytes() }
        if (bytes != null) {
            cachedBuffers[sound] = bytes
        }
        return bytes
    }

    override fun play(sound: SoundType) {
        scope.launch {
            try {
                val bytes = loadBuffer(sound) ?: return@launch
                val audioStream = AudioSystem.getAudioInputStream(ByteArrayInputStream(bytes))
                val clip = AudioSystem.getClip()
                clip.open(audioStream)
                clip.start()
            } catch (_: Throwable) {
                // Audio device unavailable or non-critical error
            }
        }
    }

    override fun release() {
        cachedBuffers.clear()
    }

    private fun getFileName(type: SoundType): String = when (type) {
        SoundType.TOGGLE_ON -> "toggle_on.wav"
        SoundType.TOGGLE_OFF -> "toggle_off.wav"
        SoundType.MODE_CHANGE -> "mode_change.wav"
        SoundType.CONNECTED -> "connected.wav"
        SoundType.ERROR -> "error.wav"
        SoundType.CLICK -> "click.wav"
    }
}

actual fun createPlatformSoundPlayer(): SoundPlayer = DesktopSoundPlayer()
