/**
 * Official Shushly Design Tokens
 * Directly synchronized with composeApp/src/commonMain/kotlin/com/shushly/app/presentation/theme/Color.kt
 */
export const SHUSHLY_COLORS = {
  // Brand
  sapphire: '#2563EB',
  sapphireLight: '#3B82F6',
  sapphireDark: '#1D4ED8',

  // Status & Connected
  emerald: '#10B981',
  emeraldDark: '#059669',
  emeraldGlow: 'rgba(16, 185, 129, 0.2)',
  amber: '#F59E0B',
  crimson: '#EF4444',

  // Dark Palette (Default)
  dark: {
    background: '#0B0F17',
    surface: '#131A26',
    surfaceElevated: '#1A2332',
    border: '#253246',
    borderActive: '#3B82F6',
    textPrimary: '#F8FAFC',
    textSecondary: '#94A3B8',
    textMuted: '#64748B',
  },

  // Light Palette
  light: {
    background: '#F8FAFC',
    surface: '#FFFFFF',
    surfaceElevated: '#F1F5F9',
    border: '#E2E8F0',
    borderActive: '#2563EB',
    textPrimary: '#0F172A',
    textSecondary: '#475569',
    textMuted: '#94A3B8',
  },
} as const;
