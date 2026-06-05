export interface BrandRequest {
  brandName: string
  industry: string
  targetCategory: string
  hasCategory: boolean
  interestDirection: string
  targetPlatforms: string[]
  budgetRange: string
  profitMin: string
  supplyChain: string
  stockCycle: string
  excludeCategories: string[]
  existingProducts?: string
}

export interface BrandInfo extends Omit<BrandRequest, 'targetPlatforms' | 'excludeCategories'> {
  id: number
  targetPlatforms: string
  excludeCategories: string
}

export interface CategoryTrend {
  id: number
  categoryName: string
  platform: string
  trendMonth: string
  searchVolume: number
  salesVolume: number
  growthRate: number
  socialHeat: number
  risingWords: string
}

export interface CompetitionData {
  id: number
  categoryName: string
  platform: string
  totalSearchVolume: number
  totalSkuCount: number
  top10SalesRatio: number
  cr3: number
  cr5: number
  homogeneityScore: number
  conclusion: string
}

export interface SupplyDemand {
  id: number
  categoryName: string
  platform: string
  priceRange: string
  searchVolume: number
  supplyCount: number
  demandSupplyRatio: number
}

export interface InsightCard {
  id: number
  brandId: number
  categoryName: string
  marketSize: string
  marketGrowth: string
  competitionPattern: string
  competitionLevel: string
  priceGap: string
  estimatedStartupCost: string
  recommendation: string
}

export interface InsightCardView {
  card: InsightCard
  pinned: boolean
  budgetCompatible: boolean
  matchTags: string[]
  decision: string
  scoreBreakdown: ScoreBreakdown
  reasons: ReasonItem[]
  risks: RiskItem[]
  brandFitDetail: BrandFitDetail
  mismatches: ConstraintMismatch[]
}

export interface SignalItem {
  id: string
  categoryName: string
  signalType: string
  strength: string
  score: number
  confidence: number
  platform: string
  metric: string
  summary: string
  discoveredAt: string
  cardId: number | null
  recommendedAction: string
  reasonTags: string[]
  decision: string
  brandFitDetail: BrandFitDetail | null
  reasons: ReasonItem[]
  risks: RiskItem[]
  mismatches: ConstraintMismatch[]
}

export interface CompetitorShop {
  shopName: string
  platform: string
  focusCategory: string
  latestHit: string
  growthSignal: string
  addedAt: string
  cardId: number | null
  sourceSignalId: string | null
  sourceSignalType: string | null
  recentLaunch: string
  hitProductCount: number
  complaintTopics: string[]
  opportunityTags: string[]
}

export interface CompetitorTimelinePoint {
  period: string
  heatIndex: number
  salesIndex: number
  note: string
}

export interface CompetitorTimeline {
  shopName: string
  platform: string
  focusCategory: string
  trendLabel: string
  summary: string
  points: CompetitorTimelinePoint[]
}

export interface CompetitorSummary {
  trackedShopCount: number
  coveredPlatforms: string
  totalHitProductCount: number
  commonComplaintTopics: string[]
  strongestSignal: string
  summary: string
}

export interface PlatformPlaybook {
  firstLaunchPlatform: string
  validationPlatform: string
  conversionPlatform: string
  executionHints: string[]
  summary: string
}

export interface DashboardSummary {
  monitoredCategories: number
  activeSignals: number
  bestScore: number
  topCategory: string
  bestCardId: number | null
  brandName: string
  watchlistCount: number
  opportunityRankingTotal: number
}

export interface WorkflowStage {
  key: string
  title: string
  status: 'done' | 'current' | 'pending'
  summary: string
  nextAction: string
}

export interface WorkflowProgress {
  brandId: number
  brandName: string
  currentStage: string
  recommendedNextStage: string
  stages: WorkflowStage[]
  signalCount: number
  insightCount: number
  opportunityCount: number
  reportReady: boolean
}

export interface SelectionReport {
  title: string
  generatedAt: string
  format: string
  content: string
  fileName: string
  platformView: string
  brandSummary: string
  decisionSummary: DecisionSummary
  competitionReport: CompetitionReport
  profitAnalysis: ProfitAnalysis
  supplyChainFeasibility: SupplyChainFeasibility
  riskSummary: ReportRiskSummary
  keySignals: string[]
  opportunityHighlights: string[]
  opportunityNarrative: OpportunityNarrative
  actionSummary: ReportActionSummary
  nextActions: ReportAction[]
}

