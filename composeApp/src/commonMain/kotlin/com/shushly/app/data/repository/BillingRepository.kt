package com.shushly.app.data.repository

import com.shushly.app.domain.model.PremiumEntitlement
import kotlinx.coroutines.flow.StateFlow

interface PlatformBillingHandler {
    suspend fun purchaseLifetime(): Result<PremiumEntitlement>
    suspend fun restorePurchases(): Result<PremiumEntitlement>
}

class BillingRepository(
    private val settingsRepository: SettingsRepository,
    private val platformBillingHandler: PlatformBillingHandler? = null
) {
    val entitlement: StateFlow<PremiumEntitlement> = settingsRepository.entitlement

    suspend fun purchaseLifetime(): Result<PremiumEntitlement> {
        val handler = platformBillingHandler
        if (handler != null) {
            val result = handler.purchaseLifetime()
            result.onSuccess { purchased ->
                settingsRepository.updateEntitlement(purchased)
            }
            return result
        }

        // Direct unlock simulation for desktop / local preview mode (verifiable in test harness)
        val entitlement = PremiumEntitlement(
            isPremium = true,
            purchaseToken = "shushly_lifetime_direct_${RandomUtil.generateToken()}",
            purchaseTimestamp = 1774000000000L, // Current era
            storeSource = "DirectLicense"
        )
        settingsRepository.updateEntitlement(entitlement)
        return Result.success(entitlement)
    }

    suspend fun restorePurchases(): Result<PremiumEntitlement> {
        val handler = platformBillingHandler
        if (handler != null) {
            val result = handler.restorePurchases()
            result.onSuccess { restored ->
                settingsRepository.updateEntitlement(restored)
            }
            return result
        }

        return Result.success(settingsRepository.entitlement.value)
    }

    private object RandomUtil {
        fun generateToken(): String {
            val chars = "0123456789abcdef"
            return (1..16).map { chars.random() }.joinToString("")
        }
    }
}
