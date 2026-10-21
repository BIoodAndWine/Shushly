package com.shushly.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ResolverHealth(
    val dohUrl: String,
    val isReachable: Boolean,
    val latencyMs: Long,
    val isFilteringActive: Boolean = true,
    val errorMessage: String? = null,
    val timestampMs: Long = 0L
)
