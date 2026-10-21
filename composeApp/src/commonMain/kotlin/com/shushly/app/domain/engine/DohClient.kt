package com.shushly.app.domain.engine

import com.shushly.app.domain.model.ResolverHealth
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.TimeSource

class DohClient(
    private val httpClient: HttpClient = HttpClient()
) {
    /**
     * Executes an RFC 8484 DNS-over-HTTPS POST request with binary wire-format payload.
     */
    suspend fun queryRaw(dohUrl: String, queryBytes: ByteArray): Result<ByteArray> = withContext(Dispatchers.Default) {
        try {
            val response = httpClient.post(dohUrl) {
                contentType(ContentType("application", "dns-message"))
                header("Accept", "application/dns-message")
                setBody(queryBytes)
            }

            if (response.status == HttpStatusCode.OK) {
                val bytes = response.bodyAsBytes()
                Result.success(bytes)
            } else {
                Result.failure(Exception("DoH server responded with HTTP ${response.status.value}: ${response.status.description}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Resolves a domain via DoH and parses the response.
     */
    suspend fun resolve(dohUrl: String, domain: String, qType: Int = 1): Result<DnsResponseSummary> {
        val query = DnsPacketParser.buildQuery(domain, qType)
        return queryRaw(dohUrl, query).mapCatching { respBytes ->
            DnsPacketParser.parseResponse(respBytes)
                ?: throw Exception("Failed to parse DNS response packet from $dohUrl")
        }
    }

    /**
     * Performs an accurate latency and ad-blocking verification check against a DoH endpoint.
     * Takes multiple timed samples, uses median latency, and verifies filtering against an ad domain.
     */
    suspend fun checkHealth(dohUrl: String): ResolverHealth = withContext(Dispatchers.Default) {
        val timeSource = TimeSource.Monotonic
        val latencies = mutableListOf<Long>()
        var isFiltering = false
        var reachable = false
        var lastError: String? = null

        // 1. Warm-up request on a neutral domain (e.g. example.com)
        val warmupQuery = DnsPacketParser.buildQuery("example.com")
        val warmupResult = queryRaw(dohUrl, warmupQuery)
        if (warmupResult.isSuccess) {
            reachable = true
        } else {
            lastError = warmupResult.exceptionOrNull()?.message
        }

        // 2. Measure latency with 3 consecutive timed queries
        val testDomains = listOf("cloudflare.com", "wikipedia.org", "dns.google")
        for (domain in testDomains) {
            val query = DnsPacketParser.buildQuery(domain)
            val mark = timeSource.markNow()
            val res = queryRaw(dohUrl, query)
            val elapsed = mark.elapsedNow().inWholeMilliseconds
            if (res.isSuccess) {
                reachable = true
                latencies.add(elapsed)
            } else {
                lastError = res.exceptionOrNull()?.message
            }
        }

        // 3. Test filtering behavior on a known ad domain: pagead2.googlesyndication.com
        val adQuery = DnsPacketParser.buildQuery("pagead2.googlesyndication.com")
        val adRes = queryRaw(dohUrl, adQuery)
        if (adRes.isSuccess) {
            val parsed = DnsPacketParser.parseResponse(adRes.getOrThrow())
            if (parsed != null && parsed.isBlocked) {
                isFiltering = true
            }
        }

        val medianLatency = if (latencies.isNotEmpty()) {
            latencies.sorted()[latencies.size / 2]
        } else {
            -1L
        }

        ResolverHealth(
            dohUrl = dohUrl,
            isReachable = reachable,
            latencyMs = medianLatency,
            isFilteringActive = isFiltering,
            errorMessage = if (reachable) null else (lastError ?: "Resolver unreachable"),
            timestampMs = 0L // Can be populated with current epoch if needed
        )
    }
}
