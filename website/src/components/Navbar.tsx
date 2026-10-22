import React, { useState } from 'react';
import { Volume2, VolumeX, Menu, X, Github, Download } from 'lucide-react';
import { AnimatedThemeToggle } from './AnimatedThemeToggle';
import { soundPlayer } from '../audio/soundPlayer';

interface NavbarProps {
  isDark: boolean;
  onToggleTheme: () => void;
  soundEnabled: boolean;
  onToggleSound: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({
  isDark,
  onToggleTheme,
  soundEnabled,
  onToggleSound,
}) => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const navLinks = [
    { label: 'Features', href: '#features' },
    { label: 'How It Works', href: '#how-it-works' },
    { label: 'Protection', href: '#protection-modes' },
    { label: 'Showcase', href: '#showcase' },
    { label: 'Downloads', href: '#downloads' },
    { label: 'FAQ', href: '#faq' },
  ];

  const handleLinkClick = () => {
    soundPlayer.play('click', 0.2);
    setMobileMenuOpen(false);
  };

  return (
    <header className="sticky top-0 z-50 w-full backdrop-blur-md bg-light-bg/85 dark:bg-dark-bg/85 border-b border-light-border/80 dark:border-dark-border/80 transition-colors duration-200">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
        {/* Brand Logo & Wordmark */}
        <a
          href="#"
          onClick={handleLinkClick}
          className="flex items-center gap-3 group focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-shushly-sapphire rounded-lg p-1"
        >
          <img
            src="/shushly-logo.png"
            alt="Shushly Logo"
            className="w-8 h-8 rounded-lg shadow-sm transition-transform group-hover:scale-105"
          />
          <div className="flex items-baseline gap-1.5">
            <span className="font-extrabold text-lg text-light-text dark:text-dark-text tracking-tight">
              Shushly
            </span>
            <span className="text-[11px] font-mono font-medium text-shushly-sapphire dark:text-shushly-sapphire-light hidden sm:inline">
              v0.1.2
            </span>
          </div>
        </a>

        {/* Desktop Navigation Links */}
        <nav className="hidden md:flex items-center gap-7 text-sm font-medium text-light-muted dark:text-dark-muted">
          {navLinks.map((link) => (
            <a
              key={link.href}
              href={link.href}
              onClick={() => soundPlayer.play('click', 0.2)}
              className="hover:text-shushly-sapphire dark:hover:text-shushly-sapphire-light transition-colors py-1"
            >
              {link.label}
            </a>
          ))}
        </nav>

        {/* Right Action Controls */}
        <div className="flex items-center gap-2 sm:gap-3">
          {/* Optional Subtle Audio Toggle */}
          <button
            type="button"
            onClick={() => {
              onToggleSound();
              if (!soundEnabled) {
                // If turning on, test sound
                setTimeout(() => soundPlayer.play('click', 0.4), 50);
              }
            }}
            aria-label={soundEnabled ? 'Mute website UI sounds' : 'Enable website UI sounds'}
            title={soundEnabled ? 'Mute website UI sounds' : 'Enable website UI sounds (Defaults to off)'}
            className={`p-2 rounded-full border transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-shushly-sapphire ${
              soundEnabled
                ? 'bg-shushly-sapphire/10 border-shushly-sapphire/30 text-shushly-sapphire dark:text-shushly-sapphire-light'
                : 'border-transparent text-light-dim dark:text-dark-dim hover:text-light-text dark:hover:text-dark-text'
            }`}
          >
            {soundEnabled ? (
              <Volume2 className="w-4 h-4" />
            ) : (
              <VolumeX className="w-4 h-4" />
            )}
          </button>

          {/* GitHub Repository Link */}
          <a
            href="https://github.com/BIoodAndWine/Shushly"
            target="_blank"
            rel="noopener noreferrer"
            aria-label="GitHub Repository"
            title="GitHub Repository"
            onClick={() => soundPlayer.play('click', 0.2)}
            className="p-2 rounded-full border border-transparent hover:border-light-border dark:hover:border-dark-border text-light-dim dark:text-dark-dim hover:text-light-text dark:hover:text-dark-text transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-shushly-sapphire"
          >
            <Github className="w-4 h-4" />
          </a>

          {/* Animated Theme Toggle Button */}
          <AnimatedThemeToggle isDark={isDark} onToggle={onToggleTheme} />

          {/* Desktop Download Button CTA */}
          <a
            href="#downloads"
            onClick={() => soundPlayer.play('click', 0.3)}
            className="hidden sm:inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full text-xs font-semibold bg-shushly-sapphire hover:bg-shushly-sapphire-dark text-white transition-all shadow-sm hover:shadow-glow-sapphire active:scale-95 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-shushly-sapphire"
          >
            <Download className="w-3.5 h-3.5" />
            <span>Download</span>
          </a>

          {/* Mobile Menu Hamburger */}
          <button
            type="button"
            onClick={() => {
              soundPlayer.play('click', 0.2);
              setMobileMenuOpen(!mobileMenuOpen);
            }}
            aria-label="Toggle Navigation Menu"
            className="p-2 md:hidden rounded-lg border border-light-border dark:border-dark-border text-light-text dark:text-dark-text focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-shushly-sapphire"
          >
            {mobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
          </button>
        </div>
      </div>

      {/* Mobile Drawer Menu */}
      {mobileMenuOpen && (
        <div className="md:hidden border-b border-light-border dark:border-dark-border bg-light-surface dark:bg-dark-surface px-6 py-5 shadow-xl space-y-4 animate-in slide-in-from-top-2 duration-200">
          <nav className="flex flex-col space-y-3 font-medium text-sm">
            {navLinks.map((link) => (
              <a
                key={link.href}
                href={link.href}
                onClick={handleLinkClick}
                className="text-light-text dark:text-dark-text hover:text-shushly-sapphire py-1"
              >
                {link.label}
              </a>
            ))}
          </nav>
          <div className="pt-3 border-t border-light-border dark:border-dark-border flex flex-col gap-3">
            <a
              href="#downloads"
              onClick={handleLinkClick}
              className="w-full flex items-center justify-center gap-2 py-2.5 rounded-xl font-semibold text-sm bg-shushly-sapphire text-white shadow-sm"
            >
              <Download className="w-4 h-4" />
              Download Shushly (v0.1.2)
            </a>
            <a
              href="https://github.com/BIoodAndWine/Shushly"
              target="_blank"
              rel="noopener noreferrer"
              className="w-full flex items-center justify-center gap-2 py-2 rounded-xl text-xs font-medium border border-light-border dark:border-dark-border text-light-text dark:text-dark-text hover:bg-light-elevated dark:hover:bg-dark-elevated"
            >
              <Github className="w-3.5 h-3.5" />
              Star on GitHub
            </a>
          </div>
        </div>
      )}
    </header>
  );
};
