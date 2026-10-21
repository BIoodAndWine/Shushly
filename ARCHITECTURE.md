# Architecture

Shushly follows clean architecture and MVVM principles, separating presentation, domain, and platform-specific network integrations.

## Architecture layers

```
┌────────────────────────────────────────────────────────┐
│               Compose Multiplatform UI                 │
│      HomeScreen  ·  PremiumScreen  ·  SettingsScreen   │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│                       ViewModels                       │
│     HomeViewModel · PremiumViewModel · SettingsViewModel│
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
│  DNS-only local TUN route    │  │  127.0.0.1:53 proxy  │
│  RFC 8484 DoH query dispatch │  │  PowerShell NetTCPIP │
│  UDP checksum & packet reply │  │  Auto-restore hook   │
└──────────────────────────────┘  └──────────────────────┘
```

## Platform implementations

### Android DNS engine

- **Implementation:** `ShushlyVpnService` extends Android `VpnService`.
- **DNS-only routing:** The TUN interface allocates private addresses (`10.233.0.2/32`, `fd00:1::2/128`) and registers DNS endpoints (`10.233.0.1`, `fd00:1::1`). Routes are added exclusively for the DNS addresses. Web, media, and general application TCP/UDP connections never enter the tunnel and remain untouched.
- **Packet handling:** UDP port 53 packets arriving on the TUN interface are parsed via `DnsPacketParser`. The DNS payload wire format is forwarded over HTTPS to the selected DoH resolver (`application/dns-message`).
- **Loop prevention:** Outbound HTTPS connections use the device network directly, preventing routing loops.
- **Response delivery:** When DoH responses return, a standard IPv4 UDP packet is constructed with correct IP header checksums and written to the TUN file descriptor.
- **Lifecycle:** Operates as a foreground service with a low-priority status notification. Handles connectivity changes between Wi-Fi and mobile networks via `ConnectivityManager.NetworkCallback`.

### Windows 11 DNS engine

- **Implementation:** `WindowsDnsProtectionController` manages system network adapters and runs a local DoH proxy.
- **Local DNS forwarder:** Binds a UDP socket to `127.0.0.1:53`. Incoming standard DNS queries from the Windows DNS client are converted to RFC 8484 DoH POST requests, dispatched to upstream resolvers, and returned to the client socket.
- **Adapter configuration:** Queries the active network interface index and backs up existing DNS configuration (DHCP or static servers). Directs the adapter's IPv4 DNS to `127.0.0.1` and executes `Clear-DnsClientCache`.
- **Safe restore:** When disconnected or on application shutdown (via JVM shutdown hook), resets the adapter to DHCP or restores the exact saved IP list, avoiding orphaned network settings.

## Reactive state flow

All components observe immutable StateFlows:
- `DnsProtectionController.status`: Reports real system engine status (`DISABLED`, `CONNECTING`, `CONNECTED`, `RECONNECTING`, `ERROR`).
- `SettingsRepository.configuration`: Persists chosen protection levels, auto-reconnect preferences, and custom resolvers.
- `BillingRepository.entitlement`: Emits lifetime premium status.
