import React from 'react';
import {
  Zap,
  Layers,
  Lock,
  Sliders,
  EyeOff,
  Code2,
} from 'lucide-react';

export const Features: React.FC = () => {
  const features = [
    {
      icon: Zap,
      title: 'One-click protection',
      description:
        'No network routing configurations, manual DNS IP entry, or complex rulesets. Tap the power toggle and Shushly shields your connection immediately.',
      badge: 'Zero Configuration',
    },
    {
      icon: Layers,
      title: 'Three curated modes',
      description:
        'Choose between Light (default, 100% site compatibility), Balanced (comprehensive OISD Full protection), or Strict (maximum HaGeZi Pro shield).',
      badge: 'Customizable',
    },
    {
      icon: Lock,
      title: 'Encrypted DNS-over-HTTPS',
      description:
        'All domain lookups travel through encrypted RFC 8484 TLS tunnels. Your ISP, local hotspot operators, and eavesdroppers cannot inspect your queries.',
      badge: 'RFC 8484 TLS',
    },
    {
      icon: Sliders,
      title: '100% free Advanced Mode',
      description:
        'Connect your own NextDNS profile, Pi-hole instance, or personal AdGuard Home. Switch freely between global zero-log resolvers with real-time latency checks.',
      badge: 'Unrestricted',
    },
    {
      icon: EyeOff,
      title: 'Zero logs, zero telemetry',
      description:
        'Shushly contains no Google Analytics, no Firebase Crashlytics, and no ad SDKs. Your queries never touch our servers, because we do not operate any servers.',
      badge: 'Absolute Privacy',
    },
    {
      icon: Code2,
      title: 'Open source & verifiable',
      description:
        'Published under the MIT license on GitHub. Every line of Kotlin Multiplatform code, workflow file, and cryptographic build digest is public for inspection.',
      badge: 'MIT Licensed',
    },
  ];

  return (
    <section id="features" className="py-20 sm:py-28 border-t border-light-border/80 dark:border-dark-border/80 bg-light-surface/50 dark:bg-dark-surface/30">
      <div className="max-w-6xl mx-auto px-4 sm:px-6">
        {/* Section Header */}
        <div className="max-w-2xl mx-auto text-center mb-16">
          <h2 className="text-xs font-mono font-semibold tracking-wider text-shushly-sapphire dark:text-shushly-sapphire-light uppercase mb-2">
            Engineered for simplicity
          </h2>
          <p className="text-3xl sm:text-4xl font-extrabold tracking-tight text-light-text dark:text-dark-text">
            Everything you need. Nothing you don't.
          </p>
          <p className="mt-4 text-sm sm:text-base text-light-muted dark:text-dark-muted">
            Designed for ordinary people who want privacy without becoming network engineers.
          </p>
        </div>

        {/* Feature Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 sm:gap-8">
          {features.map((feat, index) => {
            const Icon = feat.icon;
            return (
              <div
                key={index}
                className="p-6 rounded-2xl bg-light-surface dark:bg-dark-surface border border-light-border dark:border-dark-border hover:border-shushly-sapphire/40 transition-all duration-200 shadow-sm flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center justify-between mb-4">
                    <div className="w-10 h-10 rounded-xl bg-shushly-sapphire/10 text-shushly-sapphire dark:text-shushly-sapphire-light flex items-center justify-center">
                      <Icon className="w-5 h-5 stroke-[2]" />
                    </div>
                    <span className="text-[10px] font-mono font-medium px-2 py-0.5 rounded-full bg-light-elevated dark:bg-dark-elevated text-light-muted dark:text-dark-muted border border-light-border dark:border-dark-border">
                      {feat.badge}
                    </span>
                  </div>
                  <h3 className="text-base font-bold text-light-text dark:text-dark-text mb-2">
                    {feat.title}
                  </h3>
                  <p className="text-xs sm:text-sm text-light-muted dark:text-dark-muted leading-relaxed">
                    {feat.description}
                  </p>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
};
