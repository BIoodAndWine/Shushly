package com.shushly.app.domain.audio

enum class SoundType {
    TOGGLE_ON,
    TOGGLE_OFF,
    MODE_CHANGE,
    CONNECTED,
    ERROR,
    CLICK
}

interface SoundPlayer {
    fun play(sound: SoundType)
    fun release() {}
}

expect fun createPlatformSoundPlayer(): SoundPlayer