export interface ReportRiskSummary {
  totalCount: number
  highestLevel: string
  primaryRiskTitle: string
  primaryRiskDescription: string
  suggestedAttention: string
  summary: string
}

export interface ReportActionSummary {
  totalCount: number
  completedCount: number
  inProgressCount: number
  pendingCount: number
  focusActionTitle: string
  focusActionStatus: string
  latestUpdatedAt: string
  summary: string
}

export interface OpportunityNarrative {
  competitorSummary: CompetitorSummary
  platformPlaybook: PlatformPlaybook
  competitorComparison: CompetitorShop[]
  differentiationAdvice: string[]
  competitorFocusReasons: CompetitorFocusReason[]
  opportunityLensFocuses: OpportunityLensFocus[]
}

export interface CompetitorFocusReason {
  shopName: string
  reason: string
}

export interface OpportunityLensFocus {
  lensKey: string
  lensLabel: string
  summary: string
  targetCrowd: string
  scenarioText: string
  differentiation: string
  opportunityScore: number
  opportunityLevel: string
  entryTiming: string
  lifecycleStage: string
  reason: string
}

export interface CategoryBrief {
  categoryName: string
  metric: string
  description: string
  monthlySearchVolume?: number | null
  growthRate12m?: string | null
  socialHeat?: number | null
  risingWords?: string | null
  platformGrowthRates?: string | null
  categoryDescription?: string | null
  tamSamSomSummary?: string | null
}

export interface PainPointItem {
  rank: number
  topic: string
  crossCompetitorFrequency: number
  sentimentLevel: string
  summary: string
}

export interface OpportunityRankItem {
  rank: number
  cardId: number | null
  productTitle: string
  categoryName: string
  opportunityScore: number
  recommendationReason: string
  decision: string
  sellingPoint: SellingPointSuggestion
}

export interface OpportunityRankingPage {
  total: number
  page: number
  pageSize: number
  items: OpportunityRankItem[]
}

export interface SellingPointSuggestion {
  suggestedPriceBand: string
  sellingPoint: string
  differentiationDirection: string
}

export interface PatentRecordItem {
  applicationNo: string
  title: string
  status: string
  applicant: string
  filingDate: string
}

export interface PatentIntel {
  riskLevel: string
  summary: string
  highlights: string[]
  dataProvider: string
  syncedAt: string
  searchQuery: string
  records: PatentRecordItem[]
}

export interface AlibabaOfferItem {
  offerId: string
  title: string
  unitPrice: string
  moq: string
  factoryName: string
  location: string
  creditLevel: string
}

export interface Alibaba1688Intel {
  priceRange: string
  moq: string
  factoryCapacity: string
  summary: string
  sampleSuppliers: string[]
  dataProvider: string
  syncedAt: string
  categoryKeyword: string
  offers: AlibabaOfferItem[]
}

export interface DataConnectorStatus {
  provider: string
  providerType: string
  status: string
  lastSyncedAt: string
  coverage: string
  sampleHighlight: string
  demoData: boolean
}

export interface ChanmamaFeedSample {
  productTitle: string
  categoryName: string
  salesGrowth7d: string
  liveRank: string
  gmvBand: string
}

export interface FeiguaFeedSample {
  videoTitle: string
  categoryName: string
  playGrowth7d: string
  influencerTier: string
  hotTopic: string
}

export interface DataConnectorsOverview {
  connectors: DataConnectorStatus[]
  chanmamaFeeds: ChanmamaFeedSample[]
  feiguaFeeds: FeiguaFeedSample[]
  marketDataSource: string
  lastMarketSyncAt: string
  lastMarketSyncStatus: string
}

export interface MarketDataSyncResult {
  success: boolean
  message: string
  syncedAt: string
}

export interface SupplyMatchItem {
  supplierName: string
  region: string
  productTitle: string
  unitPrice: string
  moq: string
  creditLevel: string
  matchScore: number
  matchReason: string
}

export interface TeamMemberItem {
  id: number
  memberName: string
  roleLabel: string
  email?: string
}

