import { chromium } from 'playwright';
import path from 'path';
import fs from 'fs';

const OUT_DIR = path.resolve('../qa-screenshots');
if (!fs.existsSync(OUT_DIR)) {
  fs.mkdirSync(OUT_DIR, { recursive: true });
}

async function runQA() {
  console.log('Launching browser (msedge)...');
  const browser = await chromium.launch({ channel: 'msedge', headless: true });

  // 1. Desktop Dark
  {
    const context = await browser.newContext({
      viewport: { width: 1440, height: 900 },
      deviceScaleFactor: 2,
      colorScheme: 'dark',
    });
    const page = await context.newPage();
    await page.goto('https://shushly-adblocker.web.app', { waitUntil: 'networkidle' });
    console.log('Capturing desktop-dark.png...');
    await page.screenshot({ path: path.join(OUT_DIR, 'desktop-dark.png'), fullPage: true });
    await context.close();
  }

  // 2. Desktop Light
  {
    const context = await browser.newContext({
      viewport: { width: 1440, height: 900 },
      deviceScaleFactor: 2,
      colorScheme: 'light',
    });
    const page = await context.newPage();
    await page.goto('https://shushly-adblocker.web.app', { waitUntil: 'networkidle' });
    // Force light mode
    await page.evaluate(() => {
      document.documentElement.classList.remove('dark');
      document.documentElement.classList.add('light');
    });
    await page.waitForTimeout(300);
    console.log('Capturing desktop-light.png...');
    await page.screenshot({ path: path.join(OUT_DIR, 'desktop-light.png'), fullPage: true });
    await context.close();
  }

  // 3. Mobile Viewport Dark (390x844 - iPhone / Pixel)
  {
    const context = await browser.newContext({
      viewport: { width: 390, height: 844 },
      deviceScaleFactor: 2,
      colorScheme: 'dark',
      isMobile: true,
    });
    const page = await context.newPage();
    await page.goto('https://shushly-adblocker.web.app', { waitUntil: 'networkidle' });
    console.log('Capturing mobile-dark.png...');
    await page.screenshot({ path: path.join(OUT_DIR, 'mobile-dark.png'), fullPage: true });
    await context.close();
  }

  // 4. Mobile Viewport Light (390x844)
  {
    const context = await browser.newContext({
      viewport: { width: 390, height: 844 },
      deviceScaleFactor: 2,
      colorScheme: 'light',
      isMobile: true,
    });
    const page = await context.newPage();
    await page.goto('https://shushly-adblocker.web.app', { waitUntil: 'networkidle' });
    await page.evaluate(() => {
      document.documentElement.classList.remove('dark');
      document.documentElement.classList.add('light');
    });
    await page.waitForTimeout(300);
    console.log('Capturing mobile-light.png...');
    await page.screenshot({ path: path.join(OUT_DIR, 'mobile-light.png'), fullPage: true });
    await context.close();
  }

  await browser.close();
  console.log('Full QA suite screenshots captured successfully in qa-screenshots/');
}

runQA().catch((err) => {
  console.error('QA Failed:', err);
  process.exit(1);
});
