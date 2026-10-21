package com.shushly.app.domain.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.shushly.app.android.ShushlyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class AndroidSoundPlayer(private val context: Context? = null) : SoundPlayer {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val soundPool: SoundPool by lazy {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(attributes)
            .build()
    }

    private val soundIds = mutableMapOf<SoundType, Int>()

    init {
        scope.launch {
            preload()
        }
    }

    private fun getContext(): Context? {
        return context ?: ShushlyApplication.instance?.applicationContext
    }

    private fun preload() {
        val ctx = getContext() ?: return
        SoundType.entries.forEach { type ->
            try {
                val fileName = getFileName(type)
                val targetFile = File(ctx.cacheDir, "shushly_sfx_$fileName")
                if (!targetFile.exists() || targetFile.length() == 0L) {
                    val resourcePath = "composeResources/shushly.composeapp.generated.resources/files/sounds/$fileName"
                    val stream: InputStream? = javaClass.classLoader?.getResourceAsStream(resourcePath)
                        ?: Thread.currentThread().contextClassLoader?.getResourceAsStream(resourcePath)
                    stream?.use { input ->
                        FileOutputStream(targetFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                }
                if (targetFile.exists() && targetFile.length() > 0) {
                    val id = soundPool.load(targetFile.absolutePath, 1)
                    soundIds[type] = id
                }
            } catch (_: Throwable) {}
        }
    }

    override fun play(sound: SoundType) {
        val soundId = soundIds[sound]
        if (soundId != null && soundId != 0) {
            try {
                soundPool.play(soundId, 0.7f, 0.7f, 1, 0, 1.0f)
            } catch (_: Throwable) {}
        }
    }

    override fun release() {
        soundPool.release()
        soundIds.clear()
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

actual fun createPlatformSoundPlayer(): SoundPlayer = AndroidSoundPlayer()
