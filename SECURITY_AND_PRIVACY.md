# Security and privacy policy

Shushly handles network configuration and adheres to strict security and privacy standards.

## Security architecture

- **TLS verification:** All DNS-over-HTTPS requests enforce certificate authority validation and hostname matching. No custom root certificates or insecure trust managers are installed.
- **DNS-only local routing:** On Android, the local VPN routes only DNS server addresses (`10.233.0.1`, `fd00:1::1`). Regular internet traffic is not inspected, proxied, or decrypted.
- **Routing loop prevention:** Upstream DoH queries bypass the local DNS interception layer using OS-level socket protection.
- **Safe restore:** The Windows engine backs up adapter DNS settings before modification. It registers a JVM shutdown hook to restore adapter settings to DHCP or saved static servers on process termination.
- **No executable injection:** DNS configuration commands use static parameter arrays, avoiding command interpolation or script injection.

## Privacy practices

- **No query logging:** Shushly does not inspect, log, or record domain queries or visited URLs.
- **No analytics or telemetry:** No user tracking SDKs, crash report collectors, or advertising identifiers are integrated.
- **No mandatory accounts:** The application operates without requiring sign-up, email addresses, or phone numbers.
- **Local persistence:** User preferences (theme, selected protection level, custom DoH endpoints) are stored locally on the device using platform preferences.
- **Third-party providers:** Queries are resolved by the selected public DNS resolver (Control D, AdGuard, or user-provided DoH). Queries are subject to each provider's published privacy policy.
