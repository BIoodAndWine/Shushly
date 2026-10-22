/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        shushly: {
          sapphire: '#2563EB',
          'sapphire-light': '#3B82F6',
          'sapphire-dark': '#1D4ED8',
          emerald: '#10B981',
          'emerald-dark': '#059669',
          amber: '#F59E0B',
          crimson: '#EF4444',
        },
        dark: {
          bg: '#0B0F17',
          surface: '#131A26',
          elevated: '#1A2332',
          border: '#253246',
          'border-active': '#3B82F6',
          text: '#F8FAFC',
          muted: '#94A3B8',
          dim: '#64748B',
        },
        light: {
          bg: '#F8FAFC',
          surface: '#FFFFFF',
          elevated: '#F1F5F9',
          border: '#E2E8F0',
          'border-active': '#2563EB',
          text: '#0F172A',
          muted: '#475569',
          dim: '#94A3B8',
        },
      },
      fontFamily: {
        sans: [
          'Inter',
          '-apple-system',
          'BlinkMacSystemFont',
          '"Segoe UI"',
          'Roboto',
          'sans-serif',
        ],
        mono: [
          'JetBrains Mono',
          'ui-monospace',
          'SFMono-Regular',
          'Menlo',
          'monospace',
        ],
      },
      borderRadius: {
        'squircle-sm': '10px',
        'squircle': '16px',
        'squircle-lg': '24px',
        'squircle-xl': '32px',
      },
      boxShadow: {
        'glow-sapphire': '0 0 32px -4px rgba(37, 99, 235, 0.35)',
        'glow-emerald': '0 0 32px -4px rgba(16, 185, 129, 0.35)',
        'card-dark': '0 4px 20px -2px rgba(0, 0, 0, 0.4)',
        'card-light': '0 4px 20px -2px rgba(15, 23, 42, 0.06)',
      },
    },
  },
  plugins: [],
};
