import { test, expect } from '@playwright/test'

test.use({ channel: 'msedge' })

test('机会页动作状态可更新', async ({ page }) => {
  await page.goto('/opportunity/1?brandId=1&platform=%E5%85%A8%E5%B9%B3%E5%8F%B0')
  await expect(page.getByRole('heading', { name: /当前值得怎么判断/ })).toBeVisible()

  const firstCard = page.locator('.action-card').first()
  const statusTag = firstCard.locator('.tag-group .el-tag').first()
  const firstSegmented = firstCard.locator('.status-segmented')
  const currentSegmentedStatus = (await firstSegmented.locator('.el-segmented__item.is-selected .el-segmented__item-label').textContent())?.trim() ?? ''
  const targetStatus = currentSegmentedStatus === '已完成' ? '进行中' : '已完成'
  const targetItem = firstSegmented.locator('.el-segmented__item').filter({ hasText: targetStatus })

  await expect(firstSegmented).toBeVisible()
  await targetItem.scrollIntoViewIfNeeded()
  await Promise.all([
    page.waitForResponse((response) =>
      response.request().method() === 'POST' &&
      response.url().includes('/api/opportunity/1/actions/') &&
      response.status() === 200
    ),
    targetItem.locator('.el-segmented__item-label').click({ force: true })
  ])
  await expect(statusTag).toHaveText(targetStatus)

  await page.reload({ waitUntil: 'networkidle' })
  await expect(page.getByRole('heading', { name: /当前值得怎么判断/ })).toBeVisible()
  await expect(page.locator('.action-card').first().locator('.tag-group .el-tag').first()).toHaveText(targetStatus)
})
