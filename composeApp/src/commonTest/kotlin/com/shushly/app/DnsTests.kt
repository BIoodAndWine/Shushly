package com.shushly.app

import com.shushly.app.data.repository.DnsDirectoryRepository
import com.shushly.app.domain.engine.DnsPacketParser
import com.shushly.app.domain.engine.DohClient
import com.shushly.app.domain.model.DnsConfiguration
import com.shushly.app.domain.model.ProtectionLevel
import com.shushly.app.domain.model.Region
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DnsTests {

    @Test
    fun testProtectionLevelsEndpointsMatchSpecification() {
        assertEquals("https://freedns.controld.com/p2", ProtectionLevel.DEFAULT.dohUrl)
        assertEquals("https://freedns.controld.com/x-oisd", ProtectionLevel.BALANCED.dohUrl)
        assertEquals("https://freedns.controld.com/x-hagezi-pro", ProtectionLevel.STRONG.dohUrl)
    }

    @Test
    fun testDnsPacketParserBuildAndParseQuery() {
        val domain = "example.com"
        val queryBytes = DnsPacketParser.buildQuery(domain, qType = 1, id = 0x1234)

        assertTrue(queryBytes.size >= 12, "DNS query wireformat must be at least 12 bytes")
        val parsed = DnsPacketParser.parseQuestion(queryBytes)

        assertNotNull(parsed)
        assertEquals("example.com", parsed.name)
        assertEquals(1, parsed.qType)
        assertEquals(1, parsed.qClass)
    }

    @Test
    fun testDnsPacketParserHandlesMalformedInputGracefully() {
        val emptyBytes = ByteArray(0)
        assertEquals(null, DnsPacketParser.parseQuestion(emptyBytes))

        val shortBytes = ByteArray(8)
        assertEquals(null, DnsPacketParser.parseQuestion(shortBytes))

        val garbage = ByteArray(50) { 0xFF.toByte() }
        assertEquals(null, DnsPacketParser.parseQuestion(garbage))
    }

    @Test
    fun testDnsPacketParserIdentifiesBlockedResponse() {
        // Mock a DNS response with rcode 3 (NXDOMAIN)
        val nxdomainHeader = ByteArray(12)
        nxdomainHeader[0] = 0x12
        nxdomainHeader[1] = 0x34
        nxdomainHeader[2] = 0x81.toByte() // QR = 1, RD = 1
        nxdomainHeader[3] = 0x83.toByte() // RA = 1, RCODE = 3 (NXDOMAIN)

        val res = DnsPacketParser.parseResponse(nxdomainHeader)
        assertNotNull(res)
        assertEquals(3, res.rcode)
        assertTrue(res.isBlocked, "NXDOMAIN response must be flagged as blocked")
    }

    @Test
    fun testDnsDirectoryRegionalRecommendations() {
        val repo = DnsDirectoryRepository()
        val seaProviders = repo.getRecommendedForRegion(Region.SEA)
        assertTrue(seaProviders.any { it.region == Region.SEA }, "SEA region should suggest SEA provider")

        val euwProviders = repo.getRecommendedForRegion(Region.EUW)
        assertTrue(euwProviders.any { it.region == Region.EUW }, "EUW region should suggest EUW provider")
    }

    @Test
    fun testCustomDohValidationRequiresHttps() = runTest {
        val repo = DnsDirectoryRepository()
        val httpResult = repo.validateCustomDohUrl("http://insecure-dns.com/dns-query")
        assertTrue(httpResult.isFailure, "HTTP (unencrypted) endpoints must be rejected")
    }

    @Test
    fun testDnsConfigurationEffectiveEndpoints() {
        val repo = DnsDirectoryRepository()
        val config = DnsConfiguration(
            isProtectionEnabled = true,
            protectionLevel = ProtectionLevel.STRONG,
            isCustomConfig = false
        )
        assertEquals(ProtectionLevel.STRONG.dohUrl, config.getEffectivePrimaryDohUrl(repo.providers))

        val customConfig = DnsConfiguration(
            isProtectionEnabled = true,
            isCustomConfig = true,
            primaryCustomDohUrl = "https://custom.dns.org/dns-query"
        )
        assertEquals("https://custom.dns.org/dns-query", customConfig.getEffectivePrimaryDohUrl(repo.providers))
    }

    @Test
    fun testLiveDohResolutionOnBalancedEndpoint() = runTest {
        val client = DohClient()
        val endpoint = ProtectionLevel.BALANCED.dohUrl // Control D OISD

        val res = client.resolve(endpoint, "example.com")
        assertTrue(res.isSuccess, "Live DoH query to $endpoint must succeed: ${res.exceptionOrNull()?.message}")
        val summary = res.getOrThrow()
        assertEquals(0, summary.rcode, "example.com should resolve with NOERROR (0)")
        assertTrue(summary.answerIps.isNotEmpty(), "example.com should return resolved IP addresses")
    }

    @Test
    fun testLiveDohAdBlockingVerification() = runTest {
        val client = DohClient()
        val endpoint = ProtectionLevel.BALANCED.dohUrl // Control D OISD

        val health = client.checkHealth(endpoint)
        assertTrue(health.isReachable, "Balanced endpoint should be reachable")
        assertTrue(health.latencyMs > 0, "Measured latency should be positive: ${health.latencyMs} ms")
        assertTrue(health.isFilteringActive, "Ad filtering should be active on OISD endpoint")
    }
}
