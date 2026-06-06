// 2026-06-05 交付材料采集脚本：完整页面截图、正常视口录屏与按钮动作演示
import { chromium } from 'playwright'
import fs from 'node:fs/promises'
import path from 'node:path'

const ROOT = path.resolve(process.cwd(), '..')
const OUTPUT_DIR = path.join(ROOT, 'docs', '交付材料_20260605')
const BASE_URL = process.env.DELIVERY_BASE_URL || 'http://127.0.0.1:5173'
const BRAND_ID = process.env.DELIVERY_BRAND_ID || '1'
const PLATFORM = process.env.DELIVERY_PLATFORM || '全平台'
const VIEWPORT = { width: 1440, height: 900 }

const query = (route) => {
  if (route.includes('?')) return `${route}&brandId=${BRAND_ID}&platform=${encodeURIComponent(PLATFORM)}`
  return `${route}?brandId=${BRAND_ID}&platform=${encodeURIComponent(PLATFORM)}`
}

const pages = [
  ['01_数据准备', `/data-prep?brandId=${BRAND_ID}`],
  ['02_信号雷达', query('/radar')],
  ['03_洞察发现', query('/insight')],
  ['04_机会榜单TOP50', query('/ranking')],
  ['05_爆品机会', query('/opportunity/1')],
  ['06_选品报告', query('/report/1')],
  ['07_竞品监控', `/competitor?brandId=${BRAND_ID}`],
  ['08_移动看板', query('/mobile')],
  ['09_归因报告', query('/attribution')],
  ['10_品牌模型', query('/brand-model')],
  ['11_试运行优化', query('/test-run')],
  ['12_开放API', query('/open-api')],
  ['13_供需匹配', query('/supply-match')],
  ['14_团队协作', query('/team')]
]

const demoSteps = [
  {
    title: '数据准备',
    route: `/data-prep?brandId=${BRAND_ID}`,
    actions: ['下一步', '下一步', '上一步', '查看洞察']
  },
  {
    title: '信号雷达',
    route: query('/radar'),
    actions: ['推送今日摘要', '填入演示 Webhook', '保存配置', '回到洞察']
  },
  {
    title: '洞察发现',
    route: query('/insight'),
    actions: ['进入最佳机会', '查看全部类目']
  },
  {
    title: '机会榜单 TOP50',
    route: query('/ranking'),
    actions: ['刷新榜单', '进入机会']
  },
  {
    title: '爆品机会',
    route: query('/opportunity/1'),
    actions: ['查看报告', '查看竞品页', '返回洞察']
  },
  {
    title: '选品报告',
    route: query('/report/1'),
    actions: ['下载 Excel', '下载 PDF', '下载 Markdown', '返回机会']
  },
  {
    title: '竞品监控',
    route: `/competitor?brandId=${BRAND_ID}`,
    actions: ['自动发现竞品', '添加竞品', '取消', '重置筛选', '返回洞察']
  },
  {
    title: '移动看板',
    route: query('/mobile'),
    actions: ['信号', '榜单', '洞察', '竞品']
  },
  {
    title: '归因报告',
    route: query('/attribution'),
    actions: []
  },
  {
    title: '品牌模型',
    route: query('/brand-model'),
    actions: ['重新训练']
  },
  {
    title: '试运行优化',
    route: query('/test-run'),
    actions: ['刷新诊断', '回到机会页']
  },
  {
    title: '开放 API',
    route: query('/open-api'),
    actions: ['保存到会话', '试调用', '清空']
  },
  {
    title: '供需匹配',
    route: query('/supply-match'),
    actions: ['刷新匹配']
  },
  {
    title: '团队协作',
    route: query('/team'),
    actions: ['添加成员', '取消', '新建分派', '取消']
  }
]

async function launchBrowser() {
  try {
    return await chromium.launch({ channel: 'msedge' })
  } catch {
    return await chromium.launch({ channel: 'chrome' })
  }
}

async function waitReady(page) {
  await page.waitForLoadState('networkidle', { timeout: 20000 }).catch(() => {})
  await page.waitForTimeout(900)
}

async function gotoPage(page, route) {
  await page.goto(`${BASE_URL}${route}`, { waitUntil: 'domcontentloaded' })
  await waitReady(page)
}

