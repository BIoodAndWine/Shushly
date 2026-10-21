package com.shushly.app.domain.engine

import com.shushly.app.domain.model.DnsConfiguration
import com.shushly.app.domain.model.ProtectionLevel
import com.shushly.app.domain.model.ProtectionStatus
import com.shushly.app.domain.model.ResolverHealth
import kotlinx.coroutines.flow.StateFlow

interface DnsProtectionController {
    /**
     * Observable protection status representing real system engine state.
     */
    val status: StateFlow<ProtectionStatus>

    /**
     * Currently active DNS configuration.
     */
    val configuration: StateFlow<DnsConfiguration>

    /**
     * Enables system-level DNS protection with the specified protection level.
     */
    suspend fun enableProtection(level: ProtectionLevel): Result<Unit>

    /**
     * Enables protection with a full custom configuration (Premium).
     */
    suspend fun enableProtectionWithConfig(config: DnsConfiguration): Result<Unit>

    /**
     * Disables DNS protection and restores previous network configuration.
     */
    suspend fun disableProtection(): Result<Unit>

    /**
     * Switches protection level while active.
     */
    suspend fun setProtectionLevel(level: ProtectionLevel): Result<Unit>

    /**
     * Updates configuration without interrupting if already connected.
     */
    suspend fun updateConfiguration(config: DnsConfiguration): Result<Unit>

    /**
     * Tests reachability and latency of a DoH resolver endpoint.
     */
    suspend fun testResolver(endpointUrl: String): ResolverHealth

    /**
     * Re-verifies current connection health.
     */
    suspend fun checkHealth(): ResolverHealth
}
