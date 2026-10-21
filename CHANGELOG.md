# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [0.1.0] - 2026-10-21

### Added
- **One-Click Protection:** Instant system-wide DNS ad and tracker blocking powered by Kotlin Multiplatform and Compose Multiplatform.
- **Three Protection Modes:**
  - `Light` (Default for new users): High compatibility blocking powered by Control D Ads & Tracking (`https://freedns.controld.com/p2`).
  - `Balanced`: Balanced blocking powered by Control D OISD Full (`https://freedns.controld.com/x-oisd`).
  - `Strict`: Maximum privacy filtering powered by Control D HaGeZi Pro (`https://freedns.controld.com/x-hagezi-pro`).
- **Animated Theme Switcher:** Interactive Sun/Moon header toggle inspired by `karlmolles.com` featuring coupled spring physics, smooth scale and rotation transitions, and animated color token interpolation across the UI.
- **Follow System Appearance:** Optional toggle in Settings to adapt seamlessly to OS dark/light mode switches.
- **Advanced Mode (100% Free):**
  - Unrestricted custom DoH resolver endpoint configuration.
  - Built-in international DNS resolver directory (Control D, AdGuard, HaGeZi, BlahDNS, dnsforge, LibreDNS, DNS4EU).
  - Live DNS resolver latency testing and health checks.
  - Automatic failover support.
- **Engine State Machine:** Real-time truthful status reporting (`DISABLED`, `CONNECTING`, `CONNECTED`, `RECONNECTING`, `ERROR`) with latency telemetry.
- **Android VpnService Engine:** DNS-only loopback routing through `10.233.0.1` and `fd00:1::1` with zero packet inspection of non-DNS traffic.
- **Windows Local DNS Engine:** Background loopback forwarder (`127.0.0.1:5300`) with safe adapter restoration on process termination.
- **Sound System (`earshot`):** Native multiplatform audio feedback on toggle, click, and state changes with an accessible mute toggle.
- **Cross-Platform CI/CD:** GitHub Actions automation for automated testing, Android APK compilation, and Windows desktop distributable packaging.

### Security & Privacy
- **Zero Query Logging:** No logs, storage, or external transmission of visited domains or queries.
- **Zero Telemetry:** Completely free of tracking SDKs, analytics, and advertising frameworks.
- **Zero Account Requirement:** Fully functional out of the box with no account creation or login required.
- **Strict TLS:** Enforced TLS 1.3/1.2 certificate verification on all upstream DoH endpoints.
