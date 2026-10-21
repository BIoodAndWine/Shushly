# Privacy Policy

**Last updated:** October 21, 2026

Shushly is designed from first principles to protect your privacy without tracking, profiling, or data collection.

---

## 1. Core Principles

- **Zero Logs:** Shushly does not inspect, log, store, or transmit your DNS queries, visited domains, or browsing activity.
- **Zero Telemetry:** Shushly contains no analytics SDKs, event trackers, crash collectors, or advertising identifiers.
- **Zero Accounts:** Shushly requires no account, email address, password, or personal information.
- **Zero Interception:** Shushly does not proxy or decrypt web traffic. Only DNS queries are routed to your selected encrypted DNS resolver.

---

## 2. Information Handled by Shushly

### On Your Device (Local Storage)
All settings and preferences are stored exclusively on your device using native platform mechanisms (Android SharedPreferences / Windows Multiplatform Settings):
- Selected protection mode (`Light`, `Balanced`, `Strict`, or custom provider)
- UI theme preference (Light, Dark, or System)
- Custom DNS endpoint configuration (if you choose to configure one in Advanced Mode)
- Audio sound toggle preference

This data never leaves your device and is erased when you clear app data or uninstall the application.

### Network Traffic
When protection is enabled:
- **Android:** Shushly establishes a local `VpnService` that redirects DNS traffic (UDP/TCP port 53) to our local loopback engine. The engine encrypts outgoing queries using RFC 8484 DNS-over-HTTPS (DoH) and sends them to the chosen resolver. Non-DNS network traffic (web pages, media, downloads) flows directly through your normal network interface untouched.
- **Windows:** Shushly configures your active network adapter to route DNS through a secure local loopback forwarder (`127.0.0.1:5300`) that forwards queries via DoH to your chosen resolver.

---

## 3. Third-Party Resolvers

Shushly does not operate its own DNS servers. Queries are resolved by reputable public, encrypted DNS-over-HTTPS providers. When you select a mode, queries are resolved by that provider under their respective privacy policies:

| Mode / Feature | Default Resolver | Provider Privacy Policy |
|---|---|---|
| **Light** | Control D Ads & Tracking (`freedns.controld.com/p2`) | [Control D Privacy Policy](https://controld.com/privacy) |
| **Balanced** | Control D OISD Full (`freedns.controld.com/x-oisd`) | [Control D Privacy Policy](https://controld.com/privacy) |
| **Strict** | Control D HaGeZi Pro (`freedns.controld.com/x-hagezi-pro`) | [Control D Privacy Policy](https://controld.com/privacy) |
| **Advanced Mode** | User-selected or custom DoH endpoint | Governed by chosen third-party resolver |

All default resolvers provide zero-log guarantees and enforce strict TLS encryption in transit.

---

## 4. Permissions

Shushly requests only the minimum permissions required to perform DNS filtering:
- **Android `BIND_VPN_SERVICE`:** Required to capture local DNS requests and forward them via DoH.
- **Android `INTERNET` & `ACCESS_NETWORK_STATE`:** Required to establish DoH connections and detect network connectivity changes.
- **Windows Administrator / UAC:** Required once to configure network adapter DNS settings.

---

## 5. Open Source Transparency

Shushly is completely open source under the MIT License. You can verify our privacy commitments and audit our codebase directly on GitHub:
[https://github.com/BIoodAndWine/Shushly](https://github.com/BIoodAndWine/Shushly)

---

## 6. Contact & Questions

If you have questions about privacy in Shushly, please open a GitHub Discussion or submit an issue in our repository.
