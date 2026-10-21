package com.shushly.app.domain.audio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.LineEvent

class DesktopSoundPlayer : SoundPlayer {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val cachedBuffers = ConcurrentHashMap<SoundType, ByteArray>()
    private val cachedClips = ConcurrentHashMap<SoundType, Clip>()

    init {
        preloadAll()
    }

    private fun preloadAll() {
        scope.launch {
            SoundType.entries.forEach { sound ->
                try {
                    val bytes = loadBuffer(sound) ?: return@forEach
                    val audioStream = AudioSystem.getAudioInputStream(ByteArrayInputStream(bytes))
                    val clip = AudioSystem.getClip()
                    clip.open(audioStream)
                    cachedClips[sound] = clip
                } catch (_: Throwable) {
                    // Audio device unavailable or headless execution
                }
            }
        }
    }

    private fun loadBuffer(sound: SoundType): ByteArray? {
        cachedBuffers[sound]?.let { return it }
        val fileName = getFileName(sound)
        val resourcePath = "composeResources/shushly.composeapp.generated.resources/files/sounds/$fileName"
        val stream: InputStream? = javaClass.classLoader?.getResourceAsStream(resourcePath)
            ?: Thread.currentThread().contextClassLoader?.getResourceAsStream(resourcePath)
        val bytes = stream?.use { it.readBytes() }
        if (bytes != null) {
            cachedBuffers[sound] = bytes
        }
        return bytes
    }

    override fun play(sound: SoundType) {
        scope.launch {
            try {
                val clip = cachedClips[sound]
                if (clip != null && clip.isOpen) {
                    // Instant replay with reusable clip (zero line leak, near 0ms latency)
                    if (clip.isRunning) {
                        clip.stop()
                    }
                    clip.framePosition = 0
                    clip.start()
                    return@launch
                }

                // Fallback: Dynamically open clip with automatic lifecycle listener to close on stop
                val bytes = loadBuffer(sound) ?: return@launch
                val audioStream = AudioSystem.getAudioInputStream(ByteArrayInputStream(bytes))
                val fallbackClip = AudioSystem.getClip()
                fallbackClip.addLineListener { event ->
                    if (event.type == LineEvent.Type.STOP) {
                        try {
                            fallbackClip.close()
                        } catch (_: Throwable) {}
                    }
                }
                fallbackClip.open(audioStream)
                fallbackClip.start()
            } catch (_: Throwable) {
                // Audio device unavailable or non-critical error
            }
        }
    }

    override fun release() {
        cachedClips.forEach { (_, clip) ->
            try {
                if (clip.isOpen) {
                    clip.stop()
                    clip.close()
                }
            } catch (_: Throwable) {}
        }
        cachedClips.clear()
        cachedBuffers.clear()
    }

    private fun getFileName(type: SoundType): String = when (type) {
        SoundType.CLICK -> "click.wav"
        SoundType.CONNECTED -> "connected.wav"
        SoundType.TOGGLE_OFF -> "toggle_off.wav"
        SoundType.TOGGLE_ON -> "toggle_on.wav"
        SoundType.MODE_CHANGE -> "mode_change.wav"
        SoundType.ERROR -> "error.wav"
    }
}

actual fun createPlatformSoundPlayer(): SoundPlayer = DesktopSoundPlayer()
