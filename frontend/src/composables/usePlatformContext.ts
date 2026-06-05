import type { LocationQueryRaw } from 'vue-router'
import { DEFAULT_PLATFORM_VIEW } from '@/constants/brand'
import { getBrandId } from '@/composables/useBrandContext'

/** 路由平台参数解析与 query 拼装 2026-06-05 */
export function resolvePlatform(platform?: unknown): string {
  if (typeof platform === 'string' && platform.trim()) {
    return platform.trim()
  }
  return DEFAULT_PLATFORM_VIEW
}

export function buildBrandPlatformQuery(
  routeQuery: LocationQueryRaw,
  extra: Record<string, string | number | undefined> = {}
): Record<string, string | number | undefined> {
  const brandId = Number(routeQuery.brandId)
  const resolvedBrandId = Number.isFinite(brandId) && brandId > 0 ? brandId : getBrandId()
  const platform = resolvePlatform(
    extra.platform !== undefined ? extra.platform : routeQuery.platform
  )
  const { platform: _ignored, brandId: _brandIgnored, ...rest } = extra
  return {
    ...routeQuery,
    brandId: resolvedBrandId,
    platform,
    ...rest
  }
}
