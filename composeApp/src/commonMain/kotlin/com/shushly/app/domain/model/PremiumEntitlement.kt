package com.shushly.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PremiumEntitlement(
    val isPremium: Boolean = false,
    val purchaseToken: String? = null,
    val purchaseTimestamp: Long? = null,
    val storeSource: String = "none"
) {
    companion object {
        val FREE_DEFAULT = PremiumEntitlement(
            isPremium = false,
            purchaseToken = null,
            purchaseTimestamp = null,
            storeSource = "none"
        )
    }
}
