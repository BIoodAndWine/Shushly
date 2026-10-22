import React, { useState } from 'react';
import { Laptop, Smartphone, Sliders, Check } from 'lucide-react';
import { soundPlayer } from '../audio/soundPlayer';

export const AppShowcase: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'desktop' | 'mobile' | 'advanced'>('desktop');

  return (
    <section id="showcase" className="py-20 sm:py-28 border-t border-light-border/80 dark:border-dark-border/80">
      <div className="max-w-6xl mx-auto px-4 sm:px-6">
        <div className="max-w-2xl mx-auto text-center mb-14">
          <h2 className="text-xs font-mono font-semibold tracking-wider text-shushly-sapphire dark:text-shushly-sapphire-light uppercase mb-2">
            Native craftsmanship
          </h2>
          <p className="text-3xl sm:text-4xl font-extrabold tracking-tight text-light-text dark:text-dark-text">
            Built for Windows and Android
          </p>
          <p className="mt-4 text-sm sm:text-base text-light-muted dark:text-dark-muted">
            Engineered with Kotlin Multiplatform and Compose Multiplatform for fluid 60 FPS performance and genuine system integration.
          </p>

          {/* Showcase Tabs */}
          <div className="inline-flex p-1 mt-8 bg-light-surface dark:bg-dark-surface border border-light-border dark:border-dark-border rounded-xl text-xs font-semibold shadow-sm">
            <button
              type="button"
              onClick={() => {
                soundPlayer.play('click', 0.2);
                setActiveTab('desktop');
              }}
              className={`flex items-center gap-2 px-4 py-2 rounded-lg transition-all ${
                activeTab === 'desktop'
                  ? 'bg-shushly-sapphire text-white shadow-sm'
                  : 'text-light-muted dark:text-dark-muted hover:text-light-text dark:hover:text-dark-text'
              }`}
            >
              <Laptop className="w-4 h-4" />
              Windows Client
            </button>
            <button
              type="button"
              onClick={() => {
                soundPlayer.play('click', 0.2);
                setActiveTab('mobile');
              }}
              className={`flex items-center gap-2 px-4 py-2 rounded-lg transition-all ${
                activeTab === 'mobile'
                  ? 'bg-shushly-sapphire text-white shadow-sm'
                  : 'text-light-muted dark:text-dark-muted hover:text-light-text dark:hover:text-dark-text'
              }`}
            >
              <Smartphone className="w-4 h-4" />
              Android Mobile
            </button>
            <button
              type="button"
              onClick={() => {
                soundPlayer.play('click', 0.2);
                setActiveTab('advanced');
              }}
              className={`flex items-center gap-2 px-4 py-2 rounded-lg transition-all ${
                activeTab === 'advanced'
                  ? 'bg-shushly-sapphire text-white shadow-sm'
                  : 'text-light-muted dark:text-dark-muted hover:text-light-text dark:hover:text-dark-text'
              }`}
            >
              <Sliders className="w-4 h-4" />
              Advanced Mode
            </button>
          </div>
        </div>

        {/* Tab 1: Windows Client Showcase */}
        {activeTab === 'desktop' && (
          <div className="max-w-4xl mx-auto rounded-2xl bg-light-surface dark:bg-dark-surface border border-light-border dark:border-dark-border p-6 sm:p-10 shadow-xl grid grid-cols-1 md:grid-cols-2 gap-8 items-center animate-in fade-in duration-300">
            <div className="space-y-4">
              <span className="text-xs font-mono font-medium px-2.5 py-1 rounded-full bg-shushly-sapphire/10 text-shushly-sapphire dark:text-shushly-sapphire-light">
                Windows 10 / 11 Native
              </span>
              <h3 className="text-2xl font-bold text-light-text dark:text-dark-text">
                Unobtrusive background defense for PC
              </h3>
              <p className="text-sm text-light-muted dark:text-dark-muted leading-relaxed">
                Runs silently on Windows loopback (<code className="font-mono text-xs">127.0.0.1:53</code>) without consuming background memory. Configures adapter DNS automatically and cleanly restores your DHCP settings when closed.
              </p>
              <ul className="space-y-2 text-xs text-light-muted dark:text-dark-muted">
                <li className="flex items-center gap-2">
                  <Check className="w-4 h-4 text-shushly-emerald" />
                  <span>WiX Toolset verified MSI and automated Setup EXE</span>
                </li>
                <li className="flex items-center gap-2">
                  <Check className="w-4 h-4 text-shushly-emerald" />
                  <span>Portable zero-install standalone ZIP option</span>
                </li>
                <li className="flex items-center gap-2">
                  <Check className="w-4 h-4 text-shushly-emerald" />
                  <span>Graceful shutdown hooks prevent broken DNS on exit</span>
                </li>
              </ul>
            </div>

            {/* Visual Representation */}
            <div className="rounded-xl border border-light-border dark:border-dark-border bg-light-bg dark:bg-dark-bg p-5 shadow-inner">
              <div className="flex items-center justify-between pb-3 border-b border-light-border dark:border-dark-border text-xs">
                <div className="flex items-center gap-2 font-semibold">
                  <img src="/icon.png" alt="Shushly" className="w-4 h-4 rounded" />
                  <span>Shushly for Windows</span>
                </div>
                <span className="text-[11px] font-mono text-shushly-emerald flex items-center gap-1">
                  <span className="w-1.5 h-1.5 rounded-full bg-shushly-emerald animate-pulse" />
                  127.0.0.1:53 Active
                </span>
              </div>
              <div className="py-6 flex flex-col items-center text-center">
                <div className="w-28 h-14 rounded-full bg-shushly-sapphire p-1 flex items-center justify-end shadow-glow-sapphire mb-3">
                  <div className="w-12 h-12 rounded-full bg-white flex items-center justify-center text-shushly-sapphire shadow">
                    <Check className="w-6 h-6 stroke-[3]" />
                  </div>
                </div>
                <div className="text-sm font-bold text-light-text dark:text-dark-text">
                  Protected with Shushly
                </div>
                <div className="text-xs text-light-muted dark:text-dark-muted mt-1">
                  Filtering lookups via Control D Ads & Tracking
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Tab 2: Android Mobile Showcase */}
        {activeTab === 'mobile' && (
          <div className="max-w-4xl mx-auto rounded-2xl bg-light-surface dark:bg-dark-surface border border-light-border dark:border-dark-border p-6 sm:p-10 shadow-xl grid grid-cols-1 md:grid-cols-2 gap-8 items-center animate-in fade-in duration-300">
            <div className="space-y-4">
              <span className="text-xs font-mono font-medium px-2.5 py-1 rounded-full bg-shushly-emerald/10 text-shushly-emerald-dark dark:text-shushly-emerald">
                Android 8.0 - 15
              </span>
              <h3 className="text-2xl font-bold text-light-text dark:text-dark-text">
                DNS-only loopback tunnel. Zero battery drain.
              </h3>
              <p className="text-sm text-light-muted dark:text-dark-muted leading-relaxed">
                Uses Android's native <code className="font-mono text-xs">VpnService</code> strictly as a DNS interceptor (<code className="font-mono text-xs">10.233.0.1/32</code>). Unlike full-tunnel VPNs, your web browsing, video streams, and game traffic never pass through a VPN tunnel.
              </p>
              <ul className="space-y-2 text-xs text-light-muted dark:text-dark-muted">
                <li className="flex items-center gap-2">
                  <Check className="w-4 h-4 text-shushly-emerald" />
                  <span>Production signed APK with persistent key (<code className="font-mono text-[11px]">CN=Karl Molles</code>)</span>
                </li>
                <li className="flex items-center gap-2">
                  <Check className="w-4 h-4 text-shushly-emerald" />
                  <span>Seamless in-place updates from GitHub Releases</span>
                </li>
                <li className="flex items-center gap-2">
                  <Check className="w-4 h-4 text-shushly-emerald" />
                  <span>Always-on VPN and Quick Settings tile integration ready</span>
                </li>
              </ul>
            </div>

            {/* Mobile Visual */}
            <div className="max-w-[280px] mx-auto rounded-[32px] border-[5px] border-slate-300 dark:border-slate-800 bg-light-bg dark:bg-dark-bg p-5 shadow-2xl text-center">
              <div className="w-2.5 h-2.5 rounded-full bg-slate-900 dark:bg-slate-700 mx-auto mb-4" />
              <img src="/shushly-logo.png" alt="Shushly" className="w-10 h-10 rounded-xl mx-auto mb-3 shadow" />
              <div className="text-xs font-bold text-light-text dark:text-dark-text mb-1">
                Shushly Mobile
              </div>
              <div className="text-[11px] text-shushly-emerald mb-4 font-mono">
                ● Connected (DoH)
              </div>
              <div className="w-24 h-12 rounded-full bg-shushly-sapphire p-1 mx-auto flex items-center justify-end shadow-glow-sapphire mb-4">
                <div className="w-10 h-10 rounded-full bg-white flex items-center justify-center text-shushly-sapphire">
                  <Check className="w-5 h-5 stroke-[3]" />
                </div>
              </div>
              <div className="p-2.5 rounded-xl bg-light-surface dark:bg-dark-surface border border-light-border dark:border-dark-border text-[11px] text-light-muted dark:text-dark-muted">
                Mode: <strong>Light (Default)</strong>
              </div>
            </div>
          </div>
        )}

        {/* Tab 3: Advanced Mode Showcase */}
        {activeTab === 'advanced' && (
          <div className="max-w-4xl mx-auto rounded-2xl bg-light-surface dark:bg-dark-surface border border-light-border dark:border-dark-border p-6 sm:p-10 shadow-xl grid grid-cols-1 md:grid-cols-2 gap-8 items-center animate-in fade-in duration-300">
            <div className="space-y-4">
              <span className="text-xs font-mono font-medium px-2.5 py-1 rounded-full bg-purple-500/10 text-purple-600 dark:text-purple-400">
                Power Users & Custom Profiles
              </span>
              <h3 className="text-2xl font-bold text-light-text dark:text-dark-text">
                Connect your custom DoH endpoints
              </h3>
              <p className="text-sm text-light-muted dark:text-dark-muted leading-relaxed">
                Already have a customized NextDNS configuration, personal Pi-hole DoH tunnel, or corporate DNS filter? Enter your custom HTTPS URL directly with zero restrictions.
              </p>
              <ul className="space-y-2 text-xs text-light-muted dark:text-dark-muted">
                <li className="flex items-center gap-2">
                  <Check className="w-4 h-4 text-shushly-emerald" />
                  <span>Supports NextDNS, AdGuard Home, Pi-hole, and Cloudflare Teams</span>
                </li>
                <li className="flex items-center gap-2">
                  <Check className="w-4 h-4 text-shushly-emerald" />
                  <span>Built-in directory with live latency pings to global providers</span>
                </li>
                <li className="flex items-center gap-2">
                  <Check className="w-4 h-4 text-shushly-emerald" />
                  <span>Automatic failover ensures continuous DNS availability</span>
                </li>
              </ul>
            </div>

            {/* Advanced Directory Visual */}
            <div className="rounded-xl border border-light-border dark:border-dark-border bg-light-bg dark:bg-dark-bg p-4 shadow-inner text-xs space-y-2">
              <div className="font-semibold text-light-text dark:text-dark-text pb-2 border-b border-light-border dark:border-dark-border flex items-center justify-between">
                <span>Directory of Verified Resolvers</span>
                <span className="font-mono text-[10px] text-shushly-sapphire">RFC 8484</span>
              </div>
              {[
                { name: 'Control D Ads & Tracking', host: 'freedns.controld.com', ping: '12ms' },
                { name: 'Control D OISD Full', host: 'freedns.controld.com', ping: '14ms' },
                { name: 'Control D HaGeZi Pro', host: 'freedns.controld.com', ping: '15ms' },
                { name: 'AdGuard DNS Default', host: 'dns.adguard-dns.com', ping: '18ms' },
                { name: 'dnsforge.de (No-Log)', host: 'dnsforge.de', ping: '24ms' },
                { name: 'LibreDNS (Privacy)', host: 'doh.libredns.gr', ping: '29ms' },
              ].map((res, i) => (
                <div
                  key={i}
                  className="flex items-center justify-between p-2 rounded-lg bg-light-surface dark:bg-dark-surface border border-light-border/60 dark:border-dark-border/60"
                >
                  <div>
                    <div className="font-medium text-light-text dark:text-dark-text">{res.name}</div>
                    <div className="text-[10px] text-light-dim dark:text-dark-dim font-mono">{res.host}</div>
                  </div>
                  <span className="font-mono text-[10px] text-shushly-emerald px-1.5 py-0.5 rounded bg-shushly-emerald/10">
                    {res.ping}
                  </span>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </section>
  );
};
