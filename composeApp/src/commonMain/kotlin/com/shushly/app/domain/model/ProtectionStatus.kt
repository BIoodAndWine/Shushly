package com.shushly.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class StatusState {
    DISABLED,
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    ERROR
}

@Serializable
data class ProtectionStatus(
    val state: StatusState = StatusState.DISABLED,
    val activeLevel: ProtectionLevel = ProtectionLevel.LIGHT,
    val activeResolverName: String = ProtectionLevel.LIGHT.filterName,
    val activeResolverEndpoint: String = ProtectionLevel.LIGHT.dohUrl,
    val isCustomResolver: Boolean = false,
    val errorMessage: String? = null,
    val connectedSinceEpochMs: Long? = null,
    val latencyMs: Long? = null
) {
    val isProtected: Boolean
        get() = state == StatusState.CONNECTED

    val isTransitioning: Boolean
        get() = state == StatusState.CONNECTING || state == StatusState.RECONNECTING

    val displayText: String
        get() = when (state) {
            StatusState.DISABLED -> "Protection is Off"
            StatusState.CONNECTING -> "Connecting…"
            StatusState.CONNECTED -> "You're Protected"
            StatusState.RECONNECTING -> "Reconnecting…"
            StatusState.ERROR -> "Connection Failed"
        }

    val displaySubtitle: String
        get() = when (state) {
            StatusState.DISABLED -> "Turn on to block ads and trackers"
            StatusState.CONNECTING -> "Setting up your protection"
            StatusState.CONNECTED -> "Ads and trackers are being blocked"
            StatusState.RECONNECTING -> "Restoring protection"
            StatusState.ERROR -> errorMessage ?: "Tap to try again"
        }
}
