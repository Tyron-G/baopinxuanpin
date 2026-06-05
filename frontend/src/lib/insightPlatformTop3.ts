import type { CategoryBrief, CategoryTrend, CompetitionData, SupplyDemand } from '@/types'

function tamFromFallback(categoryName: string, fallback: CategoryBrief[]): string | null {
  return fallback.find((item) => item.categoryName === categoryName)?.tamSamSomSummary ?? null
}

function latestTrendByCategory(rows: CategoryTrend[]): CategoryTrend[] {
  const map = new Map<string, CategoryTrend>()
  for (const row of rows) {
    const existing = map.get(row.categoryName)
    if (!existing || row.trendMonth.localeCompare(existing.trendMonth) >= 0) {
      map.set(row.categoryName, row)
    }
  }
  return [...map.values()]
}

export function buildTrendTop3ForPlatform(
  platform: string,
  rows: CategoryTrend[],
  fallback: CategoryBrief[]
): CategoryBrief[] {
  if (platform === '全平台') return fallback
  return latestTrendByCategory(rows)
    .sort((left, right) => Number(right.growthRate) - Number(left.growthRate))
    .slice(0, 3)
    .map((row) => {
      const growth = `${row.growthRate}%`
      const description = `${row.categoryName} 在 ${platform} 搜索与内容声量同步上升，${row.risingWords} 等词加速。`
      return {
        categoryName: row.categoryName,
        metric: `月增速 ${growth}`,
        description,
        monthlySearchVolume: row.searchVolume,
        growthRate12m: growth,
        socialHeat: row.socialHeat,
        risingWords: row.risingWords,
        platformGrowthRates: `${platform} 近6月峰值增速 ${growth}`,
        categoryDescription: description,
        tamSamSomSummary: tamFromFallback(row.categoryName, fallback)
      }
    })
}

export function buildCompetitionTop3ForPlatform(
  platform: string,
  rows: CompetitionData[],
  fallback: CategoryBrief[]
): CategoryBrief[] {
  if (platform === '全平台') return fallback
  return [...rows]
    .sort((left, right) => Number(left.cr5) - Number(right.cr5))
    .slice(0, 3)
    .map((row) => {
      const description = row.conclusion || `${row.categoryName} 在 ${platform} 的竞争格局需结合 CR3/CR5 判断进入难度。`
      return {
        categoryName: row.categoryName,
        metric: `CR5 ${row.cr5}%`,
        description,
        monthlySearchVolume: row.totalSearchVolume,
        growthRate12m: '近12月搜索平稳',
        socialHeat: null,
        risingWords: `CR3 ${row.cr3}%`,
        platformGrowthRates: `${platform} CR3 ${row.cr3}% · CR5 ${row.cr5}%`,
        categoryDescription: description,
        tamSamSomSummary: tamFromFallback(row.categoryName, fallback)
      }
    })
}

export function buildSupplyTop3ForPlatform(
  platform: string,
  rows: SupplyDemand[],
  fallback: CategoryBrief[]
): CategoryBrief[] {
  if (platform === '全平台') return fallback
  const bestGapByCategory = new Map<string, SupplyDemand>()
  for (const row of rows) {
    const existing = bestGapByCategory.get(row.categoryName)
    if (!existing || Number(row.demandSupplyRatio) > Number(existing.demandSupplyRatio)) {
      bestGapByCategory.set(row.categoryName, row)
    }
  }
  return [...bestGapByCategory.values()]
    .sort((left, right) => Number(right.demandSupplyRatio) - Number(left.demandSupplyRatio))
    .slice(0, 3)
    .map((row) => {
      const description = `${row.categoryName} 在 ${platform} 的 ${row.priceRange} 价格带供需比 ${row.demandSupplyRatio}，供给 ${row.supplyCount} 仍偏紧。`
      return {
        categoryName: row.categoryName,
        metric: row.priceRange,
        description,
        monthlySearchVolume: row.searchVolume,
        growthRate12m: `供需比 ${row.demandSupplyRatio}`,
        socialHeat: null,
        risingWords: `供给 ${row.supplyCount}`,
        platformGrowthRates: `${platform} 供需比 ${row.demandSupplyRatio}`,
        categoryDescription: description,
        tamSamSomSummary: tamFromFallback(row.categoryName, fallback)
      }
    })
}
