import React from 'react';
import { Mail, ExternalLink } from 'lucide-react';
import { soundPlayer } from '../audio/soundPlayer';

export const Footer: React.FC = () => {
  return (
    <footer className="border-t border-light-border dark:border-dark-border bg-light-surface dark:bg-dark-surface py-14 text-xs text-light-muted dark:text-dark-muted">
      <div className="max-w-6xl mx-auto px-4 sm:px-6">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-12">
          {/* Col 1: Brand & Purpose */}
          <div className="md:col-span-1 space-y-3">
            <div className="flex items-center gap-2.5">
              <img src="/shushly-logo.png" alt="Shushly" className="w-7 h-7 rounded-lg" />
              <span className="font-extrabold text-base text-light-text dark:text-dark-text tracking-tight">
                Shushly
              </span>
            </div>
            <p className="text-xs leading-relaxed text-light-muted dark:text-dark-muted">
              Free, open-source DNS ad and tracker blocker built with Kotlin Multiplatform and Compose.
            </p>
            <div className="pt-1 text-[11px] text-light-dim dark:text-dark-dim">
              Released under the <a href="https://github.com/BIoodAndWine/Shushly/blob/main/LICENSE" target="_blank" rel="noopener noreferrer" className="underline hover:text-shushly-sapphire">MIT License</a>.
            </div>
          </div>

          {/* Col 2: Navigation & Downloads */}
          <div className="space-y-2.5">
            <h4 className="font-bold text-light-text dark:text-dark-text text-xs uppercase tracking-wider">
              Product
            </h4>
            <ul className="space-y-2">
              <li>
                <a href="#features" onClick={() => soundPlayer.play('click', 0.2)} className="hover:text-shushly-sapphire transition-colors">
                  Features
                </a>
              </li>
              <li>
                <a href="#how-it-works" onClick={() => soundPlayer.play('click', 0.2)} className="hover:text-shushly-sapphire transition-colors">
                  How It Works
                </a>
              </li>
              <li>
                <a href="#protection-modes" onClick={() => soundPlayer.play('click', 0.2)} className="hover:text-shushly-sapphire transition-colors">
                  Protection Modes
                </a>
              </li>
              <li>
                <a href="#downloads" onClick={() => soundPlayer.play('click', 0.2)} className="hover:text-shushly-sapphire transition-colors font-semibold text-shushly-sapphire">
                  Download Shushly (v0.1.2)
                </a>
              </li>
            </ul>
          </div>

          {/* Col 3: Resources & Source */}
          <div className="space-y-2.5">
            <h4 className="font-bold text-light-text dark:text-dark-text text-xs uppercase tracking-wider">
              Open Source
            </h4>
            <ul className="space-y-2">
              <li>
                <a
                  href="https://github.com/BIoodAndWine/Shushly"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="hover:text-shushly-sapphire transition-colors inline-flex items-center gap-1"
                >
                  <span>GitHub Repository</span>
                  <ExternalLink className="w-3 h-3 text-light-dim" />
                </a>
              </li>
              <li>
                <a
                  href="https://github.com/BIoodAndWine/Shushly/releases"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="hover:text-shushly-sapphire transition-colors inline-flex items-center gap-1"
                >
                  <span>Releases & Checksums</span>
                  <ExternalLink className="w-3 h-3 text-light-dim" />
                </a>
              </li>
              <li>
                <a
                  href="https://github.com/BIoodAndWine/Shushly/blob/main/PRIVACY.md"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="hover:text-shushly-sapphire transition-colors inline-flex items-center gap-1"
                >
                  <span>Privacy Policy</span>
                  <ExternalLink className="w-3 h-3 text-light-dim" />
                </a>
              </li>
              <li>
                <a
                  href="https://github.com/BIoodAndWine/Shushly/blob/main/SECURITY.md"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="hover:text-shushly-sapphire transition-colors inline-flex items-center gap-1"
                >
                  <span>Security Vulnerability Policy</span>
                  <ExternalLink className="w-3 h-3 text-light-dim" />
                </a>
              </li>
            </ul>
          </div>

          {/* Col 4: Author & Contact */}
          <div className="space-y-2.5">
            <h4 className="font-bold text-light-text dark:text-dark-text text-xs uppercase tracking-wider">
              Contact & Creator
            </h4>
            <ul className="space-y-2">
              <li>
                <a
                  href="mailto:contact@karlmolles.com"
                  className="hover:text-shushly-sapphire transition-colors inline-flex items-center gap-1"
                >
                  <Mail className="w-3.5 h-3.5" />
                  <span>contact@karlmolles.com</span>
                </a>
              </li>
              <li>
                <a
                  href="https://karlmolles.com/"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="hover:text-shushly-sapphire transition-colors inline-flex items-center gap-1"
                >
                  <span>Creator: Karl Molles</span>
                  <ExternalLink className="w-3 h-3 text-light-dim" />
                </a>
              </li>
            </ul>
          </div>
        </div>

        {/* Bottom Bar */}
        <div className="pt-8 border-t border-light-border/60 dark:border-dark-border/60 flex flex-col sm:flex-row items-center justify-between gap-4 text-[11px] text-light-dim dark:text-dark-dim">
          <p>© {new Date().getFullYear()} Shushly. Open-source software released under the MIT License.</p>
          <div className="flex items-center gap-1">
            <span>Crafted with care by</span>
            <a
              href="https://karlmolles.com/"
              target="_blank"
              rel="noopener noreferrer"
              className="font-medium text-light-text dark:text-dark-text hover:text-shushly-sapphire underline"
            >
              Karl Molles
            </a>
          </div>
        </div>
      </div>
    </footer>
  );
};
