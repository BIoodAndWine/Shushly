import React from 'react';
import { Github, Lock, Users, Bug } from 'lucide-react';
import { soundPlayer } from '../audio/soundPlayer';

export const OpenSourceTrust: React.FC = () => {
  const trustPoints = [
    {
      icon: Github,
      title: 'Public Source Code',
      description: 'Every component, background service, and network engine line is public on GitHub under the MIT License.',
      linkText: 'Browse Repository',
      linkUrl: 'https://github.com/BIoodAndWine/Shushly',
    },
    {
      icon: Lock,
      title: 'No Centralized Servers',
      description: 'Shushly connects directly to established public DoH providers. We operate no proxies and store no query records.',
      linkText: 'DNS Providers Spec',
      linkUrl: 'https://github.com/BIoodAndWine/Shushly/blob/main/DNS_PROVIDERS.md',
    },
    {
      icon: Bug,
      title: 'Security Vulnerability Disclosure',
      description: 'Documented security policy with responsible disclosure reporting guidelines directly to the maintainer.',
      linkText: 'Read SECURITY.md',
      linkUrl: 'https://github.com/BIoodAndWine/Shushly/blob/main/SECURITY.md',
    },
    {
      icon: Users,
      title: 'Open Community Governance',
      description: 'Welcoming community contributions, transparent issue tracking, and public pull request review.',
      linkText: 'Contribution Guidelines',
      linkUrl: 'https://github.com/BIoodAndWine/Shushly/blob/main/CONTRIBUTING.md',
    },
  ];

  return (
    <section className="py-20 sm:py-28 border-t border-light-border/80 dark:border-dark-border/80">
      <div className="max-w-6xl mx-auto px-4 sm:px-6">
        <div className="max-w-2xl mx-auto text-center mb-16">
          <h2 className="text-xs font-mono font-semibold tracking-wider text-shushly-sapphire dark:text-shushly-sapphire-light uppercase mb-2">
            Verifiable transparency
          </h2>
          <p className="text-3xl sm:text-4xl font-extrabold tracking-tight text-light-text dark:text-dark-text">
            Trust is earned through public code
          </p>
          <p className="mt-4 text-sm sm:text-base text-light-muted dark:text-dark-muted">
            Security software should never rely on blind faith. Inspect our code, build it yourself, or verify our release binaries.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
          {trustPoints.map((item, i) => {
            const Icon = item.icon;
            return (
              <div
                key={i}
                className="p-6 rounded-2xl bg-light-surface dark:bg-dark-surface border border-light-border dark:border-dark-border flex flex-col justify-between"
              >
                <div>
                  <div className="w-10 h-10 rounded-xl bg-shushly-sapphire/10 text-shushly-sapphire dark:text-shushly-sapphire-light flex items-center justify-center mb-4">
                    <Icon className="w-5 h-5" />
                  </div>
                  <h3 className="text-base font-bold text-light-text dark:text-dark-text mb-2">
                    {item.title}
                  </h3>
                  <p className="text-xs text-light-muted dark:text-dark-muted leading-relaxed mb-4">
                    {item.description}
                  </p>
                </div>
                <a
                  href={item.linkUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  onClick={() => soundPlayer.play('click', 0.2)}
                  className="text-xs font-semibold text-shushly-sapphire dark:text-shushly-sapphire-light hover:underline inline-flex items-center gap-1"
                >
                  <span>{item.linkText}</span>
                  <span aria-hidden="true">&rarr;</span>
                </a>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
};
