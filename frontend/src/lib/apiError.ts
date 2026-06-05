/** 从 axios / ApiResult 错误中提取用户可读文案 2026-06-04 */
export function getApiErrorMessage(error: unknown, fallback = '请求失败，请稍后重试'): string {
  if (error instanceof Error && error.message) {
    return error.message
  }
  return fallback
}
