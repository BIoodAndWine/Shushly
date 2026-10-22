import React, { useState } from 'react';
import { Check, Sparkles } from 'lucide-react';
import { ProtectionLevel } from '../types';
import { soundPlayer } from '../audio/soundPlayer';

export const ProtectionLevels: React.FC = () => {
  const [activeLevel, setActiveLevel] = useState<ProtectionLevel>('LIGHT');

  const modes = [
    {
      id: 'LIGHT' as ProtectionLevel,
      title: 'Light',
      badge: 'Default',
      badgeColor: 'bg-shushly-sapphire/10 text-shushly-sapphire dark:text-shushly-sapphire-light',
      endpoint: 'https://freedns.controld.com/p2',
      upstream: 'Control D Ads & Tracking',
      compatibility: '100% Guaranteed',
      summary: 'Ideal for everyday browsing where website breakage cannot be tolerated.',
      features: [
        'Blocks banner ads, popups, and invasive video overlays',
        'Stops third-party ad-network tracking scripts',
        'Preserves affiliate shopping links and coupon portals',
        'Zero false positives on banking, streaming, and retail sites',
      ],
      recommendedFor: 'New users, family computers, and online shopping',
    },
    {
      id: 'BALANCED' as ProtectionLevel,
      title: 'Balanced',
      badge: 'Popular',
      badgeColor: 'bg-shushly-emerald/10 text-shushly-emerald-dark dark:text-shushly-emerald',
      endpoint: 'https://freedns.controld.com/x-oisd',
      upstream: 'Control D OISD Full',
      compatibility: '99% High',
      summary: 'Comprehensive ad, telemetry, and tracking shield using the renowned OISD Full list.',
      features: [
        'Extensive ad blocking across apps, web pages, and mobile games',
        'Neutralizes telemetry and background tracking beacons',
        'Blocks known phishing domains and malicious redirectors',
        'Carefully maintained to avoid breaking mainstream sites',
      ],
      recommendedFor: 'Privacy-conscious users wanting strong daily defense',
    },
    {
      id: 'STRICT' as ProtectionLevel,
      title: 'Strict',
      badge: 'Max Privacy',
      badgeColor: 'bg-purple-500/10 text-purple-600 dark:text-purple-400',
      endpoint: 'https://freedns.controld.com/x-hagezi-pro',
      upstream: 'Control D HaGeZi Pro',
      compatibility: 'Advanced (~95%)',
      summary: 'Aggressive multi-layered privacy shield powered by the HaGeZi Pro blocklist.',
      features: [
        'Stops virtually all ad networks, trackers, and telemetry services',
        'Blocks sponsored search results and affiliate redirection URLs',
        'Neutralizes smart TV and OEM device diagnostic beacons',
        'May require occasional allowlisting for aggressive affiliate links',
      ],
      recommendedFor: 'Advanced users demanding the highest level of privacy',
    },
  ];

  return (
    <section id="protection-modes" className="py-20 sm:py-28 border-t border-light-border/80 dark:border-dark-border/80 bg-light-surface/40 dark:bg-dark-surface/20">
      <div className="max-w-6xl mx-auto px-4 sm:px-6">
        <div className="max-w-2xl mx-auto text-center mb-16">
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-mono font-medium bg-shushly-emerald/10 text-shushly-emerald-dark dark:text-shushly-emerald mb-3">
            <Sparkles className="w-3.5 h-3.5" />
            <span>100% Free · No Tiers · No Paywalls</span>
          </div>
          <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-light-text dark:text-dark-text">
            Three levels of defense. You choose the balance.
          </h2>
          <p className="mt-4 text-sm sm:text-base text-light-muted dark:text-dark-muted">
            Every protection mode is included forever without subscriptions. Switch anytime with a single click.
          </p>
        </div>

        {/* Protection Mode Cards */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 items-stretch">
          {modes.map((mode) => {
            const isSelected = activeLevel === mode.id;
            return (
              <div
                key={mode.id}
                onClick={() => {
                  soundPlayer.play('mode_change', 0.4);
                  setActiveLevel(mode.id);
                }}
                className={`cursor-pointer rounded-2xl p-6 sm:p-7 flex flex-col justify-between transition-all duration-200 border ${
                  isSelected
                    ? 'bg-light-surface dark:bg-dark-surface border-shushly-sapphire ring-2 ring-shushly-sapphire/20 shadow-xl'
                    : 'bg-light-surface/70 dark:bg-dark-surface/50 border-light-border dark:border-dark-border hover:border-light-border-active dark:hover:border-dark-border-active shadow-sm'
                }`}
              >
                <div>
                  <div className="flex items-center justify-between mb-4">
                    <h3 className="text-xl font-bold text-light-text dark:text-dark-text">
                      {mode.title}
                    </h3>
                    <span className={`px-2.5 py-0.5 rounded-full text-xs font-mono font-semibold ${mode.badgeColor}`}>
                      {mode.badge}
                    </span>
                  </div>

                  <p className="text-xs sm:text-sm text-light-muted dark:text-dark-muted mb-6 leading-relaxed">
                    {mode.summary}
                  </p>

                  <div className="p-3 rounded-xl bg-light-elevated dark:bg-dark-elevated border border-light-border/60 dark:border-dark-border/60 text-xs font-mono space-y-1 mb-6">
                    <div className="text-[11px] text-light-dim dark:text-dark-dim">Upstream Filter:</div>
                    <div className="font-semibold text-light-text dark:text-dark-text truncate">
                      {mode.upstream}
                    </div>
                  </div>

                  <div className="space-y-3 mb-6">
                    <div className="text-xs font-semibold text-light-text dark:text-dark-text uppercase tracking-wider text-[11px]">
                      Filter capabilities:
                    </div>
                    {mode.features.map((feat, idx) => (
                      <div key={idx} className="flex items-start gap-2.5 text-xs text-light-muted dark:text-dark-muted">
                        <Check className="w-4 h-4 text-shushly-emerald shrink-0 mt-0.5" />
                        <span>{feat}</span>
                      </div>
                    ))}
                  </div>
                </div>

                <div className="pt-4 border-t border-light-border/60 dark:border-dark-border/60 text-[11px] text-light-dim dark:text-dark-dim">
                  <strong>Best for:</strong> {mode.recommendedFor}
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
};
