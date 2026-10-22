/**
 * Audio Player for Shushly Website
 * Reuses the authentic sounds from composeApp/src/commonMain/composeResources/files/sounds/
 *
 * Requirements:
 * - Defaults to OFF.
 * - Never autoplays audio.
 * - Plays only on explicit user interaction.
 * - Preloads buffers cleanly on first enable.
 */

type SoundName = 'click' | 'toggle_on' | 'toggle_off' | 'connected' | 'mode_change' | 'error';

class SoundPlayer {
  private ctx: AudioContext | null = null;
  private buffers: Map<SoundName, AudioBuffer> = new Map();
  private enabled: boolean = false;
  private hasInitialized: boolean = false;

  constructor() {
    // Check if preference is stored in localStorage
    try {
      const stored = localStorage.getItem('shushly-sound-enabled');
      this.enabled = stored === 'true';
    } catch {
      this.enabled = false;
    }
  }

  public isEnabled(): boolean {
    return this.enabled;
  }

  public setEnabled(enable: boolean) {
    this.enabled = enable;
    try {
      localStorage.setItem('shushly-sound-enabled', String(enable));
    } catch {}

    if (enable && !this.hasInitialized) {
      this.initContext();
    }
  }

  private initContext() {
    if (typeof window === 'undefined') return;
    try {
      const AudioCtx = window.AudioContext || (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
      this.ctx = new AudioCtx();
      this.hasInitialized = true;
      this.preloadAll();
    } catch (e) {
      console.warn('Web Audio not supported', e);
    }
  }

  private async loadSound(name: SoundName, path: string) {
    if (!this.ctx) return;
    try {
      const res = await fetch(path);
      const arrayBuffer = await res.arrayBuffer();
      const audioBuffer = await this.ctx.decodeAudioData(arrayBuffer);
      this.buffers.set(name, audioBuffer);
    } catch {
      // Gracefully silent if network or decode fails
    }
  }

  private preloadAll() {
    const sounds: Record<SoundName, string> = {
      click: '/sounds/click.wav',
      toggle_on: '/sounds/toggle_on.wav',
      toggle_off: '/sounds/toggle_off.wav',
      connected: '/sounds/connected.wav',
      mode_change: '/sounds/mode_change.wav',
      error: '/sounds/error.wav',
    };

    Object.entries(sounds).forEach(([name, path]) => {
      this.loadSound(name as SoundName, path);
    });
  }

  public async play(name: SoundName, volume = 0.45) {
    if (!this.enabled) return;

    if (!this.ctx) {
      this.initContext();
    }

    if (this.ctx && this.ctx.state === 'suspended') {
      try {
        await this.ctx.resume();
      } catch {}
    }

    let buffer = this.buffers.get(name);
    if (!buffer && this.ctx) {
      await this.loadSound(name, `/sounds/${name}.wav`);
      buffer = this.buffers.get(name);
    }

    if (buffer && this.ctx) {
      try {
        const source = this.ctx.createBufferSource();
        source.buffer = buffer;
        const gainNode = this.ctx.createGain();
        gainNode.gain.value = volume;
        source.connect(gainNode);
        gainNode.connect(this.ctx.destination);
        source.start(0);
      } catch {}
    }
  }
}

export const soundPlayer = new SoundPlayer();
