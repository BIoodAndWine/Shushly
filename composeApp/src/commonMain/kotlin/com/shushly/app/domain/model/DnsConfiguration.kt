package com.shushly.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DnsConfiguration(
    val isProtectionEnabled: Boolean = false,
    val protectionLevel: ProtectionLevel = ProtectionLevel.BALANCED,
    val isCustomConfig: Boolean = false,
    val primaryProviderId: String = "controld-oisd",
    val primaryCustomDohUrl: String? = null,
    val secondaryProviderId: String = "adguard",
    val secondaryCustomDohUrl: String? = null,
    val selectedRegion: Region = Region.NA,
    val autoFailover: Boolean = true,
    val autoReconnect: Boolean = true,
    val startOnBoot: Boolean = false
) {
    fun getEffectivePrimaryDohUrl(builtInProviders: List<DnsProvider>): String {
        if (isCustomConfig && !primaryCustomDohUrl.isNullOrBlank()) {
            return primaryCustomDohUrl.trim()
        }
        if (isCustomConfig) {
            val provider = builtInProviders.firstOrNull { it.id == primaryProviderId }
            if (provider != null) return provider.dohUrl
        }
        return protectionLevel.dohUrl
    }

    fun getEffectiveSecondaryDohUrl(builtInProviders: List<DnsProvider>): String? {
        if (!isCustomConfig) return null
        if (!secondaryCustomDohUrl.isNullOrBlank()) {
            return secondaryCustomDohUrl.trim()
        }
        return builtInProviders.firstOrNull { it.id == secondaryProviderId }?.dohUrl
    }
}
