import React, { useState } from 'react';
import { Sun, Moon } from 'lucide-react';
import { soundPlayer } from '../audio/soundPlayer';

interface AnimatedThemeToggleProps {
  isDark: boolean;
  onToggle: () => void;
  className?: string;
}

/**
 * Animated Theme Toggle matching Shushly's native AnimatedThemeToggle.kt
 * and inspired by https://karlmolles.com/
 *
 * In Dark Mode: Displays Sun icon (inviting switch to Light).
 * In Light Mode: Displays Moon icon (inviting switch to Dark).
 *
 * Smooth rotation, scale, opacity with spring-like physics and tactile press feedback.
 */
export const AnimatedThemeToggle: React.FC<AnimatedThemeToggleProps> = ({
  isDark,
  onToggle,
  className = '',
}) => {
  const [isPressed, setIsPressed] = useState(false);

  const handleClick = () => {
    soundPlayer.play('click', 0.3);
    onToggle();
  };

  return (
    <button
      type="button"
      onClick={handleClick}
      onMouseDown={() => setIsPressed(true)}
      onMouseUp={() => setIsPressed(false)}
      onMouseLeave={() => setIsPressed(false)}
      aria-label={isDark ? 'Switch to light theme' : 'Switch to dark theme'}
      title={isDark ? 'Switch to light theme' : 'Switch to dark theme'}
      className={`relative inline-flex items-center justify-center w-10 h-10 rounded-full transition-transform duration-150 ease-out focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-shushly-sapphire active:scale-90 ${
        isPressed ? 'scale-90' : 'hover:scale-105'
      } bg-light-surface dark:bg-dark-surface border border-light-border dark:border-dark-border text-light-text dark:text-dark-text shadow-sm hover:border-shushly-sapphire/50 dark:hover:border-shushly-sapphire-light/50 ${className}`}
    >
      <div className="relative w-5 h-5 overflow-hidden flex items-center justify-center">
        {/* Sun Icon (Visible in Dark Mode) */}
        <div
          className={`absolute transition-all duration-300 ease-[cubic-bezier(0.34,1.56,0.64,1)] ${
            isDark
              ? 'opacity-100 scale-100 rotate-0 pointer-events-auto text-amber-400'
              : 'opacity-0 scale-50 -rotate-90 pointer-events-none text-light-dim'
          }`}
        >
          <Sun className="w-5 h-5 transition-transform" />
        </div>

        {/* Moon Icon (Visible in Light Mode) */}
        <div
          className={`absolute transition-all duration-300 ease-[cubic-bezier(0.34,1.56,0.64,1)] ${
            !isDark
              ? 'opacity-100 scale-100 rotate-0 pointer-events-auto text-shushly-sapphire'
              : 'opacity-0 scale-50 rotate-90 pointer-events-none text-dark-muted'
          }`}
        >
          <Moon className="w-5 h-5 transition-transform" />
        </div>
      </div>
    </button>
  );
};
