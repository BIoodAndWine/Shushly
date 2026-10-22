import React, { useState } from 'react';
import {
  Download,
  Laptop,
  Smartphone,
  FileCheck,
  ExternalLink,
  Info,
  Clock,
  Sparkles,
} from 'lucide-react';
import { soundPlayer } from '../audio/soundPlayer';

export const Downloads: React.FC = () => {
  const [platform, setPlatform] = useState<'windows' | 'android'>('windows');

  const releaseVersion = 'v0.1.2';
  const releaseBaseUrl = 'https://github.com/BIoodAndWine/Shushly/releases/latest/download';

  const windowsAssets = [
    {
      title: 'Windows MSI Installer',
      filename: 'Shushly.msi',
      format: 'Standard Windows Installer',
      desc: 'System-wide installation with Start menu shortcuts and auto-updater integration.',
      url: `${releaseBaseUrl}/Shushly.msi`,
      primary: true,
      size: '~52 MB',
      type: 'Recommended',
    },
    {
      title: 'Windows Setup Bootstrapper',
      filename: 'Shushly-Setup.exe',
      format: 'Standalone Executable Installer',
      desc: 'One-click executable installer with automated dependency checks.',
      url: `${releaseBaseUrl}/Shushly-Setup.exe`,
      primary: false,
      size: '~52 MB',
      type: 'Alternative',
    },
    {
      title: 'Windows Portable ZIP',
      filename: 'Shushly-Windows-Portable.zip',
      format: 'Zero-Installation Standalone',
      desc: 'Extract and launch Shushly directly from USB or local drive. Bundles its own minimal JRE.',
      url: `${releaseBaseUrl}/Shushly-Windows-Portable.zip`,
      primary: false,
      size: '~50 MB',
      type: 'Portable',
    },
  ];

  const androidAssets = [
    {
      title: 'Production Release APK',
      filename: 'Shushly.apk',
      format: 'Android Package (v1 + v2 Signed)',
      desc: 'Compatible with Android 8.0 through Android 15. Signed with persistent key (CN=Karl Molles).',
      url: `${releaseBaseUrl}/Shushly.apk`,
      primary: true,
      size: '~12 MB',
      type: 'Official APK',
    },
  ];

  return (
    <section id="downloads" className="py-20 sm:py-28 border-t border-light-border/80 dark:border-dark-border/80 bg-light-surface/40 dark:bg-dark-surface/20">
      <div className="max-w-6xl mx-auto px-4 sm:px-6">
        <div className="max-w-2xl mx-auto text-center mb-14">
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-mono font-medium bg-shushly-sapphire/10 text-shushly-sapphire dark:text-shushly-sapphire-light mb-3">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Latest Official Release · {releaseVersion}</span>
          </div>
          <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-light-text dark:text-dark-text">
            Download Shushly for your devices
          </h2>
          <p className="mt-4 text-sm sm:text-base text-light-muted dark:text-dark-muted">
            100% free forever. No sign-ups, no user accounts, and no license keys required.
          </p>

          {/* Platform Selector Buttons */}
          <div className="inline-flex p-1 mt-8 bg-light-surface dark:bg-dark-surface border border-light-border dark:border-dark-border rounded-xl text-xs font-semibold shadow-sm">
            <button
              type="button"
              onClick={() => {
                soundPlayer.play('click', 0.2);
                setPlatform('windows');
              }}
              className={`flex items-center gap-2 px-5 py-2.5 rounded-lg transition-all ${
                platform === 'windows'
                  ? 'bg-shushly-sapphire text-white shadow-sm'
                  : 'text-light-muted dark:text-dark-muted hover:text-light-text dark:hover:text-dark-text'
              }`}
            >
              <Laptop className="w-4 h-4" />
              Windows (Available)
            </button>
            <button
              type="button"
              onClick={() => {
                soundPlayer.play('click', 0.2);
                setPlatform('android');
              }}
              className={`flex items-center gap-2 px-5 py-2.5 rounded-lg transition-all ${
                platform === 'android'
                  ? 'bg-shushly-sapphire text-white shadow-sm'
                  : 'text-light-muted dark:text-dark-muted hover:text-light-text dark:hover:text-dark-text'
              }`}
            >
              <Smartphone className="w-4 h-4" />
              Android (Available)
            </button>
          </div>
        </div>

        {/* Windows Assets List */}
        {platform === 'windows' && (
          <div className="space-y-4 max-w-4xl mx-auto mb-10">
            {windowsAssets.map((asset, i) => (
              <div
                key={i}
                className={`p-5 sm:p-6 rounded-2xl bg-light-surface dark:bg-dark-surface border transition-all flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 ${
                  asset.primary
                    ? 'border-shushly-sapphire ring-1 ring-shushly-sapphire/30 shadow-md'
                    : 'border-light-border dark:border-dark-border hover:border-shushly-sapphire/40 shadow-sm'
                }`}
              >
                <div className="space-y-1">
                  <div className="flex items-center gap-2.5">
                    <h3 className="text-base font-bold text-light-text dark:text-dark-text">
                      {asset.title}
                    </h3>
                    <span className="px-2 py-0.5 rounded text-[10px] font-mono font-semibold bg-light-elevated dark:bg-dark-elevated text-light-muted dark:text-dark-muted border border-light-border dark:border-dark-border">
                      {asset.type}
                    </span>
                    <span className="text-xs font-mono text-light-dim dark:text-dark-dim">
                      {asset.size}
                    </span>
                  </div>
                  <p className="text-xs sm:text-sm text-light-muted dark:text-dark-muted">
                    {asset.desc}
                  </p>
                  <div className="text-[11px] font-mono text-light-dim dark:text-dark-dim pt-0.5">
                    Filename: <strong className="text-light-text dark:text-dark-text">{asset.filename}</strong>
                  </div>
                </div>

                <a
                  href={asset.url}
                  onClick={() => soundPlayer.play('click', 0.3)}
                  className={`w-full sm:w-auto inline-flex items-center justify-center gap-2 px-5 py-2.5 rounded-full text-xs font-bold transition-all shrink-0 ${
                    asset.primary
                      ? 'bg-shushly-sapphire hover:bg-shushly-sapphire-dark text-white shadow-sm hover:shadow-glow-sapphire active:scale-95'
                      : 'bg-light-elevated dark:bg-dark-elevated hover:bg-shushly-sapphire hover:text-white text-light-text dark:text-dark-text border border-light-border dark:border-dark-border'
                  }`}
                >
                  <Download className="w-3.5 h-3.5" />
                  <span>Download {asset.filename}</span>
                </a>
              </div>
            ))}
          </div>
        )}

        {/* Android Assets List */}
        {platform === 'android' && (
          <div className="space-y-4 max-w-4xl mx-auto mb-10">
            {androidAssets.map((asset, i) => (
              <div
                key={i}
                className="p-6 rounded-2xl bg-light-surface dark:bg-dark-surface border border-shushly-sapphire ring-1 ring-shushly-sapphire/30 shadow-md flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4"
              >
                <div className="space-y-1">
                  <div className="flex items-center gap-2.5">
                    <h3 className="text-base font-bold text-light-text dark:text-dark-text">
                      {asset.title}
                    </h3>
                    <span className="px-2 py-0.5 rounded text-[10px] font-mono font-semibold bg-shushly-emerald/10 text-shushly-emerald-dark dark:text-shushly-emerald border border-shushly-emerald/20">
                      Production Signed
                    </span>
                    <span className="text-xs font-mono text-light-dim dark:text-dark-dim">
                      {asset.size}
                    </span>
                  </div>
                  <p className="text-xs sm:text-sm text-light-muted dark:text-dark-muted">
                    {asset.desc}
                  </p>
                  <div className="text-[11px] font-mono text-light-dim dark:text-dark-dim pt-0.5">
                    Signer: <strong className="text-light-text dark:text-dark-text">CN=Karl Molles</strong> · SHA-256 v2 Scheme
                  </div>
                </div>

                <a
                  href={asset.url}
                  onClick={() => soundPlayer.play('click', 0.3)}
                  className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-6 py-3 rounded-full text-xs font-bold bg-shushly-sapphire hover:bg-shushly-sapphire-dark text-white shadow-sm hover:shadow-glow-sapphire active:scale-95 shrink-0"
                >
                  <Download className="w-4 h-4" />
                  <span>Download {asset.filename}</span>
                </a>
              </div>
            ))}
          </div>
        )}

        {/* Checksum & Other Platforms Strip */}
        <div className="max-w-4xl mx-auto grid grid-cols-1 md:grid-cols-2 gap-4 text-xs">
          {/* Integrity Checksum Card */}
          <div className="p-4 rounded-xl bg-light-surface dark:bg-dark-surface border border-light-border dark:border-dark-border flex items-center justify-between">
            <div className="flex items-center gap-2.5">
              <FileCheck className="w-4 h-4 text-shushly-emerald" />
              <div>
                <div className="font-semibold text-light-text dark:text-dark-text">
                  Cryptographic Integrity
                </div>
                <div className="text-[11px] text-light-muted dark:text-dark-muted">
                  Verify downloads with SHA-256 checksums
                </div>
              </div>
            </div>
            <a
              href={`${releaseBaseUrl}/SHA256SUMS.txt`}
              target="_blank"
              rel="noopener noreferrer"
              className="px-3 py-1.5 rounded-lg border border-light-border dark:border-dark-border hover:bg-light-elevated dark:hover:bg-dark-elevated text-light-text dark:text-dark-text font-mono text-[11px] flex items-center gap-1"
            >
              <span>SHA256SUMS.txt</span>
              <ExternalLink className="w-3 h-3 text-light-dim" />
            </a>
          </div>

          {/* Upcoming Platforms Roadmap */}
          <div className="p-4 rounded-xl bg-light-surface dark:bg-dark-surface border border-light-border dark:border-dark-border flex items-center justify-between">
            <div className="flex items-center gap-2.5">
              <Clock className="w-4 h-4 text-light-dim dark:text-dark-dim" />
              <div>
                <div className="font-semibold text-light-text dark:text-dark-text">
                  macOS, Linux & iOS
                </div>
                <div className="text-[11px] text-light-muted dark:text-dark-muted">
                  Planned in upcoming releases
                </div>
              </div>
            </div>
            <span className="px-2.5 py-1 rounded-full text-[10px] font-mono font-medium bg-light-elevated dark:bg-dark-elevated text-light-dim dark:text-dark-dim border border-light-border dark:border-dark-border">
              In Development
            </span>
          </div>
        </div>

        {/* Honest Installation Guidance */}
        <div className="mt-8 p-5 rounded-xl bg-light-surface dark:bg-dark-surface border border-light-border dark:border-dark-border max-w-4xl mx-auto text-xs text-light-muted dark:text-dark-muted space-y-2 text-left">
          <div className="flex items-center gap-2 font-bold text-light-text dark:text-dark-text">
            <Info className="w-4 h-4 text-shushly-sapphire" />
            <span>Installation Notes & Platform Permissions</span>
          </div>
          <p>
            • <strong>Windows Administrator Prompt:</strong> Windows requires administrator elevation (UAC) whenever an application modifies network adapter DNS settings. Shushly uses this solely to configure your chosen DoH resolver and automatically resets it to DHCP when closed. As an independent open-source release without an expensive corporate Authenticode certificate, Windows SmartScreen may show an unrecognized publisher prompt on initial download.
          </p>
          <p>
            • <strong>Android VPN Dialog:</strong> Android requires approval for its system VPN service to create a local virtual network adapter (<code className="font-mono">10.233.0.1/32</code>). Shushly intercepts <em>only</em> DNS lookups; your regular app traffic, passwords, and web streams never traverse the tunnel.
          </p>
        </div>
      </div>
    </section>
  );
};
