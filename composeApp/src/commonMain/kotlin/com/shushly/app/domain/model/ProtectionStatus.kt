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
    val activeLevel: ProtectionLevel = ProtectionLevel.BALANCED,
    val activeResolverName: String = ProtectionLevel.BALANCED.filterName,
    val activeResolverEndpoint: String = ProtectionLevel.BALANCED.dohUrl,
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
            StatusState.CONNECTING -> "Connecting..."
            StatusState.CONNECTED -> "Protection is On"
            StatusState.RECONNECTING -> "Reconnecting..."
            StatusState.ERROR -> "Couldn't connect. Try again."
        }

    val displaySubtitle: String
        get() = when (state) {
            StatusState.DISABLED -> "Tap below to block ads and trackers"
            StatusState.CONNECTING -> "Securing your connection..."
            StatusState.CONNECTED -> "Ads and trackers are being blocked"
            StatusState.RECONNECTING -> "Network changed. Resuming protection..."
            StatusState.ERROR -> errorMessage ?: "Connection failed. Please check network settings."
        }
}
