import { test, expect } from '@playwright/test'

test.use({ channel: 'msedge' })

test('报告页可触发 Markdown 下载', async ({ page }) => {
  await page.goto('/report/1?brandId=1&platform=%E5%85%A8%E5%B9%B3%E5%8F%B0')
  await expect(page.getByRole('heading', { name: /当前的正式结论是什么/ })).toBeVisible()

  const downloadPromise = page.waitForEvent('download')
  await page.getByRole('button', { name: '下载 Markdown' }).click()
  const download = await downloadPromise

  expect(download.suggestedFilename()).toMatch(/\.md$/)
})
