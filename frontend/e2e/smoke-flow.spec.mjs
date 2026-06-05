import { test, expect } from '@playwright/test'

test.use({ channel: 'msedge' })

test('核心页面主链路可达', async ({ page }) => {
  await page.goto('/insight?brandId=1&category=%E5%AE%A0%E7%89%A9%E6%99%BA%E8%83%BD%E7%94%A8%E5%93%81&platform=%E5%85%A8%E5%B9%B3%E5%8F%B0')
  await expect(page.getByText('把候选赛道先筛成可判断的机会池')).toBeVisible()

  await page.getByRole('button', { name: '进入最佳机会' }).click()
  await expect(page).toHaveURL(/\/opportunity\//)
  await expect(page.getByRole('heading', { name: /当前值得怎么判断/ })).toBeVisible()

  await page.getByRole('button', { name: '查看报告' }).click()
  await expect(page).toHaveURL(/\/report\//)
  await expect(page.getByRole('heading', { name: /当前的正式结论是什么/ })).toBeVisible()

  await page.getByRole('button', { name: '返回机会' }).click()
  await expect(page).toHaveURL(/\/opportunity\//)
  await expect(page.getByRole('heading', { name: /当前值得怎么判断/ })).toBeVisible()

  await page.getByRole('button', { name: '查看竞品页' }).first().click()
  await expect(page).toHaveURL(/\/competitor/)
  await expect(page.getByRole('heading', { name: /把值得长期盯的对标对象稳定放进工作台/ })).toBeVisible()

  await page.getByRole('button', { name: '返回洞察' }).click()
  await expect(page).toHaveURL(/\/insight/)
  await expect(page.getByRole('heading', { name: /把候选赛道先筛成可判断的机会池/ })).toBeVisible()
})
