package com.shushly.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class Region(
    val code: String,
    val displayName: String,
    val startingPrimaryProviderId: String = "controld-oisd",
    val startingSecondaryProviderId: String = "adguard"
) {
    NA("NA", "North America"),
    EUW("EUW", "Western Europe"),
    EUNE("EUNE", "Northern / Eastern Europe"),
    SEA("SEA", "Southeast Asia"),
    EA("EA", "East Asia"),
    OCE("OCE", "Oceania"),
    SA("SA", "South America"),
    SOUTH_ASIA("South Asia", "South Asia");

    companion object {
        fun fromCode(code: String?): Region {
            if (code == null) return NA
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: NA
        }
    }
}
