export type ProtectionLevel = 'LIGHT' | 'BALANCED' | 'STRICT';

export type ProtectionState = 'DISCONNECTED' | 'CONNECTING' | 'CONNECTED';

export interface ProtectionLevelInfo {
  id: ProtectionLevel;
  title: string;
  badge?: string;
  subtitle: string;
  dohUrl: string;
  filterName: string;
  compatibility: string;
  blockTarget: string;
  recommendedFor: string;
}

export interface ReleaseAsset {
  platform: 'windows' | 'android';
  title: string;
  description: string;
  filename: string;
  format: string;
  url: string;
  isPrimary?: boolean;
  notes?: string;
}
