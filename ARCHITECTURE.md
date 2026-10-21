# Architecture & System Design

Shushly is built with clean architecture and MVVM principles, separating presentation, domain, and platform-specific network integrations while sharing business logic and UI across platforms.

---

## 1. Architectural Layers

```
┌────────────────────────────────────────────────────────┐
│               Compose Multiplatform UI                 │
│      HomeScreen  ·  AdvancedScreen  ·  SettingsScreen  │
│         (AnimatedThemeToggle · PowerButton)            │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│                       ViewModels                       │
│     HomeViewModel · AdvancedViewModel · SettingsVM     │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│                    Domain & Engine                     │
│  DnsProtectionController (Interface)  ·  DohClient     │
│  DnsPacketParser  ·  Models (ProtectionLevel, Status)  │
└───────────────┬────────────────────────┬───────────────┘
                │                        │
┌───────────────▼──────────────┐  ┌──────▼───────────────┐
│     Android VpnService       │  │  Windows 11 Engine   │
│  DNS-only local TUN route    │  │  127.0.0.1:5300 proxy│
│  RFC 8484 DoH query dispatch │  │  PowerShell NetTCPIP │
│  UDP checksum & packet reply │  │  Auto-restore hook   │
└──────────────────────────────┘  └──────────────────────┘
```

---

## 2. Platform Implementations

### Android DNS Engine
- **Implementation:** `ShushlyVpnService` extends Android `VpnService`.
- **DNS-Only Routing:** The TUN interface allocates private addresses (`10.233.0.2/32`, `fd00:1::2/128`) and registers DNS endpoints (`10.233.0.1`, `fd00:1::1`). Routes are configured exclusively for these DNS endpoints. Web browsing, media streams, downloads, and app connections bypass the VPN tunnel entirely.
- **Packet Handling:** UDP port 53 packets arriving on the TUN interface are parsed via `DnsPacketParser`. The raw DNS payload wire format is forwarded over HTTPS to the selected DoH resolver (`application/dns-message`).
- **Loop Prevention:** Outbound HTTPS connections use the device network directly via `VpnService.protect(socket)`, preventing routing loops.
- **Response Delivery:** When DoH responses return, a standard IPv4 UDP packet is constructed with correct IP header checksums and written to the TUN file descriptor.
- **Lifecycle:** Operates as a foreground service with a low-priority status notification. Handles connectivity changes between Wi-Fi and mobile networks via `ConnectivityManager.NetworkCallback`.

### Windows 11 DNS Engine
- **Implementation:** `WindowsDnsProtectionController` manages system network adapters and runs a local DoH forwarder.
- **Local DNS Forwarder:** Binds a UDP socket to `127.0.0.1:5300`. Incoming DNS queries from the Windows DNS client are converted to RFC 8484 DoH POST requests, dispatched to upstream resolvers, and returned to the client socket.
- **Adapter Configuration:** Queries the active network interface index and backs up existing DNS configuration (DHCP or static servers). Directs the adapter's IPv4 DNS to the local forwarder and executes `Clear-DnsClientCache`.
- **Safe Restore:** When disconnected or on application shutdown (via JVM shutdown hook), resets the adapter to DHCP or restores the exact saved IP list, avoiding orphaned network settings.

---

## 3. Reactive State Flow

All components observe immutable StateFlows:
- `DnsProtectionController.status`: Reports real system engine status (`DISABLED`, `CONNECTING`, `CONNECTED`, `RECONNECTING`, `ERROR`).
- `SettingsRepository.configuration`: Persists chosen protection modes (`Light`, `Balanced`, `Strict`), auto-reconnect preferences, custom resolvers, and theme choices.
- `SettingsRepository.themeMode`: Drives animated theme toggling and OS appearance synchronization.
