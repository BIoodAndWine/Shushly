package com.shushly.app.domain.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.shushly.app.android.ShushlyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap

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
            .build().apply {
                setOnLoadCompleteListener { _, sampleId, status ->
                    if (status == 0) {
                        readySampleIds.add(sampleId)
                        // Check if this sample had a pending play request
                        pendingPlays.entries.find { it.value == sampleId }?.let { entry ->
                            pendingPlays.remove(entry.key)
                            playSample(sampleId)
                        }
                    }
                }
            }
    }

    private val soundIds = ConcurrentHashMap<SoundType, Int>()
    private val readySampleIds = ConcurrentHashMap.newKeySet<Int>()
    private val pendingPlays = ConcurrentHashMap<SoundType, Int>()

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
                    
                    // Attempt asset loading first (primary Compose Multiplatform asset packaging on Android)
                    var stream: InputStream? = null
                    try {
                        stream = ctx.assets.open(resourcePath)
                    } catch (_: Throwable) {
                        // Fallback to ClassLoader stream
                        stream = javaClass.classLoader?.getResourceAsStream(resourcePath)
                            ?: Thread.currentThread().contextClassLoader?.getResourceAsStream(resourcePath)
                    }

                    stream?.use { input ->
                        FileOutputStream(targetFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                }

                if (targetFile.exists() && targetFile.length() > 0) {
                    val sampleId = soundPool.load(targetFile.absolutePath, 1)
                    soundIds[type] = sampleId
                }
            } catch (_: Throwable) {
                // Non-critical audio resource preload failure
            }
        }
    }

    override fun play(sound: SoundType) {
        // Subtle haptic response immediately on interaction
        if (sound == SoundType.CLICK || sound == SoundType.TOGGLE_OFF || sound == SoundType.MODE_CHANGE) {
            triggerHaptic()
        }

        val ctx = getContext() ?: return
        val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        if (audioManager != null) {
            // Strictly respect Android Silent and Vibrate modes
            if (audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT ||
                audioManager.ringerMode == AudioManager.RINGER_MODE_VIBRATE
            ) {
                return
            }
        }

        val sampleId = soundIds[sound]
        if (sampleId != null && sampleId != 0) {
            if (readySampleIds.contains(sampleId)) {
                playSample(sampleId)
            } else {
                // Queue for playback as soon as SoundPool onLoadComplete fires
                pendingPlays[sound] = sampleId
            }
        }
    }

    private fun playSample(sampleId: Int) {
        try {
            soundPool.play(sampleId, 0.7f, 0.7f, 1, 0, 1.0f)
        } catch (_: Throwable) {
            // Audio line unavailable
        }
    }

    private fun triggerHaptic() {
        try {
            val ctx = getContext() ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = ctx.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(15)
                }
            }
        } catch (_: Throwable) {
            // Haptics unavailable or permission denied
        }
    }

    override fun release() {
        try {
            soundPool.release()
        } catch (_: Throwable) {}
        soundIds.clear()
        readySampleIds.clear()
        pendingPlays.clear()
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

actual fun createPlatformSoundPlayer(): SoundPlayer = AndroidSoundPlayer()
