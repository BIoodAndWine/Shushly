# DNS Providers and Upstream Directory

Shushly routes DNS queries to public, free, encrypted DNS-over-HTTPS (DoH) resolvers. The application does not operate proprietary DNS servers or host proprietary filtering infrastructure.

---

## 1. Built-in Protection Modes

| Mode | Filter Name | Upstream DoH Endpoint | Filtering Characteristics |
|---|---|---|---|
| **Light** *(Default)* | Control D Ads & Tracking | `https://freedns.controld.com/p2` | Everyday ad and tracker blocking. Highest site and application compatibility. Recommended for casual browsing and smart home devices. |
| **Balanced** | Control D OISD Full | `https://freedns.controld.com/x-oisd` | Comprehensive ad, tracker, and telemetry blocking using the renowned OISD Full blocklist. Ideal balance of privacy and reliability. |
| **Strict** | Control D HaGeZi Pro | `https://freedns.controld.com/x-hagezi-pro` | Strict privacy protection using HaGeZi's Pro blocklist. Aggressively blocks trackers, telemetry, malicious domains, and affiliate tracking. |

Official documentation: [Control D Free DNS](https://docs.controld.com/docs/free-dns)

---

## 2. Advanced Mode Built-in Directory

Advanced Mode is 100% free and open, offering instant one-click switching and live latency testing to regional and privacy-focused resolvers worldwide:

| Provider | Region | DoH Endpoint | Focus / Use Case |
|---|---|---|---|
| **Control D Ads & Tracking** | Global Anycast | `https://freedns.controld.com/p2` | General ad blocking & telemetry prevention |
| **Control D OISD Full** | Global Anycast | `https://freedns.controld.com/x-oisd` | Comprehensive balanced blocking |
| **Control D HaGeZi Pro** | Global Anycast | `https://freedns.controld.com/x-hagezi-pro` | Strict privacy & tracking protection |
| **AdGuard DNS** | Global Anycast | `https://dns.adguard-dns.com/dns-query` | Global ad & phishing blocking |
| **BlahDNS Singapore** | Southeast Asia | `https://doh-sg.blahdns.com/dns-query` | Low-latency DNS filtering in Asia-Pacific |
| **BlahDNS Germany** | Central Europe | `https://doh-de.blahdns.com/dns-query` | Low-latency DNS filtering in Central Europe |
| **dnsforge.de** | Germany (EU) | `https://dnsforge.de/dns-query` | Independent German privacy resolver |
| **HaGeZi DNS Germany** | Germany (EU) | `https://root.hagezi.org/dns-query` | Dedicated European HaGeZi DoH resolver |
| **HaGeZi DNS Finland** | Finland (Nordics) | `https://juuri.hagezi.org/dns-query` | Dedicated Nordic HaGeZi DoH resolver |
| **LibreDNS No Ads** | Greece (EU) | `https://doh.libredns.gr/noads` | Community-operated zero-log resolver |
| **DNS4EU NoAds** | European Union | `https://noads.joindns4.eu/dns-query` | European sovereign DNS infrastructure |

---

## 3. Custom DoH Endpoints

In Advanced Mode, users can configure any RFC 8484 compliant DNS-over-HTTPS URL (e.g., private NextDNS profiles, personal AdGuard Home instances, Pi-hole DoH gateways, or corporate DoH resolvers).

### Verification Criteria for Upstream Endpoints
All built-in directory resolvers satisfy these strict requirements:
1. Valid HTTPS transport with strict TLS 1.3 / 1.2 certificate verification.
2. Native RFC 8484 DNS-over-HTTPS wire-format compatibility.
3. Verified ad, tracker, and malware blocking functionality.
4. Published zero-logging and zero-monetization privacy policies.
5. No required registration, API keys, or accounts for resolution.