async function resetViewport(page) {
  await page.setViewportSize(VIEWPORT)
  await page.evaluate(() => window.scrollTo({ top: 0, left: 0 }))
  await page.waitForTimeout(300)
}

async function scrollPreview(page) {
  await resetViewport(page)
  const height = await page.evaluate(() => document.documentElement.scrollHeight)
  const stops = [0, 0.35, 0.7, 1]
  for (const stop of stops) {
    await page.evaluate((top) => window.scrollTo({ top, left: 0, behavior: 'smooth' }), Math.max(0, height * stop - VIEWPORT.height))
    await page.waitForTimeout(650)
  }
  await page.evaluate(() => window.scrollTo({ top: 0, left: 0, behavior: 'smooth' }))
  await page.waitForTimeout(500)
}

async function clickByText(page, text) {
  const locator = page
    .getByRole('button', { name: text, exact: false })
    .or(page.getByRole('link', { name: text, exact: false }))
    .first()
  const count = await locator.count().catch(() => 0)
  if (!count) return false

  await locator.scrollIntoViewIfNeeded().catch(() => {})
  await page.waitForTimeout(250)
  await locator.click({ timeout: 3500 }).catch(async () => {
    await locator.click({ force: true, timeout: 3500 })
  })
  await waitReady(page)
  return true
}

async function closeTransientUi(page) {
  for (const text of ['取消', '关闭']) {
    const locator = page.getByRole('button', { name: text, exact: false }).last()
    if ((await locator.count().catch(() => 0)) > 0) {
      await locator.click({ timeout: 1500 }).catch(() => {})
      await page.waitForTimeout(300)
    }
  }
}

async function captureScreenshots(browser) {
  const context = await browser.newContext({ viewport: VIEWPORT })
  const page = await context.newPage()

  for (const [name, route] of pages) {
    await gotoPage(page, route)
    await resetViewport(page)
    await page.screenshot({
      path: path.join(OUTPUT_DIR, `页面截图_${name}.png`),
      fullPage: false
    })
    await page.screenshot({
      path: path.join(OUTPUT_DIR, `页面长图_${name}.png`),
      fullPage: true
    })
  }

  await context.close()
}

async function recordDemo(browser) {
  const context = await browser.newContext({
    viewport: VIEWPORT,
    recordVideo: {
      dir: OUTPUT_DIR,
      size: VIEWPORT
    }
  })
  const page = await context.newPage()
  const video = page.video()

  for (const step of demoSteps) {
    await gotoPage(page, step.route)
    await scrollPreview(page)

    for (const action of step.actions) {
      const beforeUrl = page.url()
      const clicked = await clickByText(page, action)
      if (!clicked) continue

      await closeTransientUi(page)
      if (page.url() !== beforeUrl) {
        await page.waitForTimeout(900)
        await gotoPage(page, step.route)
      }
      await resetViewport(page)
    }
  }

  await page.close()
  await context.close()
  return video ? await video.path() : ''
}

async function normalizeVideoName(mainVideoPath) {
  await fs.rm(path.join(OUTPUT_DIR, '演示录屏_主流程.webm'), { force: true }).catch(() => {})
  if (mainVideoPath) {
    await fs.rename(mainVideoPath, path.join(OUTPUT_DIR, '演示录屏_主流程.webm')).catch(async () => {
      await fs.copyFile(mainVideoPath, path.join(OUTPUT_DIR, '演示录屏_主流程.webm'))
      await fs.unlink(mainVideoPath)
    })
  }

  const files = await fs.readdir(OUTPUT_DIR)
  for (const file of files.filter((item) => item.endsWith('.webm') && item !== '演示录屏_主流程.webm')) {
    await fs.rm(path.join(OUTPUT_DIR, file), { force: true }).catch(() => {})
  }
}

async function main() {
  await fs.mkdir(OUTPUT_DIR, { recursive: true })
  const browser = await launchBrowser()

  try {
    await captureScreenshots(browser)
    const mainVideoPath = await recordDemo(browser)
    await normalizeVideoName(mainVideoPath)
  } finally {
    await browser.close()
  }
  console.log(`Delivery assets saved to ${OUTPUT_DIR}`)
}

main().catch((error) => {
  console.error(error)
  process.exit(1)
})
