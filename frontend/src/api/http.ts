import axios, { type AxiosError } from 'axios'
import { getBrandId } from '@/composables/useBrandContext'

export interface ApiResultBody<T> {
  code: number
  message: string
  data: T
  path: string
  timestamp: number
}

export const http = axios.create({
  baseURL: '/api',
  timeout: 10000
})

function isApiResultBody(value: unknown): value is ApiResultBody<unknown> {
  if (!value || typeof value !== 'object') {
    return false
  }
  return 'code' in value && 'data' in value && 'message' in value
}

http.interceptors.response.use((response) => {
  if (response.config.responseType === 'blob') {
    return response
  }
  const body = response.data
  if (isApiResultBody(body)) {
    if (body.code !== 0) {
      return Promise.reject(new Error(body.message || '请求失败'))
    }
    response.data = body.data
  }
  return response
}, (error: AxiosError<ApiResultBody<unknown>>) => {
  const message = error.response?.data?.message
  if (message) {
    return Promise.reject(new Error(message))
  }
  return Promise.reject(error)
})

/** 未显式传入时使用当前会话品牌 2026-06-04 */
export function resolveBrandId(brandId?: number): number {
  if (brandId != null && Number.isFinite(brandId) && brandId > 0) {
    return brandId
  }
  return getBrandId()
}
