const backendBaseUrl = process.env.E2E_BACKEND_URL || 'http://127.0.0.1:8088'
const e2ePort = process.env.E2E_PORT || '4173'
const e2eHost = process.env.E2E_HOST || '127.0.0.1'
const corsOrigin = process.env.E2E_CORS_ORIGIN || `http://${e2eHost}:${e2ePort}`

async function ensureBackendReachable() {
  const response = await fetch(`${backendBaseUrl}/api/opportunity/1?brandId=1&platform=%E5%85%A8%E5%B9%B3%E5%8F%B0`)
  if (!response.ok) {
    throw new Error(`backend endpoint returned ${response.status}`)
  }
}

async function ensureCorsReady() {
  const response = await fetch(`${backendBaseUrl}/api/opportunity/1/actions/%E7%A1%AE%E8%AE%A4%207%20%E5%A4%A9%E5%B0%8F%E6%A0%B7%E9%AA%8C%E8%AF%81%E6%96%B9%E6%A1%88`, {
    method: 'OPTIONS',
    headers: {
      Origin: corsOrigin,
      'Access-Control-Request-Method': 'POST'
    }
  })
  const allowOrigin = response.headers.get('access-control-allow-origin')
  if (!response.ok || allowOrigin !== corsOrigin) {
    throw new Error(`cors preflight failed: status=${response.status}, allow-origin=${allowOrigin ?? 'null'}`)
  }
}

async function main() {
  try {
    await ensureBackendReachable()
    await ensureCorsReady()
    console.log(`[e2e-preflight] ok backend=${backendBaseUrl} corsOrigin=${corsOrigin}`)
  } catch (error) {
    const message = error instanceof Error ? error.message : String(error)
    console.error(`[e2e-preflight] failed: ${message}`)
    process.exit(1)
  }
}

await main()
