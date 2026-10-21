# DNS providers and verification

Shushly routes DNS queries to public, free, encrypted DNS-over-HTTPS (DoH) resolvers. The application does not operate proprietary DNS servers.

## Preconfigured free protection levels

| Level | Filter Name | Endpoint | Behavior |
|---|---|---|---|
| **Default** | Control D Ads & Tracking | `https://freedns.controld.com/p2` | Everyday ad and tracker blocking. High site compatibility. |
| **Balanced** | Control D OISD Full | `https://freedns.controld.com/x-oisd` | Comprehensive blocking using OISD Full blocklist. Default selection. |
| **Strong** | Control D HaGeZi Pro | `https://freedns.controld.com/x-hagezi-pro` | Strict privacy filtering using HaGeZi Pro blocklist. |

Official documentation: https://docs.controld.com/docs/free-dns

## Built-in premium directory

| Provider | Region | DoH Endpoint | Primary Use |
|---|---|---|---|
| Control D Ads & Tracking | Global / NA | `https://freedns.controld.com/p2` | General ad blocking |
| Control D OISD Full | Global / NA | `https://freedns.controld.com/x-oisd` | Balanced ad/tracker filtering |
| Control D HaGeZi Pro | Global / NA | `https://freedns.controld.com/x-hagezi-pro` | Strict privacy protection |
| AdGuard DNS | Global / NA | `https://dns.adguard-dns.com/dns-query` | Global anycast ad & phishing blocking |
| BlahDNS Singapore | SEA | `https://doh-sg.blahdns.com/dns-query` | Low latency for Southeast Asia |
| BlahDNS Germany | EUW | `https://doh-de.blahdns.com/dns-query` | Low latency for Central Europe |
| dnsforge | EUW | `https://dnsforge.de/dns-query` | Privacy-focused German resolver |
| HaGeZi DNS Germany | EUW | `https://root.hagezi.org/dns-query` | Direct HaGeZi upstream in Germany |
| HaGeZi DNS Finland | EUNE | `https://juuri.hagezi.org/dns-query` | Direct HaGeZi upstream in Northern Europe |
| LibreDNS No Ads | EUW | `https://doh.libredns.gr/noads` | Greek community zero-log resolver |
| DNS4EU NoAds | EUW | `https://noads.joindns4.eu/dns-query` | European sovereign DNS infrastructure |

## Verification criteria

All endpoints included in the directory meet these requirements:
1. Valid HTTPS transport with strict TLS certificate verification.
2. Direct compliance with RFC 8484 DNS-over-HTTPS standards.
3. Verified ad and tracker blocking functionality tested against ad networks.
4. Published zero-logging privacy policies.
5. No required account or embedded API key needed for query resolution.
