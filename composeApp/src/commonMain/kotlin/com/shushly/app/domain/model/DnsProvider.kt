package com.shushly.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DnsProvider(
    val id: String,
    val name: String,
    val filterName: String,
    val region: Region,
    val dohUrl: String,
    val description: String,
    val websiteUrl: String,
    val privacyPolicyUrl: String,
    val isVerified: Boolean = true,
    val isBuiltIn: Boolean = true
)
