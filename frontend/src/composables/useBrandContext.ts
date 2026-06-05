import { DEFAULT_BRAND_ID } from '@/constants/brand'

const STORAGE_KEY = 'selection-brand-id'

export function getBrandId(): number {
  const raw = sessionStorage.getItem(STORAGE_KEY)
  const parsed = raw ? Number(raw) : DEFAULT_BRAND_ID
  return Number.isFinite(parsed) && parsed > 0 ? parsed : DEFAULT_BRAND_ID
}

export function setBrandId(id: number) {
  sessionStorage.setItem(STORAGE_KEY, String(id))
}

export function withBrandQuery(extra: Record<string, string | number> = {}) {
  return { brandId: getBrandId(), ...extra }
}
