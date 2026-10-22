import React from 'react';
import { Download, Sliders, Power, ShieldAlert, Check } from 'lucide-react';

export const HowItWorks: React.FC = () => {
  const steps = [
    {
      num: '01',
      icon: Download,
      title: 'Install Shushly',
      description:
        'Download the verified installer for Windows or the signed APK for Android. No account creation or registration required.',
    },
    {
      num: '02',
      icon: Sliders,
      title: 'Pick a protection mode',
      description:
        'Light is selected by default for maximum web compatibility. Switch to Balanced or Strict at any time with a single tap.',
    },
    {
      num: '03',
      icon: Power,
      title: 'Tap to connect',
      description:
        'Flip the power toggle. Shushly immediately routes DNS lookups through your encrypted resolver, dropping ad queries instantly.',
    },
  ];

  return (
    <section id="how-it-works" className="py-20 sm:py-28 border-t border-light-border/80 dark:border-dark-border/80">
      <div className="max-w-6xl mx-auto px-4 sm:px-6">
        <div className="max-w-2xl mx-auto text-center mb-16">
          <h2 className="text-xs font-mono font-semibold tracking-wider text-shushly-sapphire dark:text-shushly-sapphire-light uppercase mb-2">
            Simplicity by design
          </h2>
          <p className="text-3xl sm:text-4xl font-extrabold tracking-tight text-light-text dark:text-dark-text">
            Up and running in thirty seconds
          </p>
          <p className="mt-4 text-sm sm:text-base text-light-muted dark:text-dark-muted">
            No technical knowledge required. Shushly handles the networking under the hood.
          </p>
        </div>

        {/* 3 Steps */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-16">
          {steps.map((step) => {
            const Icon = step.icon;
            return (
              <div
                key={step.num}
                className="relative p-6 rounded-2xl bg-light-surface dark:bg-dark-surface border border-light-border dark:border-dark-border flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center justify-between mb-5">
                    <span className="font-mono text-2xl font-black text-shushly-sapphire/30 dark:text-shushly-sapphire-light/30">
                      {step.num}
                    </span>
                    <div className="w-10 h-10 rounded-xl bg-shushly-sapphire/10 text-shushly-sapphire dark:text-shushly-sapphire-light flex items-center justify-center">
                      <Icon className="w-5 h-5" />
                    </div>
                  </div>
                  <h3 className="text-lg font-bold text-light-text dark:text-dark-text mb-2">
                    {step.title}
                  </h3>
                  <p className="text-xs sm:text-sm text-light-muted dark:text-dark-muted leading-relaxed">
                    {step.description}
                  </p>
                </div>
              </div>
            );
          })}
        </div>

        {/* Technical Honesty Note: DNS Filtering vs Full-Tunnel VPN */}
        <div className="p-6 sm:p-8 rounded-2xl bg-light-surface dark:bg-dark-surface border border-light-border dark:border-dark-border max-w-4xl mx-auto">
          <div className="flex flex-col sm:flex-row items-start gap-4">
            <div className="p-2.5 rounded-xl bg-amber-500/10 text-shushly-amber shrink-0 mt-0.5">
              <ShieldAlert className="w-6 h-6" />
            </div>
            <div className="space-y-3 text-left">
              <h4 className="text-sm font-bold text-light-text dark:text-dark-text">
                Understanding DNS filtering: What Shushly does and does not do
              </h4>
              <p className="text-xs sm:text-sm text-light-muted dark:text-dark-muted leading-relaxed">
                Shushly functions at the <strong>DNS resolution layer</strong>. When an app or website tries to connect to a known advertising or surveillance tracking server, Shushly returns a null address, preventing the tracker from loading before it ever connects.
              </p>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1 text-xs text-light-muted dark:text-dark-muted">
                <div className="flex items-start gap-2">
                  <Check className="w-4 h-4 text-shushly-emerald shrink-0 mt-0.5" />
                  <span>
                    <strong>Zero payload inspection:</strong> Shushly never reads or proxies your web pages, emails, video streams, or private passwords.
                  </span>
                </div>
                <div className="flex items-start gap-2">
                  <Check className="w-4 h-4 text-shushly-emerald shrink-0 mt-0.5" />
                  <span>
                    <strong>First-party ad boundaries:</strong> Cannot block ads served from the exact same domain as video content (such as first-party YouTube player streams).
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};
