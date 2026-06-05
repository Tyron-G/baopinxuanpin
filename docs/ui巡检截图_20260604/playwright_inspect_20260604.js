const { chromium } = require('playwright')
const path = require('path')

const outputDir = 'D:/PRD/baopinxuanpin/docs/ui巡检截图_20260604'
const base = 'http://localhost:5173'

const cases = [
  { name: 'insight', url: `${base}/insight?brandId=1&platform=%E5%85%A8%E5%B9%B3%E5%8F%B0` },
  { name: 'opportunity', url: `${base}/opportunity/1?brandId=1&platform=%E5%85%A8%E5%B9%B3%E5%8F%B0` },
  { name: 'report', url: `${base}/report/1?brandId=1&platform=%E5%85%A8%E5%B9%B3%E5%8F%B0` },
  { name: 'competitor', url: `${base}/competitor?brandId=1&platform=%E5%85%A8%E5%B9%B3%E5%8F%B0` }
]

const viewports = [
  { label: '1280', width: 1280, height: 2200 },
  { label: '1440', width: 1440, height: 2400 }
]

;(async () => {
  const browser = await chromium.launch({
    channel: 'msedge',
    headless: true
  })

  for (const viewport of viewports) {
    const page = await browser.newPage({
      viewport: { width: viewport.width, height: viewport.height },
      colorScheme: 'light'
    })

    for (const item of cases) {
      await page.goto(item.url, { waitUntil: 'networkidle', timeout: 60000 })
      await page.screenshot({
        path: path.join(outputDir, `${item.name}_${viewport.label}.png`),
        fullPage: true
      })
    }

    await page.close()
  }

  await browser.close()
})().catch((error) => {
  console.error(error)
  process.exit(1)
})
