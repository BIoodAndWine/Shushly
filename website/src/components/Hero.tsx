import React from 'react';
import { Download, Github, CheckCircle2 } from 'lucide-react';
import { AppSimulator } from './AppSimulator';
import { soundPlayer } from '../audio/soundPlayer';

interface HeroProps {
  isDark: boolean;
}

export const Hero: React.FC<HeroProps> = ({ isDark }) => {
  return (
    <section className="relative pt-12 pb-20 sm:pt-20 sm:pb-28 overflow-hidden">
      {/* Subtle Ambient Background Gradients */}
      <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[360px] bg-shushly-sapphire/10 dark:bg-shushly-sapphire/15 blur-[120px] rounded-full pointer-events-none -z-10" />

      <div className="max-w-6xl mx-auto px-4 sm:px-6">
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 lg:gap-8 items-center">
          {/* Left Column: Editorial Value Proposition */}
          <div className="lg:col-span-6 text-center lg:text-left space-y-6">
            {/* Trust Pill */}
            <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full text-xs font-medium bg-light-surface dark:bg-dark-surface border border-light-border dark:border-dark-border text-light-muted dark:text-dark-muted shadow-sm">
              <span className="w-2 h-2 rounded-full bg-shushly-emerald animate-pulse" />
              <span>v0.1.2 Production Release · 100% Free & Open Source</span>
            </div>

            {/* Main Headline */}
            <h1 className="text-4xl sm:text-5xl lg:text-6xl font-extrabold tracking-tight text-light-text dark:text-dark-text leading-[1.08]">
              A quieter internet, <br className="hidden sm:inline" />
              <span className="text-shushly-sapphire dark:text-shushly-sapphire-light">
                in one tap.
              </span>
            </h1>

            {/* Supporting Copy */}
            <p className="text-base sm:text-lg text-light-muted dark:text-dark-muted max-w-xl mx-auto lg:mx-0 leading-relaxed">
              Block ads and surveillance trackers with free, open-source DNS protection.
              No subscriptions. No user accounts. No complicated setup.
            </p>

            {/* Quick Guarantees */}
            <div className="flex flex-wrap items-center justify-center lg:justify-start gap-4 pt-1 text-xs text-light-muted dark:text-dark-muted">
              <div className="flex items-center gap-1.5">
                <CheckCircle2 className="w-4 h-4 text-shushly-emerald" />
                <span>Zero telemetry</span>
              </div>
              <div className="flex items-center gap-1.5">
                <CheckCircle2 className="w-4 h-4 text-shushly-emerald" />
                <span>No paywalls</span>
              </div>
              <div className="flex items-center gap-1.5">
                <CheckCircle2 className="w-4 h-4 text-shushly-emerald" />
                <span>RFC 8484 DoH</span>
              </div>
            </div>

            {/* CTA Buttons */}
            <div className="flex flex-col sm:flex-row items-center justify-center lg:justify-start gap-3.5 pt-2">
              <a
                href="#downloads"
                onClick={() => soundPlayer.play('click', 0.3)}
                className="w-full sm:w-auto inline-flex items-center justify-center gap-2.5 px-6 py-3.5 rounded-full text-sm font-bold bg-shushly-sapphire hover:bg-shushly-sapphire-dark text-white transition-all shadow-md hover:shadow-glow-sapphire active:scale-95 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-shushly-sapphire"
              >
                <Download className="w-4 h-4 stroke-[2.5]" />
                <span>Download Shushly</span>
              </a>

              <a
                href="https://github.com/BIoodAndWine/Shushly"
                target="_blank"
                rel="noopener noreferrer"
                onClick={() => soundPlayer.play('click', 0.2)}
                className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-5 py-3.5 rounded-full text-sm font-semibold bg-light-surface dark:bg-dark-surface hover:bg-light-elevated dark:hover:bg-dark-elevated border border-light-border dark:border-dark-border text-light-text dark:text-dark-text transition-all hover:border-shushly-sapphire/40 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-shushly-sapphire"
              >
                <Github className="w-4 h-4" />
                <span>View on GitHub</span>
              </a>
            </div>

            {/* Platform Availability Note */}
            <p className="text-[11px] text-light-dim dark:text-dark-dim pt-1">
              Verified for <strong className="text-light-text dark:text-dark-text">Windows 10/11</strong> and <strong className="text-light-text dark:text-dark-text">Android 8.0–15</strong>. macOS & Linux in development.
            </p>
          </div>

          {/* Right Column: Interactive Live Application Simulator */}
          <div className="lg:col-span-6 flex justify-center">
            <AppSimulator isDark={isDark} />
          </div>
        </div>
      </div>
    </section>
  );
};
