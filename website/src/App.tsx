import React, { useState, useEffect } from 'react';
import { Navbar } from './components/Navbar';
import { Hero } from './components/Hero';
import { Features } from './components/Features';
import { HowItWorks } from './components/HowItWorks';
import { ProtectionLevels } from './components/ProtectionLevels';
import { AppShowcase } from './components/AppShowcase';
import { Downloads } from './components/Downloads';
import { OpenSourceTrust } from './components/OpenSourceTrust';
import { FAQ } from './components/FAQ';
import { Footer } from './components/Footer';
import { soundPlayer } from './audio/soundPlayer';

export const App: React.FC = () => {
  // Theme state
  const [isDark, setIsDark] = useState<boolean>(() => {
    try {
      const saved = localStorage.getItem('shushly-theme');
      if (saved) return saved === 'dark';
      return window.matchMedia('(prefers-color-scheme: dark)').matches;
    } catch {
      return true;
    }
  });

  // Sound state (defaults to false)
  const [soundEnabled, setSoundEnabled] = useState<boolean>(() => {
    return soundPlayer.isEnabled();
  });

  // Apply theme to document element
  useEffect(() => {
    const root = document.documentElement;
    if (isDark) {
      root.classList.add('dark');
      root.classList.remove('light');
      try {
        localStorage.setItem('shushly-theme', 'dark');
      } catch {}
    } else {
      root.classList.remove('dark');
      root.classList.add('light');
      try {
        localStorage.setItem('shushly-theme', 'light');
      } catch {}
    }
  }, [isDark]);

  const toggleTheme = () => {
    setIsDark((prev) => !prev);
  };

  const toggleSound = () => {
    const next = !soundEnabled;
    setSoundEnabled(next);
    soundPlayer.setEnabled(next);
  };

  return (
    <div className="min-h-screen flex flex-col bg-light-bg dark:bg-dark-bg text-light-text dark:text-dark-text selection:bg-shushly-sapphire selection:text-white">
      <Navbar
        isDark={isDark}
        onToggleTheme={toggleTheme}
        soundEnabled={soundEnabled}
        onToggleSound={toggleSound}
      />
      <main className="flex-1">
        <Hero isDark={isDark} />
        <Features />
        <HowItWorks />
        <ProtectionLevels />
        <AppShowcase />
        <Downloads />
        <OpenSourceTrust />
        <FAQ />
      </main>
      <Footer />
    </div>
  );
};

export default App;
