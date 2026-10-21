package com.shushly.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ProtectionLevel(
    val title: String,
    val subtitle: String,
    val dohUrl: String,
    val filterName: String
) {
    DEFAULT(
        title = "Default",
        subtitle = "Everyday ad & tracker blocking",
        dohUrl = "https://freedns.controld.com/p2",
        filterName = "Control D Ads & Tracking"
    ),
    BALANCED(
        title = "Balanced",
        subtitle = "Optimal protection & site compatibility",
        dohUrl = "https://freedns.controld.com/x-oisd",
        filterName = "Control D OISD Full"
    ),
    STRONG(
        title = "Strong",
        subtitle = "Strict filtering for maximum privacy",
        dohUrl = "https://freedns.controld.com/x-hagezi-pro",
        filterName = "Control D HaGeZi Pro"
    )
}
