import React, { useState, useEffect } from 'react';
import {
  ShieldCheck,
  Check,
  ChevronDown,
  Sliders,
  Power,
  RotateCw,
  Laptop,
  Smartphone,
} from 'lucide-react';
import { ProtectionLevel, ProtectionState } from '../types';
import { soundPlayer } from '../audio/soundPlayer';

interface AppSimulatorProps {
  isDark: boolean;
}

export const AppSimulator: React.FC<AppSimulatorProps> = ({ isDark: _isDark }) => {
  const [platform, setPlatform] = useState<'windows' | 'android'>('windows');
  const [status, setStatus] = useState<ProtectionState>('CONNECTED');
  const [level, setLevel] = useState<ProtectionLevel>('LIGHT');
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [advancedOpen, setAdvancedOpen] = useState(false);
  const [blockedCount, setBlockedCount] = useState(142);

  // Increment blocked queries gently when connected
  useEffect(() => {
    if (status !== 'CONNECTED') return;
    const interval = setInterval(() => {
      setBlockedCount((c) => c + Math.floor(Math.random() * 2) + 1);
    }, 3800);
    return () => clearInterval(interval);
  }, [status]);

  const handleToggle = () => {
    if (status === 'CONNECTED') {
      soundPlayer.play('toggle_off', 0.5);
      setStatus('DISCONNECTED');
    } else {
      soundPlayer.play('toggle_on', 0.5);
      setStatus('CONNECTING');
      setTimeout(() => {
        soundPlayer.play('connected', 0.5);
        setStatus('CONNECTED');
      }, 500);
    }
  };

  const handleSelectLevel = (newLevel: ProtectionLevel) => {
    soundPlayer.play('mode_change', 0.4);
    setLevel(newLevel);
    setDropdownOpen(false);
  };

  const getLevelDetails = () => {
    switch (level) {
      case 'LIGHT':
        return {
          title: 'Light (Default)',
          subtitle: 'Everyday ad & tracker blocking · Maximum compatibility',
          upstream: 'Control D Ads & Tracking',
          tag: '100% Compatible',
        };
      case 'BALANCED':
        return {
          title: 'Balanced',
          subtitle: 'Comprehensive protection · OISD Full list',
          upstream: 'Control D OISD Full',
          tag: 'Recommended',
        };
      case 'STRICT':
        return {
          title: 'Strict',
          subtitle: 'Aggressive shield · HaGeZi Pro blocklist',
          upstream: 'Control D HaGeZi Pro',
          tag: 'Maximum Privacy',
        };
    }
  };

  const levelDetails = getLevelDetails();

  return (
    <div className="w-full max-w-xl mx-auto">
      {/* Device Switcher Controls */}
      <div className="flex items-center justify-between mb-4 px-2">
        <div className="inline-flex p-1 bg-light-surface dark:bg-dark-surface border border-light-border dark:border-dark-border rounded-xl text-xs font-medium">
          <button
            type="button"
            onClick={() => {
              soundPlayer.play('click', 0.3);
              setPlatform('windows');
            }}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg transition-all ${
              platform === 'windows'
                ? 'bg-shushly-sapphire text-white shadow-sm'
                : 'text-light-muted dark:text-dark-muted hover:text-light-text dark:hover:text-dark-text'
            }`}
          >
            <Laptop className="w-3.5 h-3.5" />
            Windows 11
          </button>
          <button
            type="button"
            onClick={() => {
              soundPlayer.play('click', 0.3);
              setPlatform('android');
            }}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg transition-all ${
              platform === 'android'
                ? 'bg-shushly-sapphire text-white shadow-sm'
                : 'text-light-muted dark:text-dark-muted hover:text-light-text dark:hover:text-dark-text'
            }`}
          >
            <Smartphone className="w-3.5 h-3.5" />
            Android
          </button>
        </div>

        {/* Live Status indicator */}
        <div className="flex items-center gap-2 text-xs">
          <span
            className={`w-2 h-2 rounded-full ${
              status === 'CONNECTED'
                ? 'bg-shushly-emerald animate-pulse'
                : status === 'CONNECTING'
                ? 'bg-shushly-amber animate-spin'
                : 'bg-light-dim dark:bg-dark-dim'
            }`}
          />
          <span className="font-mono text-light-muted dark:text-dark-muted hidden sm:inline">
            {status === 'CONNECTED' ? 'Live Interactive Preview' : 'Interactive Preview'}
          </span>
        </div>
      </div>

      {/* Frame Container */}
      <div
        className={`relative transition-all duration-300 mx-auto ${
          platform === 'android'
            ? 'max-w-[360px] rounded-[40px] p-3 border-[6px] border-slate-300 dark:border-slate-800 shadow-2xl bg-light-bg dark:bg-dark-bg'
            : 'w-full rounded-2xl border border-light-border dark:border-dark-border shadow-2xl overflow-hidden bg-light-bg dark:bg-dark-bg'
        }`}
      >
        {/* Windows Title Bar */}
        {platform === 'windows' && (
          <div className="flex items-center justify-between px-4 py-2.5 bg-light-surface dark:bg-dark-surface border-b border-light-border dark:border-dark-border select-none">
            <div className="flex items-center gap-2">
              <img src="/icon.png" alt="Shushly" className="w-4 h-4 rounded-sm" />
              <span className="text-xs font-semibold text-light-text dark:text-dark-text tracking-tight">
                Shushly
              </span>
            </div>
            <div className="flex items-center gap-2 text-light-dim dark:text-dark-dim text-[11px]">
              <span className="w-2.5 h-2.5 rounded-full hover:bg-slate-300 dark:hover:bg-slate-700 cursor-default" />
              <span className="w-2.5 h-2.5 rounded-full hover:bg-slate-300 dark:hover:bg-slate-700 cursor-default" />
              <span className="w-2.5 h-2.5 rounded-full hover:bg-red-500 cursor-default" />
            </div>
          </div>
        )}

        {/* Android Camera Notch & Status Bar */}
        {platform === 'android' && (
          <div className="flex items-center justify-between px-6 pt-2 pb-1 text-[11px] font-medium text-light-muted dark:text-dark-muted">
            <span>9:41</span>
            <div className="w-3.5 h-3.5 rounded-full bg-slate-900 dark:bg-slate-700 mx-auto" />
            <div className="flex items-center gap-1.5 font-mono text-[10px]">
              <span>5G</span>
              <span>100%</span>
            </div>
          </div>
        )}

        {/* Actual In-App Content Screen */}
        <div className="p-6 sm:p-8 flex flex-col items-center text-center">
          {/* In-App Header */}
          <div className="w-full flex items-center justify-between mb-8 pb-3 border-b border-light-border/60 dark:border-dark-border/60">
            <div className="flex items-center gap-2.5">
              <img src="/shushly-logo.png" alt="Shushly Logo" className="w-7 h-7 rounded-lg" />
              <span className="text-sm font-bold text-light-text dark:text-dark-text tracking-tight">
                Shushly
              </span>
            </div>
            <div className="flex items-center gap-2">
              <span className="inline-flex items-center px-2 py-0.5 rounded-full text-[10px] font-mono font-medium bg-shushly-sapphire/10 text-shushly-sapphire dark:text-shushly-sapphire-light">
                v0.1.2
              </span>
            </div>
          </div>

          {/* Primary Connection Pill Toggle (Cloudflare WARP-inspired) */}
          <div className="relative my-2">
            <button
              type="button"
              onClick={handleToggle}
              aria-label={status === 'CONNECTED' ? 'Disconnect Shushly' : 'Connect Shushly'}
              className={`relative inline-flex items-center w-36 h-[72px] rounded-full p-1.5 transition-all duration-300 focus:outline-none focus-visible:ring-4 focus-visible:ring-shushly-sapphire/40 ${
                status === 'CONNECTED'
                  ? 'bg-shushly-sapphire shadow-glow-sapphire'
                  : status === 'CONNECTING'
                  ? 'bg-shushly-sapphire-dark animate-pulse'
                  : 'bg-slate-200 dark:bg-[#192231] border border-light-border dark:border-dark-border'
              }`}
            >
              {/* Sliding Circular Thumb with Spring physics */}
              <div
                className={`w-[60px] h-[60px] rounded-full bg-white shadow-md flex items-center justify-center transition-all duration-300 ease-[cubic-bezier(0.34,1.56,0.64,1)] ${
                  status === 'CONNECTED'
                    ? 'translate-x-[72px] text-shushly-sapphire'
                    : status === 'CONNECTING'
                    ? 'translate-x-[36px] text-shushly-sapphire'
                    : 'translate-x-0 text-slate-400 dark:text-slate-500'
                }`}
              >
                {status === 'CONNECTED' ? (
                  <Check className="w-8 h-8 stroke-[2.5]" />
                ) : status === 'CONNECTING' ? (
                  <RotateCw className="w-7 h-7 animate-spin text-shushly-sapphire" />
                ) : (
                  <Power className="w-7 h-7 stroke-[2]" />
                )}
              </div>
            </button>
          </div>

          {/* Connection Status Text */}
          <div className="mt-5 mb-6">
            <div className="flex items-center justify-center gap-2 mb-1">
              <span
                className={`text-base font-bold tracking-tight ${
                  status === 'CONNECTED'
                    ? 'text-shushly-sapphire dark:text-shushly-sapphire-light'
                    : status === 'CONNECTING'
                    ? 'text-shushly-amber'
                    : 'text-light-text dark:text-dark-text'
                }`}
              >
                {status === 'CONNECTED'
                  ? 'Protected with Shushly'
                  : status === 'CONNECTING'
                  ? 'Connecting to secure DNS...'
                  : 'Your internet is unprotected'}
              </span>
            </div>
            <p className="text-xs text-light-muted dark:text-dark-muted max-w-xs mx-auto">
              {status === 'CONNECTED'
                ? `Encrypted DNS-over-HTTPS via ${levelDetails.upstream}`
                : 'Tap the button to block ads and trackers at the DNS layer'}
            </p>

            {status === 'CONNECTED' && (
              <div className="mt-3 inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-[11px] font-mono bg-shushly-emerald/10 text-shushly-emerald-dark dark:text-shushly-emerald border border-shushly-emerald/20">
                <ShieldCheck className="w-3.5 h-3.5" />
                <span>{blockedCount} queries shielded today</span>
              </div>
            )}
          </div>

          {/* Protection Mode Dropdown Selector */}
          <div className="w-full relative mt-2">
            <label className="block text-left text-[11px] font-medium text-light-muted dark:text-dark-muted mb-1.5 px-1">
              Protection Level
            </label>
            <button
              type="button"
              onClick={() => {
                soundPlayer.play('click', 0.3);
                setDropdownOpen(!dropdownOpen);
              }}
              className="w-full flex items-center justify-between p-3.5 rounded-xl bg-light-surface dark:bg-dark-surface border border-light-border dark:border-dark-border hover:border-shushly-sapphire/50 transition-colors text-left"
            >
              <div>
                <div className="flex items-center gap-2">
                  <span className="text-sm font-semibold text-light-text dark:text-dark-text">
                    {levelDetails.title}
                  </span>
                  <span className="px-1.5 py-0.5 rounded text-[10px] font-semibold bg-shushly-sapphire/10 text-shushly-sapphire dark:text-shushly-sapphire-light">
                    {levelDetails.tag}
                  </span>
                </div>
                <p className="text-xs text-light-muted dark:text-dark-muted mt-0.5">
                  {levelDetails.subtitle}
                </p>
              </div>
              <ChevronDown
                className={`w-4 h-4 text-light-dim dark:text-dark-dim transition-transform ${
                  dropdownOpen ? 'rotate-180' : ''
                }`}
              />
            </button>

            {/* Dropdown Options */}
            {dropdownOpen && (
              <div className="absolute top-full left-0 right-0 mt-2 p-1.5 bg-light-surface dark:bg-dark-surface border border-light-border dark:border-dark-border rounded-xl shadow-xl z-20 space-y-1">
                {(['LIGHT', 'BALANCED', 'STRICT'] as ProtectionLevel[]).map((lvl) => {
                  const isCurrent = level === lvl;
                  const item =
                    lvl === 'LIGHT'
                      ? {
                          name: 'Light (Default)',
                          sub: 'Everyday ad blocking · Max compatibility',
                          tag: 'Default',
                        }
                      : lvl === 'BALANCED'
                      ? {
                          name: 'Balanced',
                          sub: 'Comprehensive protection with OISD Full',
                          tag: 'Recommended',
                        }
                      : {
                          name: 'Strict',
                          sub: 'Aggressive shield with HaGeZi Pro',
                          tag: 'Max Privacy',
                        };

                  return (
                    <button
                      key={lvl}
                      type="button"
                      onClick={() => handleSelectLevel(lvl)}
                      className={`w-full flex items-center justify-between p-2.5 rounded-lg text-left transition-colors ${
                        isCurrent
                          ? 'bg-shushly-sapphire/10 text-shushly-sapphire dark:text-shushly-sapphire-light'
                          : 'hover:bg-light-elevated dark:hover:bg-dark-elevated text-light-text dark:text-dark-text'
                      }`}
                    >
                      <div>
                        <div className="text-xs font-semibold">{item.name}</div>
                        <div className="text-[11px] text-light-muted dark:text-dark-muted">
                          {item.sub}
                        </div>
                      </div>
                      {isCurrent && <Check className="w-4 h-4 text-shushly-sapphire" />}
                    </button>
                  );
                })}
              </div>
            )}
          </div>

          {/* Advanced Mode Collapsible Preview */}
          <div className="w-full mt-4 text-left">
            <button
              type="button"
              onClick={() => {
                soundPlayer.play('click', 0.3);
                setAdvancedOpen(!advancedOpen);
              }}
              className="w-full flex items-center justify-between px-3 py-2 text-xs font-medium text-light-muted dark:text-dark-muted hover:text-shushly-sapphire transition-colors"
            >
              <div className="flex items-center gap-1.5">
                <Sliders className="w-3.5 h-3.5" />
                <span>Advanced Mode (100% Free)</span>
              </div>
              <ChevronDown
                className={`w-3.5 h-3.5 transition-transform ${
                  advancedOpen ? 'rotate-180' : ''
                }`}
              />
            </button>

            {advancedOpen && (
              <div className="mt-2 p-3 rounded-xl bg-light-elevated dark:bg-dark-elevated border border-light-border dark:border-dark-border text-xs space-y-2">
                <div className="flex items-center justify-between text-[11px] text-light-dim dark:text-dark-dim pb-1.5 border-b border-light-border dark:border-dark-border">
                  <span>Verified Resolver Directory</span>
                  <span>Latency</span>
                </div>
                <div className="space-y-1.5">
                  {[
                    { name: 'Control D Ads & Tracking', ping: '12ms', secure: true },
                    { name: 'Control D OISD Full', ping: '14ms', secure: true },
                    { name: 'Control D HaGeZi Pro', ping: '15ms', secure: true },
                    { name: 'AdGuard DNS Default', ping: '18ms', secure: true },
                    { name: 'dnsforge.de (Germany)', ping: '24ms', secure: true },
                  ].map((res, i) => (
                    <div
                      key={i}
                      className="flex items-center justify-between text-[11px] text-light-text dark:text-dark-text"
                    >
                      <div className="flex items-center gap-1.5">
                        <span className="w-1.5 h-1.5 rounded-full bg-shushly-emerald" />
                        <span>{res.name}</span>
                      </div>
                      <span className="font-mono text-[10px] text-light-muted dark:text-dark-muted">
                        {res.ping}
                      </span>
                    </div>
                  ))}
                </div>
                <div className="pt-2 text-[10px] text-light-muted dark:text-dark-muted">
                  Custom DoH endpoints (NextDNS, Pi-hole, AdGuard Home) supported with no restrictions.
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
