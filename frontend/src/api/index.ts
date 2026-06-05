import { DEFAULT_PLATFORM_VIEW } from '@/constants/brand'
import { http, resolveBrandId } from '@/api/http'
import type {
  BrandInfo,
  BrandRequest,
  BrandSelectionModelProfile,
  CategoryTrend,
  CompetitorShop,
  CompetitorTimeline,
  CompetitionData,
  DashboardSummary,
  DataConnectorsOverview,
  ProductMetricsKpi,
  PushDeliveryRecord,
  InsightCardView,
  InsightSummary,
  OpportunityDetail,
  OpportunityPoint,
  OpportunityRankingPage,
  PushChannelConfig,
  PushDigestResult,
  SelectionAttributionReport,
  SelectionReport,
  SignalItem,
  SupplyDemand,
  TestRunDiagnosis,
  WatchlistItem,
  WorkflowProgress
} from '@/types'

export const api = {
  createBrand(payload: BrandRequest) {
    return http.post<BrandInfo>('/brand', payload).then((res) => res.data)
  },
  getBrand(id: number) {
    return http.get<BrandInfo>(`/brand/${id}`).then((res) => res.data)
  },
  getDashboard(brandId?: number) {
    return http.get<DashboardSummary>('/dashboard', { params: { brandId: resolveBrandId(brandId) } }).then((res) => res.data)
  },
  getWorkflow(brandId?: number) {
    return http.get<WorkflowProgress>('/dashboard/workflow', { params: { brandId: resolveBrandId(brandId) } }).then((res) => res.data)
  },
  getProductMetrics() {
    return http.get<ProductMetricsKpi>('/dashboard/product-metrics').then((res) => res.data)
  },
  getDataConnectors() {
    return http.get<DataConnectorsOverview>('/connectors/overview').then((res) => res.data)
  },
  getTrends(brandId?: number) {
    return http.get<CategoryTrend[]>('/insight/trend', { params: { brandId: resolveBrandId(brandId) } }).then((res) => res.data)
  },
  getCompetition(brandId?: number) {
    return http.get<CompetitionData[]>('/insight/competition', { params: { brandId: resolveBrandId(brandId) } }).then((res) => res.data)
  },
  getSupplyDemand(brandId?: number) {
    return http.get<SupplyDemand[]>('/insight/supply-demand', { params: { brandId: resolveBrandId(brandId) } }).then((res) => res.data)
  },
  getInsightCards(brandId?: number, platform?: string) {
    return http
      .get<InsightCardView[]>('/insight/cards', {
        params: { brandId: resolveBrandId(brandId), platform: platform || undefined }
      })
      .then((res) => res.data)
  },
  getInsightSummary(brandId?: number, platform?: string) {
    return http
      .get<InsightSummary>('/insight/summary', {
        params: { brandId: resolveBrandId(brandId), platform: platform || undefined }
      })
      .then((res) => res.data)
  },
  getSignals(brandId?: number) {
    return http.get<SignalItem[]>('/radar/signals', { params: { brandId: resolveBrandId(brandId) } }).then((res) => res.data)
  },
  getCompetitors(brandId?: number) {
    return http.get<CompetitorShop[]>('/competitor', { params: { brandId: resolveBrandId(brandId) } }).then((res) => res.data)
  },
  getCompetitorTimelines(brandId?: number, category = '', platform = DEFAULT_PLATFORM_VIEW) {
    return http
      .get<CompetitorTimeline[]>('/competitor/timeline', {
        params: { brandId: resolveBrandId(brandId), category, platform }
      })
      .then((res) => res.data)
  },
  addCompetitor(
    brandId: number,
    payload: {
      shopName: string
      platform: string
      focusCategory?: string
      cardId?: number | null
      sourceSignalId?: string
      sourceSignalType?: string
    }
  ) {
    return http.post<CompetitorShop>('/competitor', payload, { params: { brandId } }).then((res) => res.data)
  },
  exportReport(cardId: number, brandId?: number, platform = DEFAULT_PLATFORM_VIEW) {
    return http
      .get<SelectionReport>(`/report/${cardId}`, { params: { brandId: resolveBrandId(brandId), platform } })
      .then((res) => res.data)
  },
  getOpportunity(cardId: number, brandId?: number, platform = DEFAULT_PLATFORM_VIEW) {
    return http
      .get<OpportunityDetail>(`/opportunity/${cardId}`, { params: { brandId: resolveBrandId(brandId), platform } })
      .then((res) => res.data)
  },
  updateOpportunityAction(cardId: number, actionTitle: string, payload: { status: string; note?: string }) {
    return http.post(`/opportunity/${cardId}/actions/${encodeURIComponent(actionTitle)}`, payload).then((res) => res.data)
  },
  getOpportunityPoints(cardId: number, brandId?: number, platform = DEFAULT_PLATFORM_VIEW) {
    return http
      .get<OpportunityPoint[]>(`/opportunity/${cardId}/points`, {
        params: { brandId: resolveBrandId(brandId), platform }
      })
      .then((res) => res.data)
  },
  getTop50Ranking(brandId?: number, page = 1, pageSize = 50) {
    return http
      .get<OpportunityRankingPage>('/ranking/top50', {
        params: { brandId: resolveBrandId(brandId), page, pageSize }
      })
      .then((res) => res.data)
  },
  getWatchlist(brandId?: number) {
    return http.get<WatchlistItem[]>('/watchlist', { params: { brandId: resolveBrandId(brandId) } }).then((res) => res.data)
  },
  addWatchlist(brandId: number, categoryName: string, cardId?: number, note?: string) {
    return http
      .post<WatchlistItem>('/watchlist', null, {
        params: { brandId, categoryName, cardId, note }
      })
      .then((res) => res.data)
  },
  removeWatchlist(brandId: number, id: number) {
    return http.delete(`/watchlist/${id}`, { params: { brandId } })
  },
  getPushConfig(brandId?: number) {
    return http.get<PushChannelConfig[]>('/push/config', { params: { brandId: resolveBrandId(brandId) } }).then((res) => res.data)
  },
  savePushConfig(brandId: number, payload: { channelType: string; webhookUrl: string; enabled: boolean }) {
    return http.post<PushChannelConfig>('/push/config', payload, { params: { brandId } }).then((res) => res.data)
  },
  pushDigest(brandId?: number) {
    return http.post<PushDigestResult>('/push/digest', null, { params: { brandId: resolveBrandId(brandId) } }).then((res) => res.data)
  },
  getPushDeliveries(brandId?: number, limit = 10) {
    return http
      .get<PushDeliveryRecord[]>('/push/deliveries', { params: { brandId: resolveBrandId(brandId), limit } })
      .then((res) => res.data)
  },
  suggestCompetitors(brandId?: number) {
    return http.get<CompetitorShop[]>('/competitor/suggestions', { params: { brandId: resolveBrandId(brandId) } }).then((res) => res.data)
  },
  discoverCompetitors(brandId?: number) {
    return http.post<CompetitorShop[]>('/competitor/discover', null, { params: { brandId: resolveBrandId(brandId) } }).then((res) => res.data)
  },
  getAttributionReport(brandId?: number) {
    return http.get<SelectionAttributionReport>('/attribution/report', { params: { brandId: resolveBrandId(brandId) } }).then((res) => res.data)
  },
  getBrandModel(brandId?: number) {
    return http.get<BrandSelectionModelProfile>('/brand-model', { params: { brandId: resolveBrandId(brandId) } }).then((res) => res.data)
  },
  retrainBrandModel(brandId?: number) {
    return http.post<BrandSelectionModelProfile>('/brand-model/retrain', null, { params: { brandId: resolveBrandId(brandId) } }).then((res) => res.data)
  },
  downloadReportExcel(cardId: number, brandId?: number, platform = DEFAULT_PLATFORM_VIEW) {
    return http.get<Blob>(`/report/${cardId}/export/excel`, {
      params: { brandId: resolveBrandId(brandId), platform },
      responseType: 'blob'
    })
  },
  downloadReportPdf(cardId: number, brandId?: number, platform = DEFAULT_PLATFORM_VIEW) {
    return http.get<Blob>(`/report/${cardId}/export/pdf`, {
      params: { brandId: resolveBrandId(brandId), platform },
      responseType: 'blob'
    })
  },
  getTestRunDiagnosis(cardId: number, brandId?: number, platform = DEFAULT_PLATFORM_VIEW) {
    return http
      .get<TestRunDiagnosis>('/test-run/diagnosis', {
        params: { brandId: resolveBrandId(brandId), cardId, platform }
      })
      .then((res) => res.data)
  },
  callOpenApi<T>(path: string, apiKey: string, params: Record<string, string | number> = {}) {
    return http
      .get<T>(`/open/v1${path}`, {
        params,
        headers: { 'X-Api-Key': apiKey }
      })
      .then((res) => res.data)
  }
}
