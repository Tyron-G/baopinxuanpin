import { defineConfig } from '@playwright/test'

const e2ePort = process.env.E2E_PORT || '4173'
const e2eHost = process.env.E2E_HOST || '127.0.0.1'
const e2eBaseUrl = `http://${e2eHost}:${e2ePort}`

export default defineConfig({
  testDir: './e2e',
  timeout: 30_000,
  fullyParallel: false,
  reporter: 'list',
  webServer: {
    command: `npm run dev -- --port ${e2ePort}`,
    url: e2eBaseUrl,
    reuseExistingServer: true,
    timeout: 120_000
  },
  use: {
    channel: 'msedge',
    headless: true,
    viewport: { width: 1440, height: 1200 },
    baseURL: e2eBaseUrl
  }
})
