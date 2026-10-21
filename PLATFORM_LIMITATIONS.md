# Platform limitations and technical boundaries

DNS-level ad blocking operates by returning unresolvable responses (`0.0.0.0` or `NXDOMAIN`) for known advertising and tracking hostnames. Users and developers must understand the technical boundaries of this mechanism.

## Boundaries of DNS ad blocking

1. **Same-domain advertisements:** Advertisements hosted on the same domain as the primary content (such as YouTube in-stream video ads, sponsored posts in Facebook, Instagram, or TikTok) cannot be blocked by DNS without breaking the main service.
2. **Hardcoded IP addresses:** Applications connecting directly to IP addresses rather than performing DNS lookups bypass DNS filtering.
3. **Internal encrypted DNS:** Browsers or applications with hardcoded DNS-over-HTTPS configurations (such as Chrome with custom secure DNS enabled) may bypass local system DNS unless configured to use system resolver settings.

## Platform-specific notes

### Android

- **VPN conflicts:** Android permits only one active `VpnService` at a time. Activating another VPN application will disconnect Shushly.
- **Private DNS:** If Android Private DNS is enabled in system settings with strict mode, Android forces DNS queries through that designated provider, which may override local VPN DNS routes.

### Windows 11

- **Administrative permissions:** Changing network adapter DNS server addresses requires standard user authorization or UAC elevation.
- **Enterprise and VPN policies:** Workstation environments governed by Group Policy (GPO) or enterprise MDM configurations may enforce specific corporate DNS settings.

### Apple platforms (iOS and macOS)

- **Network Extension:** Encrypted DNS on iOS and macOS uses Apple's `NEDNSSettingsManager`. Apple requires explicit user enablement in Settings when an encrypted DNS profile is installed.
- **Compilation environment:** Native iOS frameworks and macOS notarized packages require macOS and Xcode.
