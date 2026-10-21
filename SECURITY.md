# Security Policy

## Supported Versions

We provide security updates and patches for the current major release track.

| Version | Supported          |
| ------- | ------------------ |
| 0.1.x   | :white_check_mark: |
| < 0.1   | :x:                |

---

## Security Architecture

Shushly incorporates multiple layers of security to safeguard device connectivity:

1. **Strict TLS Certificate Verification:** All DNS-over-HTTPS (DoH) requests strictly validate CA certificates and hostnames. No custom root certificates or permissive trust managers are installed.
2. **DNS-Only Routing:** On Android, `VpnService` routes only DNS addresses (`10.233.0.1`, `fd00:1::1`). User payload data (HTTP, HTTPS, SSH, WebSockets) is never intercepted or proxied.
3. **Loopback Protection:** Upstream HTTPS connections bypass the local DNS capture loop using OS socket protection (`VpnService.protect()`), preventing routing loops.
4. **Safe Adapter Restoration:** On Windows, original adapter DNS addresses (or DHCP defaults) are recorded before modification and restored on application exit via JVM shutdown hooks and clean service termination.
5. **Static Parameterized Execution:** OS network commands on Windows use static argument arrays, preventing command interpolation and script injection.

---

## Reporting a Vulnerability

We appreciate the security community's efforts to keep open-source software safe.

If you believe you have discovered a security vulnerability in Shushly:

1. **Do not disclose the issue publicly** (e.g., in a public GitHub issue, forum, or social media).
2. Report the vulnerability privately using **GitHub Private Vulnerability Reporting** via the [Security Advisories](https://github.com/BIoodAndWine/Shushly/security/advisories) tab of this repository.
3. If Private Vulnerability Reporting is unavailable, send an email to `security@shushly.app` (or contact the maintainers via GitHub).

### What to Include in Your Report

To help us triage and resolve the issue quickly, please include:
- A description of the vulnerability and its potential impact.
- Affected platform (Android, Windows, etc.) and application version.
- Step-by-step reproduction instructions or a minimal Proof of Concept (PoC).
- Any proposed mitigations or fixes.

### Response Timeline

- **Initial Response:** Within 48 hours of receipt.
- **Triage & Assessment:** Within 7 business days.
- **Resolution & Coordinated Disclosure:** We aim to release a patch within 30 days of confirmed triage, coordinating the release date with the reporter.
