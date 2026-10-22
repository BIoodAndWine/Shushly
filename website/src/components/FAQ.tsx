import React, { useState } from 'react';
import { ChevronDown } from 'lucide-react';
import { soundPlayer } from '../audio/soundPlayer';

export const FAQ: React.FC = () => {
  const [openIndex, setOpenIndex] = useState<number | null>(0);

  const faqs = [
    {
      q: 'Is Shushly really free?',
      a: 'Yes, 100% free and open-source under the MIT License. There are no paid tiers, subscriptions, paywalls, or artificial limitations. All features, including Advanced Mode and custom DoH resolvers, are unrestricted.',
    },
    {
      q: 'Does Shushly block all advertisements?',
      a: 'Shushly blocks advertising, tracking, and telemetry domains at the DNS level. When an application attempts to look up an ad server, Shushly intercepts and drops the request. However, it cannot block first-party ads served from the exact same domain as the content you are viewing (such as YouTube video ads served from the video stream itself).',
    },
    {
      q: 'Is Shushly a full-tunnel VPN?',
      a: 'No. Shushly does not tunnel your general web traffic through a remote VPN server. On Android, it uses the local VpnService interface strictly as a virtual DNS loopback (10.233.0.1/32). Your actual web browsing, emails, video streams, and personal passwords never leave your device through a proxy.',
    },
    {
      q: 'Does Shushly track or log my browsing activity?',
      a: 'No. Shushly contains zero analytics SDKs, zero crash trackers, and zero telemetry. We do not operate any centralized servers or log query history. Queries travel directly from your device to your selected upstream DNS-over-HTTPS resolver over encrypted TLS.',
    },
    {
      q: 'Which operating systems are supported?',
      a: 'Shushly currently provides production builds for Windows 10/11 (MSI, Setup EXE, and Portable ZIP) and Android 8.0 through 15 (Signed APK). Support for macOS, Linux, and iOS is planned for future releases.',
    },
    {
      q: 'What is the difference between Light, Balanced, and Strict?',
      a: 'Light (default) uses Control D Ads & Tracking for everyday ad blocking with 100% website compatibility. Balanced uses OISD Full for comprehensive ad and telemetry protection. Strict uses HaGeZi Pro for maximum privacy, blocking telemetry and affiliate redirectors, which may occasionally require allowlisting for niche shopping links.',
    },
    {
      q: 'Can I use custom DNS providers?',
      a: 'Yes. In Advanced Mode, you can enter any custom DNS-over-HTTPS endpoint URL (such as your personal NextDNS profile, Pi-hole instance, or AdGuard Home) with complete freedom.',
    },
    {
      q: 'Why does Android request VPN permission?',
      a: 'Android restricts local DNS interception unless an application initializes a local VpnService. Shushly configures the TUN interface solely for DNS routes (10.233.0.1/32 and fd00:1::1/128). It does not capture or proxy any other network traffic.',
    },
    {
      q: 'Why might Windows request administrator privileges?',
      a: 'Windows requires administrator elevation (UAC) to configure network adapter DNS settings. Shushly uses this solely to point your active network adapter to its local loopback proxy (127.0.0.1:53) and cleanly restores your automatic DHCP settings when closed.',
    },
    {
      q: 'How do I report bugs or contribute?',
      a: 'Visit our public GitHub repository at github.com/BIoodAndWine/Shushly to report issues, suggest improvements, or submit pull requests. You can also contact the maintainer directly at contact@karlmolles.com.',
    },
  ];

  const toggle = (idx: number) => {
    soundPlayer.play('click', 0.2);
    setOpenIndex(openIndex === idx ? null : idx);
  };

  return (
    <section id="faq" className="py-20 sm:py-28 border-t border-light-border/80 dark:border-dark-border/80 bg-light-surface/40 dark:bg-dark-surface/20">
      <div className="max-w-4xl mx-auto px-4 sm:px-6">
        <div className="text-center mb-14">
          <h2 className="text-xs font-mono font-semibold tracking-wider text-shushly-sapphire dark:text-shushly-sapphire-light uppercase mb-2">
            Got questions?
          </h2>
          <p className="text-3xl sm:text-4xl font-extrabold tracking-tight text-light-text dark:text-dark-text">
            Frequently Asked Questions
          </p>
          <p className="mt-3 text-sm text-light-muted dark:text-dark-muted">
            Clear, honest answers without technical jargon or marketing spin.
          </p>
        </div>

        <div className="space-y-3">
          {faqs.map((faq, idx) => {
            const isOpen = openIndex === idx;
            return (
              <div
                key={idx}
                className="rounded-2xl border border-light-border dark:border-dark-border bg-light-surface dark:bg-dark-surface overflow-hidden transition-colors"
              >
                <button
                  type="button"
                  onClick={() => toggle(idx)}
                  className="w-full flex items-center justify-between p-5 sm:p-6 text-left font-bold text-sm sm:text-base text-light-text dark:text-dark-text hover:text-shushly-sapphire transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-shushly-sapphire"
                >
                  <span>{faq.q}</span>
                  <ChevronDown
                    className={`w-4 h-4 text-light-dim dark:text-dark-dim shrink-0 transition-transform duration-200 ${
                      isOpen ? 'rotate-180 text-shushly-sapphire' : ''
                    }`}
                  />
                </button>
                {isOpen && (
                  <div className="px-5 pb-5 sm:px-6 sm:pb-6 text-xs sm:text-sm text-light-muted dark:text-dark-muted leading-relaxed border-t border-light-border/60 dark:border-dark-border/60 pt-4">
                    {faq.a}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
};
