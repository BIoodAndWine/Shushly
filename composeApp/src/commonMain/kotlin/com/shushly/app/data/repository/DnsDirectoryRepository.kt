package com.shushly.app.data.repository

import com.shushly.app.domain.engine.DohClient
import com.shushly.app.domain.model.DnsProvider
import com.shushly.app.domain.model.Region
import com.shushly.app.domain.model.ResolverHealth

class DnsDirectoryRepository(
    private val dohClient: DohClient = DohClient()
) {
    val providers: List<DnsProvider> = listOf(
        DnsProvider(
            id = "controld-p2",
            name = "Control D",
            filterName = "Ads & Tracking",
            region = Region.NA,
            dohUrl = "https://freedns.controld.com/p2",
            description = "Standard ad and tracker blocking without breaking website functionality.",
            websiteUrl = "https://controld.com",
            privacyPolicyUrl = "https://controld.com/privacy"
        ),
        DnsProvider(
            id = "controld-oisd",
            name = "Control D",
            filterName = "OISD Full",
            region = Region.NA,
            dohUrl = "https://freedns.controld.com/x-oisd",
            description = "Comprehensive ad and tracking protection balanced with excellent site compatibility.",
            websiteUrl = "https://controld.com",
            privacyPolicyUrl = "https://controld.com/privacy"
        ),
        DnsProvider(
            id = "controld-hagezi-pro",
            name = "Control D",
            filterName = "HaGeZi Pro",
            region = Region.NA,
            dohUrl = "https://freedns.controld.com/x-hagezi-pro",
            description = "Strict protection filter for maximum privacy and ad blocking.",
            websiteUrl = "https://controld.com",
            privacyPolicyUrl = "https://controld.com/privacy"
        ),
        DnsProvider(
            id = "adguard",
            name = "AdGuard DNS",
            filterName = "Standard Filter",
            region = Region.NA,
            dohUrl = "https://dns.adguard-dns.com/dns-query",
            description = "High-availability global anycast DNS with ad, tracker, and phishing blocking.",
            websiteUrl = "https://adguard-dns.io",
            privacyPolicyUrl = "https://adguard-dns.io/en/privacy.html"
        ),
        DnsProvider(
            id = "blahdns-sg",
            name = "BlahDNS",
            filterName = "Singapore (SEA)",
            region = Region.SEA,
            dohUrl = "https://doh-sg.blahdns.com/dns-query",
            description = "Community-run zero-log DNS resolver with ad blocking in Southeast Asia.",
            websiteUrl = "https://blahdns.com",
            privacyPolicyUrl = "https://blahdns.com"
        ),
        DnsProvider(
            id = "blahdns-de",
            name = "BlahDNS",
            filterName = "Germany (EUW)",
            region = Region.EUW,
            dohUrl = "https://doh-de.blahdns.com/dns-query",
            description = "Community-run zero-log DNS resolver with ad blocking in Western Europe.",
            websiteUrl = "https://blahdns.com",
            privacyPolicyUrl = "https://blahdns.com"
        ),
        DnsProvider(
            id = "dnsforge",
            name = "dnsforge.de",
            filterName = "Clean Ad & Tracker Filter",
            region = Region.EUW,
            dohUrl = "https://dnsforge.de/dns-query",
            description = "Fast, non-censored, ad-free and privacy-friendly DNS server hosted in Germany.",
            websiteUrl = "https://dnsforge.de",
            privacyPolicyUrl = "https://dnsforge.de/datenschutz/"
        ),
        DnsProvider(
            id = "hagezi-de",
            name = "HaGeZi DNS",
            filterName = "Germany Server",
            region = Region.EUW,
            dohUrl = "https://root.hagezi.org/dns-query",
            description = "High-performance encrypted DNS by the author of HaGeZi blocklists.",
            websiteUrl = "https://github.com/hagezi/dns-blocklists",
            privacyPolicyUrl = "https://github.com/hagezi/dns-blocklists"
        ),
        DnsProvider(
            id = "hagezi-fi",
            name = "HaGeZi DNS",
            filterName = "Finland Server",
            region = Region.EUNE,
            dohUrl = "https://juuri.hagezi.org/dns-query",
            description = "High-performance encrypted DNS located in Northern Europe.",
            websiteUrl = "https://github.com/hagezi/dns-blocklists",
            privacyPolicyUrl = "https://github.com/hagezi/dns-blocklists"
        ),
        DnsProvider(
            id = "libredns",
            name = "LibreDNS",
            filterName = "No Ads",
            region = Region.EUW,
            dohUrl = "https://doh.libredns.gr/noads",
            description = "Public, zero-logging DNS service hosted in Europe with ad filtering.",
            websiteUrl = "https://libredns.gr",
            privacyPolicyUrl = "https://libredns.gr"
        ),
        DnsProvider(
            id = "dns4eu",
            name = "DNS4EU",
            filterName = "NoAds",
            region = Region.EUW,
            dohUrl = "https://noads.joindns4.eu/dns-query",
            description = "European sovereign DNS infrastructure with malware and ad-blocking capabilities.",
            websiteUrl = "https://www.joindns4.eu",
            privacyPolicyUrl = "https://www.joindns4.eu/privacy"
        )
    )

    fun getRecommendedForRegion(region: Region): List<DnsProvider> {
        val regionSpecific = providers.filter { it.region == region }
        val globalProviders = providers.filter { it.region == Region.NA }
        return (regionSpecific + globalProviders).distinctBy { it.id }
    }

    suspend fun testProviderLatency(provider: DnsProvider): ResolverHealth {
        return dohClient.checkHealth(provider.dohUrl)
    }

    /**
     * Validates a custom user-supplied DoH URL.
     * Enforces HTTPS scheme, valid format, and real resolution test.
     */
    suspend fun validateCustomDohUrl(url: String): Result<ResolverHealth> {
        val trimmed = url.trim()
        if (!trimmed.startsWith("https://", ignoreCase = true)) {
            return Result.failure(IllegalArgumentException("Custom DNS URL must start with https:// (DNS-over-HTTPS)."))
        }

        try {
            val health = dohClient.checkHealth(trimmed)
            if (!health.isReachable) {
                return Result.failure(Exception(health.errorMessage ?: "Resolver is unreachable."))
            }
            return Result.success(health)
        } catch (e: Exception) {
            return Result.failure(Exception("Failed to validate DoH endpoint: ${e.message}"))
        }
    }
}