export interface TeamAssignmentItem {
  id: number
  cardId?: number | null
  actionTitle: string
  assigneeName: string
  status: string
  note?: string
}

export interface KpiMetricItem {
  key: string
  label: string
  actualValue: string
  targetValue: string
  trend: string
  status: string
}

export interface ProductMetricsKpi {
  phaseLabel: string
  asOfDate: string
  demoData: boolean
  summary: string
  metrics: KpiMetricItem[]
}

export interface PushDeliveryRecord {
  id: number
  brandId: number
  channelType: string
  status: string
  webhookMasked: string
  payloadPreview: string
  responseBody: string
  deliveredAt: string
}

export interface TestRunMetric {
  key: string
  label: string
  actualValue: string
  benchmarkValue: string
  status: 'good' | 'warn' | 'bad' | string
  hint: string
}

export interface TestRunDiagnosis {
  brandId: number
  cardId: number
  productTitle: string
  categoryName: string
  platform: string
  weekLabel: string
  verdict: string
  confidence: number
  summary: string
  metrics: TestRunMetric[]
  scaleUpActions: string[]
  stopSignals: string[]
  demoData: boolean
}

export interface WatchlistItem {
  id: number
  brandId: number
  cardId: number | null
  categoryName: string
  note: string | null
  createdAt: string
}

export interface PushChannelConfig {
  id: number
  brandId: number
  channelType: string
  webhookUrl: string
  enabled: boolean
  updatedAt: string
}

export interface PushDigestResult {
  success: boolean
  message: string
  signalCount: number
  channelResults: string[]
  deliveries: PushDeliveryRecord[]
}

export interface SelectionAttributionReport {
  brandName: string
  summary: string
  successFactors: AttributionInsight[]
  failureFactors: AttributionInsight[]
  nextQuarterOpportunities: string[]
}

export interface AttributionInsight {
  title: string
  description: string
  evidence: string
}

export interface BrandSelectionModelProfile {
  brandId: number
  brandName: string
  trendWeight: number
  competitionWeight: number
  supplyGapWeight: number
  brandFitWeight: number
  riskPenaltyWeight: number
  modelVersion: string
  trainingSummary: string
  expectedAccuracyGain: string
}

export interface ScoreBreakdown {
  trendScore: number
  competitionScore: number
  supplyGapScore: number
  brandFitScore: number
  riskPenalty: number
  totalScore: number
  confidence: number
}

export interface ReasonItem {
  title: string
  description: string
  source: string
}

export interface RiskItem {
  title: string
  level: string
  description: string
}

export interface DecisionSummary {
  decision: string
  confidence: number
  headline: string
  reasons: ReasonItem[]
  risks: RiskItem[]
  scoreBreakdown: ScoreBreakdown
}

export interface InsightSummary {
  brand: BrandInfo
  trendConclusion: string
  trendTop3: CategoryBrief[]
  competitionConclusion: string
  competitionTop3: CategoryBrief[]
  supplyConclusion: string
  supplyTop3: CategoryBrief[]
  trendJudgment: string
  painPointItems: PainPointItem[]
  painPoints: string[]
  crowdProfile: string
  skippedCards: InsightCardView[]
  blockingReasons: string[]
  recommendedAdjustments: string[]
  filteredCategories: string[]
  potentialCategories: PotentialCategoryItem[]
  marketScaleBrief: MarketScaleBrief
}

export interface PotentialCategoryItem {
  categoryName: string
  searchGrowth: string
  socialTrend: string
  risingWords: string
  socialSyncUp: boolean
  summary: string
  tam?: string
  sam?: string
  som?: string
  tamSamSomSummary?: string
}

export interface MarketScaleBrief {
  categoryName: string
  tam: string
  sam: string
  som: string
  annualGrowth: string
  summary: string
}

export interface OpportunityMarketContext {
  cpcLevel: string
  cpcVsCategory: string
  logisticsCostHint: string
  weightVolumeRatio: string
  platformPolicySignal: string
  trafficBonusChannel: string
  summary: string
}

export interface EntryBarrierAssessment {
  newProductListingCycle: string
  topCommentThreshold: string
  cpcBarrier: string
  patentBarrier: string
  supplyChainBarrier: string
  overallLevel: string
  summary: string
}

