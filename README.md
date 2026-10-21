<div align="center">

<img src="shushly-logo.png" alt="Shushly Logo" width="120" height="120" />

# Shushly

**Effortless, one-click DNS ad and tracker blocking for everyone.**

[![CI](https://github.com/BIoodAndWine/Shushly/actions/workflows/ci.yml/badge.svg)](https://github.com/BIoodAndWine/Shushly/actions/workflows/ci.yml)
[![Release](https://img.shields.io/github/v/release/BIoodAndWine/Shushly?color=blue&label=version)](https://github.com/BIoodAndWine/Shushly/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Compose Multiplatform](https://img.shields.io/badge/Compose-Multiplatform-4285F4.svg?logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Privacy: Zero Logs](https://img.shields.io/badge/Privacy-Zero%20Logs-brightgreen.svg)](#privacy--security-model)

[Download](#downloads--installation) · [Features](#features) · [Protection Modes](#three-protection-modes) · [Advanced Mode](#advanced-mode-100-free) · [Architecture](#architecture-at-a-glance) · [Building from Source](#building-from-source)

</div>

---

## Why Shushly?

Ordinary people shouldn't have to learn network routing, regex blocklists, or self-hosted container administration just to stop invasive ads and surveillance trackers.

**Shushly turns ad blocking into a single toggle.**

- **No accounts or sign-ups:** Launch and click.
- **No subscriptions or paywalls:** 100% free forever.
- **No payload decryption:** Only DNS lookups are filtered. Your web traffic and personal data are never inspected or proxied.
- **No telemetry:** Zero analytics, zero crash trackers, zero advertising SDKs.
- **Truthful engine states:** The power button reflects the real network state: `Disconnected`, `Connecting`, `Connected`, `Reconnecting`, or `Error`.

---

## Three Protection Modes

Shushly comes preconfigured with three curated protection levels powered by industry-leading, privacy-first encrypted DNS-over-HTTPS (DoH) providers:

| Mode | Filter Upstream | Endpoint | Best For |
|---|---|---|---|
| **Light** *(Default)* | Control D Ads & Tracking | `https://freedns.controld.com/p2` | Everyday ad & tracker blocking with 100% website compatibility. No broken layouts or shopping redirects. |
| **Balanced** | Control D OISD Full | `https://freedns.controld.com/x-oisd` | Comprehensive protection blocking trackers, telemetry, and malicious advertising using the renowned OISD Full blocklist. |
| **Strict** | Control D HaGeZi Pro | `https://freedns.controld.com/x-hagezi-pro` | Strict privacy shield using HaGeZi Pro. Aggressively neutralizes trackers, telemetry, malware, and affiliate redirection. |

---

## Advanced Mode (100% Free)

No features are hidden behind paywalls. Advanced users have complete control via **Advanced Mode**:

- **Custom DoH Endpoints:** Connect to your own NextDNS profile, personal AdGuard Home, Pi-hole DoH gateway, or corporate resolver.
- **Built-in Global Directory:** Instant switching between verified zero-log resolvers worldwide (Control D, AdGuard, HaGeZi, BlahDNS, dnsforge, LibreDNS, DNS4EU).
- **Live Latency & Health Checks:** Real-time ping and availability testing for every resolver in the directory.
- **Automatic Failover:** Resilient DNS resolution with automatic fallback.

---

## Architecture at a Glance

Shushly is built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**, maintaining a single reactive codebase across platforms.

```
┌────────────────────────────────────────────────────────┐
│               Compose Multiplatform UI                 │
│      HomeScreen  ·  AdvancedScreen  ·  SettingsScreen  │
│        (AnimatedThemeToggle · TactilePowerButton)      │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│                    Reactive ViewModels                 │
│         StateFlow · Single Source of Truth             │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│               Domain & Networking Engine               │
│   RFC 8484 DNS-over-HTTPS · DnsPacketParser · DoH Client│
└───────────────┬────────────────────────┬───────────────┘
                │                        │
┌───────────────▼──────────────┐  ┌──────▼───────────────┐
│     Android VpnService       │  │  Windows 11 Engine   │
│  DNS-only local TUN route    │  │  127.0.0.1:5300 proxy│
│  Zero payload interception   │  │  Auto-restore hook   │
└──────────────────────────────┘  └──────────────────────┘
```

- **Android:** Uses Android's native `VpnService` exclusively as a DNS loopback (`10.233.0.1`, `fd00:1::1`). User web payload (HTTP, HTTPS, video, games) **never** traverses the tunnel.
- **Windows:** Binds a lightweight local forwarder to `127.0.0.1:5300` and configures adapter DNS via PowerShell NetTCPIP with automatic restoration on shutdown.

For full architectural documentation, read [ARCHITECTURE.md](ARCHITECTURE.md).

---

## Downloads & Installation

### Windows 10 / 11
1. Download the latest `Shushly-Windows-Portable-v*.zip` or `Shushly-Windows-v*.msi` from [Releases](https://github.com/BIoodAndWine/Shushly/releases).
2. Extract the archive and launch `Shushly.exe` (or run the installer).
3. Click the power button to protect your system.

### Android (Android 8.0+)
1. Download the latest `Shushly-Android-v*.apk` from [Releases](https://github.com/BIoodAndWine/Shushly/releases).
2. Install the APK on your device.
3. Grant the standard Android VPN permission prompt when activating for the first time.

---

## Building from Source

### Prerequisites
- **JDK 21 LTS** (Eclipse Temurin, Azul Zulu, or OpenJDK)
- **Android SDK** with Platform 35 and Build Tools 35.0.0+ (for Android builds)
- **Git**

### Clone & Test
```bash
git clone https://github.com/BIoodAndWine/Shushly.git
cd Shushly

# Run unit and DoH integration tests
./gradlew :composeApp:desktopTest
```

### Build Android APK
```bash
./gradlew :composeApp:assembleDebug
```
Output: `composeApp/build/outputs/apk/debug/composeApp-debug.apk`

### Build Windows Desktop
```powershell
.\gradlew.bat :composeApp:createDistributable
```
Output: `composeApp/build/compose/binaries/main/app/Shushly/Shushly.exe`

For detailed release building and signing instructions, see [BUILD_AND_RELEASE.md](BUILD_AND_RELEASE.md).

---

## Privacy & Security Model

- **Zero Logs:** We operate no central servers and collect no DNS queries, IP addresses, or browsing history.
- **Strict TLS:** All DoH queries enforce TLS 1.3 / 1.2 certificate verification.
- **Open & Auditable:** All code is open-source. Anyone can inspect how packets are routed.

Read our complete [Privacy Policy](PRIVACY.md) and [Security Policy](SECURITY.md).

---

## Documentation

- [ARCHITECTURE.md](ARCHITECTURE.md) - Internal system architecture and reactive engine.
- [DNS_PROVIDERS.md](DNS_PROVIDERS.md) - Upstream resolver specifications and blocklists.
- [BUILD_AND_RELEASE.md](BUILD_AND_RELEASE.md) - Build instructions, code signing, and CI/CD.
- [PLATFORM_LIMITATIONS.md](PLATFORM_LIMITATIONS.md) - Technical boundaries of DNS ad blocking.
- [PRIVACY.md](PRIVACY.md) - Zero-telemetry privacy policy.
- [SECURITY.md](SECURITY.md) - Security vulnerability reporting.
- [CONTRIBUTING.md](CONTRIBUTING.md) - Contribution guidelines and PR process.
- [CHANGELOG.md](CHANGELOG.md) - Release history and version notes.
- [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) - Open source licenses and upstream attributions.

---

## Upstream Attributions

Shushly does not host DNS servers. We proudly route queries to independent, privacy-conscious public resolvers:
- **Control D** ([controld.com](https://controld.com/))
- **OISD** ([oisd.nl](https://oisd.nl/))
- **HaGeZi** ([github.com/hagezi/dns-blocklists](https://github.com/hagezi/dns-blocklists))
- **AdGuard DNS** ([adguard-dns.io](https://adguard-dns.io/))

---

## License

Shushly is open-source software licensed under the **[MIT License](LICENSE)**.
