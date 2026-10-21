# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [0.1.1] - 2026-10-21

### Changed & Fixed
- **UI Sound Synchronization Overhaul:**
  - Decoupled physical button press (`CLICK`) from connection completion (`CONNECTED`), eliminating premature and duplicate audio cues.
  - Success chime (`CONNECTED`) now plays exclusively upon verified system-level DNS interception and reachability.
  - Silent background reconnections: Auto-recovery from temporary network interruptions now operates completely silently.
  - Startup state silence: Initial app launch state transitions no longer emit audible cues.
  - Disconnect feedback: `TOGGLE_OFF` cue triggers only after verified teardown of local DNS interception.
  - Error deduplication: Controller errors and network failures trigger exactly one distinct error cue without duplicate alerts.
  - Rapid tap debouncing: Sub-80ms repetitive clicks are smoothly coalesced without input latency.
- **Android Sound Engine Hardening:**
  - Replaced asynchronous, unmonitored `SoundPool.load()` with an explicit `OnLoadCompleteListener` and ready sample cache.
  - Added pending play queue to eliminate missed audio cues during initial application startup.
  - Replaced brittle ClassLoader lookups with canonical Android `AssetManager` resource extraction.
  - Enforced strict adherence to Android's ringer mode (`AudioManager.RINGER_MODE_SILENT` and `RINGER_MODE_VIBRATE`).
- **Windows / Desktop Audio Overhaul:**
  - Eliminated native DirectSound / WASAPI audio line leakage by introducing reusable, pre-opened `Clip` instances with frame rewinding.
  - Reduced desktop playback latency to sub-millisecond response.
  - Added deterministic cleanup and line listeners to safeguard against headless environments or missing audio output hardware.
- **Automated Sound Synchronization Test Suite:**
  - Added comprehensive unit tests in `SoundSyncTests.kt` verifying state transitions, button press timing, error handling, debouncing, and mute enforcement.
- **Distribution & CI/CD Pipeline:**
  - Transitioned Android packaging to genuine production **Release APKs** (`com.shushly.app`) with signing support via GitHub Actions secrets and automated open-source signing fallback.
  - Fixed Windows MSI packaging pipeline with WiX Toolset path detection and deterministic validation.
  - Aligned semantic versioning (`0.1.1`) across Git tags, Android manifest, Desktop jpackage metadata, Settings, and About dialogs.
  - Hardened Dependabot rules to prevent incompatible breaking PRs against the Kotlin Multiplatform dependency matrix.

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