export interface PriceFunctionPoint {
  label: string
  priceIndex: number
  functionIndex: number
  quadrant: string
  role: string
}

export interface CompetitionQuadrantReport {
  points: PriceFunctionPoint[]
  blankZone: string
  summary: string
}

export interface SupplyDemandGapModel {
  demandHeat: number
  supplyAdequacy: number
  satisfactionGap: number
  gapIndex: number
  priceVacuumBand: string
  summary: string
}

export interface PriceBandItem {
  priceRange: string
  skuCount: number
  salesSharePercent: number
  gapHint: string
}

export interface PriceBandDistribution {
  platform: string
  bands: PriceBandItem[]
  bestVacuumBand: string
  summary: string
}

export interface LifecycleInsight {
  lifecycleStage: string
  growthAccelerating: boolean
  secondDerivativeLabel: string
  latestGrowthRate: number
  growthAcceleration: number
  firstHitTimeline: string
  firstHitMonthsAgo: number
  summary: string
}

export interface CategoryMarketMetrics {
  platform: string
  totalSkuCount: number
  top10SalesRatio: number
  homogeneityScore: number
  totalSearchVolume: number
  summary: string
}

export interface OpportunityPoint {
  id: number
  insightCardId: number
  categoryName: string
  opportunityGravity: number
  competitionResistance: number
  profitElasticity: number
  opportunityScore: number
  opportunityLevel: string
  targetCrowd: string
  scenarioText: string
  differentiation: string
  marketEstimate: string
  entryTiming: string
  lifecycleStage: string
  decision: string
  reason: string
}

export interface SentimentTerm {
  name: string
  value: number
  sentiment: 'positive' | 'negative'
}

export interface CrowdScene {
  crowd: string
  scene: string
  painPoint: string
  desiredValue: string
}

export interface ConstraintMatch {
  matched: string[]
  warnings: string[]
  brandFitDetail: BrandFitDetail
  mismatches: ConstraintMismatch[]
}

export interface BrandFitDetail {
  platformFit: string
  budgetFit: string
  profitFit: string
  supplyChainFit: string
  stockCycleFit: string
  overallFitLevel: string
  summary: string
}

export interface ConstraintMismatch {
  type: string
  message: string
  severity: string
}

export interface ProfitAnalysis {
  targetPrice: string
  unitCost: string
  platformFee: string
  adCost: string
  netMargin: string
  summary: string
}

export interface SupplyChainFeasibility {
  moq: string
  leadTime: string
  factoryCapacity: string
  riskHint: string
  conclusion: string
}

export interface CompetitionReport {
  marketType: string
  cr3?: string
  cr5: string
  entryWindow: string
  summary: string
}

export interface ExternalDriverItem {
  driverType: string
  signal: string
  impact: string
}

export interface ReportAction {
  title: string
  ownerRole: string
  expectedGoal: string
  priority: string
  eta: string
  status: string
  updatedAt: string
  note: string
}

export interface OpportunityDetail {
  insightCard: InsightCard
  brand: BrandInfo
  decisionSummary: DecisionSummary
  scoreBreakdown: ScoreBreakdown
  constraintMatch: ConstraintMatch
  brandFitDetail: BrandFitDetail
  competitionReport: CompetitionReport
  profitAnalysis: ProfitAnalysis
  supplyChainFeasibility: SupplyChainFeasibility
  platformPlaybook: PlatformPlaybook
  relatedCompetitors: CompetitorShop[]
  competitorSummary: CompetitorSummary
  differentiationAdvice: string[]
  nextActions: ReportAction[]
  points: OpportunityPoint[]
  sentimentTerms: SentimentTerm[]
  crowdScenes: CrowdScene[]
  patentIntel: PatentIntel
  alibaba1688Intel: Alibaba1688Intel
  sellingPoints: SellingPointSuggestion[]
  marketContext: OpportunityMarketContext
  externalDrivers?: ExternalDriverItem[]
  entryBarrier: EntryBarrierAssessment
  competitionQuadrant: CompetitionQuadrantReport
  supplyDemandGapModel: SupplyDemandGapModel
  priceBandDistribution: PriceBandDistribution
  lifecycleInsight: LifecycleInsight
  categoryMarketMetrics: CategoryMarketMetrics
}
