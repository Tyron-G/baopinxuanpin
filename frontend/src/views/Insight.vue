<template>
  <section>
    <PageHero
      eyebrow="洞察发现"
      title="把候选赛道先筛成可判断的机会池"
      description="从市场趋势、竞争集中度和价格带供需缺口中筛选可进入的细分赛道。"
    >
      <template #actions>
        <el-button type="primary" :icon="Aim" :disabled="!primaryCard" @click="openBestCard">进入最佳机会</el-button>
      </template>
    </PageHero>

    <WorkflowSummary current-stage="insight" :workflow="workflow" />

    <ProductMetricsPanel />

    <section class="panel pad insight-hero">
      <div class="insight-hero-main">
        <div class="insight-hero-copy">
          <span class="eyebrow">管理摘要</span>
          <h2>{{ summary?.brand.brandName ?? `Brand #${brandId}` }} 当前优先看 {{ primaryCard?.categoryName ?? '候选赛道' }}</h2>
          <p>{{ summary?.trendJudgment ?? '优先看增长、再看集中度，最后用价格带缺口和启动成本做落地过滤。' }}</p>
          <small v-if="summary">
            {{ summary.brand.industry }} · {{ platformText }} · 预算 {{ summary.brand.budgetRange || '未设置' }}
          </small>
        </div>
        <div class="insight-hero-actions">
          <div class="platform-toolbar-card">
            <span class="eyebrow">平台视角</span>
            <h3>统一洞察平台上下文</h3>
            <p>当前平台会同时影响图表、结论区和候选赛道排序。</p>
            <el-segmented v-model="activePlatform" :options="platformOptions" />
          </div>
        </div>
      </div>

      <div class="insight-hero-metrics">
        <article>
          <span>{{ focusMatched ? '当前定位类目' : '推荐赛道' }}</span>
          <b>{{ primaryCard?.categoryName ?? '-' }}</b>
          <small>当前最值得优先验证的赛道</small>
        </article>
        <article>
          <span>12月同比</span>
          <b>{{ primaryCard?.marketGrowth ?? '-' }}</b>
          <small>与 TOP3 同口径（Jan vs Dec）</small>
        </article>
        <article>
          <span>价格空白</span>
          <b>{{ primaryCard?.priceGap ?? '-' }}</b>
          <small>判断差异化带宽是否足够</small>
        </article>
        <article>
          <span>启动资金</span>
          <b>{{ primaryCard?.estimatedStartupCost ?? '-' }}</b>
          <small>决定试错和验证节奏</small>
        </article>
      </div>
    </section>

    <div v-if="focusCategory" class="panel pad focus-banner">
      <div>
        <span class="eyebrow">精准回跳</span>
        <h2>{{ focusMatched ? `已定位到 ${focusCategory}` : `未找到 ${focusCategory}` }}</h2>
        <p>
          {{ focusMatched
            ? '当前主动作、指标卡和类目列表都会优先围绕这个类目展开。'
            : '当前品牌约束下没有完全匹配的类目，系统仍保留全量洞察供你继续判断。' }}
        </p>
      </div>
      <div class="focus-actions">
        <el-button
          v-if="focusMatched && focusedView"
          type="primary"
          @click="openCard(focusedView.card.id)"
        >
          进入该类目机会
        </el-button>
        <el-button @click="clearFocus">查看全部类目</el-button>
      </div>
    </div>

    <WatchlistPanel
      v-if="primaryCard"
      :category-name="primaryCard.categoryName"
      :card-id="primaryCard.id"
    />

    <section v-if="summary?.potentialCategories?.length" class="panel pad potential-panel">
      <span class="eyebrow">潜力类目清单</span>
      <h2>12 月同比＞30% 且社媒同步上升的赛道</h2>
      <el-table :data="summary.potentialCategories" stripe size="small">
        <el-table-column prop="categoryName" label="类目" width="140" />
        <el-table-column prop="searchGrowth" label="12月同比" width="120" />
        <el-table-column prop="socialTrend" label="社媒趋势" width="120" />
        <el-table-column prop="risingWords" label="飙升词" />
        <el-table-column prop="tam" label="TAM" width="100" />
        <el-table-column prop="sam" label="SAM" width="100" />
        <el-table-column prop="som" label="SOM" width="100" />
        <el-table-column prop="socialSyncUp" label="同步上升" width="90">
          <template #default="{ row }">
            <el-tag :type="row.socialSyncUp ? 'success' : 'info'" size="small">{{ row.socialSyncUp ? '是' : '观察' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <section v-if="summary?.marketScaleBrief" class="panel pad market-scale-panel">
      <span class="eyebrow">市场规模（TAM / SAM / SOM）</span>
      <h2>{{ summary.marketScaleBrief.categoryName }}</h2>
      <div class="scale-grid">
        <article><span>TAM</span><b>{{ summary.marketScaleBrief.tam }}</b></article>
        <article><span>SAM</span><b>{{ summary.marketScaleBrief.sam }}</b></article>
        <article><span>SOM</span><b>{{ summary.marketScaleBrief.som }}</b></article>
        <article><span>年增速</span><b>{{ summary.marketScaleBrief.annualGrowth }}</b></article>
      </div>
      <p>{{ summary.marketScaleBrief.summary }}</p>
    </section>

    <div v-if="summary" class="signal-grid">
      <div class="panel pad signal-card">
        <span class="eyebrow">痛点优先级清单</span>
        <ul class="pain-list">
          <li v-for="item in summary.painPointItems" :key="item.rank">
            <b>#{{ item.rank }} {{ item.topic }}</b>
            <small>跨竞品 {{ item.crossCompetitorFrequency }} 次 · 情绪 {{ item.sentimentLevel }}</small>
            <p>{{ item.summary }}</p>
          </li>
        </ul>
      </div>
      <div class="panel pad signal-card">
        <span class="eyebrow">人群初步画像</span>
        <p>{{ summary.crowdProfile }}</p>
      </div>
      <div class="panel pad signal-card">
        <span class="eyebrow">当前建议动作</span>
        <p>{{ leadAdjustment }}</p>
        <div class="signal-meta">
          <article>
            <span>已过滤赛道</span>
            <b>{{ summary.filteredCategories.length }}</b>
          </article>
          <article>
            <span>推荐优先级</span>
            <b>{{ primaryCard?.categoryName ?? '-' }}</b>
          </article>
        </div>
      </div>
    </div>

    <div v-if="summary && (summary.blockingReasons.length || summary.recommendedAdjustments.length)" class="panel pad constraint-diagnostics">
      <div class="constraint-head">
        <div>
          <span class="eyebrow">当前约束影响</span>
          <h2>为什么系统这样排序</h2>
        </div>
        <p>这里展示当前品牌约束如何影响候选池和推荐顺序，帮助你判断是继续验证，还是先调整条件。</p>
      </div>

      <div class="constraint-grid">
        <article class="constraint-card">
          <h3>主要拦截因素</h3>
          <ul v-if="summary.blockingReasons.length">
            <li v-for="item in summary.blockingReasons" :key="item">{{ item }}</li>
          </ul>
          <p v-else>当前约束没有明显拦截项，推荐顺序主要由综合评分决定。</p>
        </article>

        <article class="constraint-card">
          <h3>建议调整动作</h3>
          <ul v-if="summary.recommendedAdjustments.length">
            <li v-for="item in summary.recommendedAdjustments" :key="item">{{ item }}</li>
          </ul>
          <p v-else>当前可以直接进入最佳机会页继续验证。</p>
        </article>
      </div>

      <div v-if="summary.filteredCategories.length" class="filtered-tags">
        <span>已过滤赛道</span>
        <el-tag
          v-for="item in summary.filteredCategories"
          :key="item"
          type="info"
          effect="plain"
          size="small"
        >
          {{ item }}
        </el-tag>
      </div>
    </div>

    <section class="insight-evidence">
      <div class="evidence-head">
        <div>
          <span class="eyebrow">证据层</span>
          <h2>把趋势、竞争和供需放到同一管理视角下看</h2>
          <p>先看哪条证据最能支撑当前推荐，再决定是否进入机会页做更深一层验证。</p>
        </div>
        <div class="evidence-summary">
          <article>
            <span>当前平台</span>
            <b>{{ activePlatform }}</b>
            <small>所有图表和结论保持同一平台语境</small>
          </article>
          <article>
            <span>当前推荐</span>
            <b>{{ primaryCard?.categoryName ?? '-' }}</b>
            <small>证据层围绕这条候选主线展开</small>
          </article>
          <article>
            <span>当前重点</span>
            <b>{{ activeEvidenceLabel }}</b>
            <small>{{ activePlatformLabel }}</small>
          </article>
        </div>
      </div>

      <div class="evidence-ribbon">
        <article>
          <span>当前重点证据</span>
          <b>{{ activeEvidenceLabel }}</b>
          <small>{{ activeEvidenceNarrative }}</small>
        </article>
        <article>
          <span>当前样本数</span>
          <b>{{ activeEvidenceCount }}</b>
          <small>{{ activePlatformLabel }}</small>
        </article>
        <article>
          <span>过滤影响</span>
          <b>{{ summary?.filteredCategories.length ?? 0 }}</b>
          <small>已被品牌约束排除的赛道数量</small>
        </article>
        <article>
          <span>优先动作</span>
          <b>{{ ribbonActionLabel }}</b>
          <small>{{ leadAdjustment }}</small>
        </article>
      </div>

      <el-tabs v-model="activeTab" class="insight-tabs">
        <el-tab-pane label="机会在哪" name="trend" lazy>
          <div class="evidence-grid">
            <MarketTrend ref="trendChartRef" :rows="trends" :platform="activePlatform" />
            <InsightConclusion
              v-if="summary"
              :conclusion="trendConclusionText"
              :top3="trendTop3Display"
              :platform-label="activePlatformLabel"
            />
          </div>
        </el-tab-pane>
        <el-tab-pane label="竞争难度" name="competition" lazy>
          <div class="evidence-grid">
            <CompetitionMap ref="competitionChartRef" :rows="competition" :platform="activePlatform" />
            <InsightConclusion
              v-if="summary"
              :conclusion="competitionConclusionText"
              :top3="competitionTop3Display"
              :platform-label="activePlatformLabel"
            />
          </div>
        </el-tab-pane>
        <el-tab-pane label="供需缺口" name="supply" lazy>
          <div class="evidence-grid">
            <SupplyDemand ref="supplyChartRef" :rows="supplyDemand" :platform="activePlatform" />
            <InsightConclusion
              v-if="summary"
              :conclusion="supplyConclusionText"
              :top3="supplyTop3Display"
              :platform-label="activePlatformLabel"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </section>

    <div v-if="cards.length === 0" class="panel pad empty-state">
      <el-result
        icon="warning"
        title="当前约束下暂无可用赛道"
        :sub-title="emptyStateSubtitle"
      >
        <template v-if="summary?.blockingReasons?.length" #extra>
          <div class="empty-actions">
            <ul class="empty-reasons">
              <li v-for="item in summary.blockingReasons" :key="item">{{ item }}</li>
            </ul>
            <el-button type="primary" @click="router.push('/data-prep')">返回数据准备</el-button>
          </div>
        </template>
        <template v-else #extra>
      <el-button type="primary" @click="router.push('/data-prep')">返回数据准备</el-button>
        </template>
      </el-result>
    </div>

    <template v-else>
      <section class="panel pad candidate-section">
        <div class="candidate-head">
          <div>
            <span class="eyebrow">候选赛道池</span>
            <h2>把当前可做和暂缓的赛道分开看</h2>
            <p>先看当前更值得进入机会页验证的候选，再看为什么其余类目没有被放在第一优先级。</p>
          </div>
          <div class="candidate-summary">
            <article>
              <span>当前可见赛道</span>
              <b>{{ displayCards.length }}</b>
              <small>已按当前平台和综合分重排</small>
            </article>
            <article>
              <span>第一推荐</span>
              <b>{{ primaryCard?.categoryName ?? '-' }}</b>
              <small>{{ activePlatformLabel }}</small>
            </article>
            <article>
              <span>次优候选</span>
              <b>{{ summary?.skippedCards?.length ?? 0 }}</b>
              <small>需要更强品牌匹配或更低进入阻力</small>
            </article>
          </div>
        </div>

        <div class="candidate-body">
          <div class="candidate-main">
            <div class="candidate-main-head">
              <div>
                <span>当前推荐主区</span>
                <b>{{ primaryCard?.categoryName ?? '候选赛道' }}</b>
              </div>
              <small>主区只保留当前更值得继续进入机会分析的候选，减少“所有卡片同等重要”的阅读噪音。</small>
            </div>
            <div class="card-grid">
              <InsightCard
                v-for="view in displayCards"
                :key="view.card.id"
                :view="view"
                :focused="isFocusedCard(view)"
                :platform-hint="cardPlatformHint(view.card.categoryName)"
                @select="openCard"
              />
            </div>
          </div>
          <aside v-if="summary?.skippedCards?.length" class="candidate-side">
            <div class="candidate-side-head">
              <span>次优候选区</span>
              <b>{{ summary.skippedCards.length }} 个类目暂缓优先</b>
              <small>这里集中说明“为什么没排第一”，帮助业务方快速判断是继续保留观察，还是直接排除。</small>
            </div>
            <SkippedReasonList :items="summary.skippedCards" compact />
          </aside>
        </div>
      </section>
    </template>
  </section>
</template>

<script setup lang="ts">
import { computed, defineAsyncComponent, nextTick, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Aim } from '@element-plus/icons-vue'
import { api } from '@/api'
import { getBrandId, setBrandId } from '@/composables/useBrandContext'
import type {
  CategoryTrend,
  CompetitionData,
  InsightCardView,
  InsightSummary,
  SupplyDemand as SupplyDemandType,
  WorkflowProgress
} from '@/types'
import PageHero from '@/components/common/PageHero.vue'
import WorkflowSummary from '@/components/common/WorkflowSummary.vue'
import InsightCard from '@/components/insight/InsightCard.vue'
import WatchlistPanel from '@/components/common/WatchlistPanel.vue'
import ProductMetricsPanel from '@/components/common/ProductMetricsPanel.vue'
import InsightConclusion from '@/components/insight/InsightConclusion.vue'
import SkippedReasonList from '@/components/insight/SkippedReasonList.vue'

const MarketTrend = defineAsyncComponent(() => import('@/components/insight/MarketTrend.vue'))
const CompetitionMap = defineAsyncComponent(() => import('@/components/insight/CompetitionMap.vue'))
const SupplyDemand = defineAsyncComponent(() => import('@/components/insight/SupplyDemand.vue'))

const route = useRoute()
const router = useRouter()
const activeTab = ref('trend')
const trendChartRef = ref<{ resize: () => void }>()
const competitionChartRef = ref<{ resize: () => void }>()
const supplyChartRef = ref<{ resize: () => void }>()
const trends = ref<CategoryTrend[]>([])
const competition = ref<CompetitionData[]>([])
const supplyDemand = ref<SupplyDemandType[]>([])
const cards = ref<InsightCardView[]>([])
const summary = ref<InsightSummary>()
const workflow = ref<WorkflowProgress>()
const activePlatform = ref('全平台')
const brandId = computed(() => {
  const queryId = Number(route.query.brandId)
  if (Number.isFinite(queryId) && queryId > 0) {
    setBrandId(queryId)
    return queryId
  }
  return getBrandId()
})
const focusCategory = computed(() => {
  const raw = route.query.category
  return typeof raw === 'string' ? raw.trim() : ''
})
const normalizedFocusCategory = computed(() => focusCategory.value.toLowerCase())
const platformOptions = computed(() => {
  const merged = [...trends.value, ...competition.value, ...supplyDemand.value]
  return [...new Set(merged.map((item) => item.platform))]
})
const trendPlatformRows = computed(() => filterByPlatform(trends.value, activePlatform.value))
const competitionPlatformRows = computed(() => filterByPlatform(competition.value, activePlatform.value))
const supplyPlatformRows = computed(() => filterByPlatform(supplyDemand.value, activePlatform.value))
const focusedView = computed(() =>
  cards.value.find((view) => {
    const name = view.card.categoryName.toLowerCase()
    const focus = normalizedFocusCategory.value
    return focus && (name.includes(focus) || focus.includes(name))
  })
)
const focusMatched = computed(() => Boolean(focusedView.value))
const displayCards = computed(() => {
  const weighted = cards.value
    .map((view) => ({ view, score: cardPlatformWeight(view.card.categoryName) }))
    .sort((left, right) => {
      if (left.view.pinned !== right.view.pinned) {
        return left.view.pinned ? -1 : 1
      }
      const platformDiff = right.score - left.score
      if (platformDiff !== 0) return platformDiff
      return right.view.scoreBreakdown.totalScore - left.view.scoreBreakdown.totalScore
    })
    .map((item) => item.view)
  if (!focusedView.value) {
    return weighted
  }
  return [
    focusedView.value,
    ...weighted.filter((view) => view.card.id !== focusedView.value?.card.id)
  ]
})
const primaryCard = computed(() => focusedView.value?.card ?? displayCards.value[0]?.card)
const platformText = computed(() => summary.value?.brand.targetPlatforms?.split(',').join(' / ') ?? '-')
const activePlatformLabel = computed(() => activePlatform.value === '全平台' ? '当前为全平台聚合视角' : `当前为 ${activePlatform.value} 平台视角`)
const activeEvidenceLabel = computed(() => {
  if (activeTab.value === 'competition') return '竞争难度'
  if (activeTab.value === 'supply') return '供需缺口'
  return '机会在哪'
})
const activeEvidenceCount = computed(() => {
  if (activeTab.value === 'competition') return competitionPlatformRows.value.length
  if (activeTab.value === 'supply') return supplyPlatformRows.value.length
  return trendPlatformRows.value.length
})
const activeEvidenceNarrative = computed(() => {
  if (activeTab.value === 'competition') {
    return `当前优先复核 ${primaryCard.value?.categoryName ?? '候选赛道'} 的竞争集中度和进入阻力。`
  }
  if (activeTab.value === 'supply') {
    return `当前优先验证 ${primaryCard.value?.categoryName ?? '候选赛道'} 在价格带和供需上的缺口。`
  }
  return `当前优先确认 ${primaryCard.value?.categoryName ?? '候选赛道'} 的增长动能是否还能持续。`
})
const leadAdjustment = computed(() => summary.value?.recommendedAdjustments[0] ?? '当前约束下可直接进入机会页继续验证。')
const ribbonActionLabel = computed(() => {
  if ((summary.value?.recommendedAdjustments.length ?? 0) > 0) return '先调整'
  return '直接验证'
})
const trendConclusionText = computed(() => buildPlatformConclusion(summary.value?.trendConclusion, trendPlatformRows.value, 'growthRate', '搜索与销量增速'))
const competitionConclusionText = computed(() => buildPlatformConclusion(summary.value?.competitionConclusion, competitionPlatformRows.value, 'cr5', '头部集中度'))
const supplyConclusionText = computed(() => buildPlatformConclusion(summary.value?.supplyConclusion, supplyPlatformRows.value, 'demandSupplyRatio', '供需缺口'))
const trendTop3Display = computed(() => summary.value?.trendTop3 ?? [])
const competitionTop3Display = computed(() => summary.value?.competitionTop3 ?? [])
const supplyTop3Display = computed(() => summary.value?.supplyTop3 ?? [])
const emptyStateSubtitle = computed(() => {
  if (summary.value?.recommendedAdjustments?.length) {
    return `建议先调整：${summary.value.recommendedAdjustments[0]}`
  }
  return '请返回数据准备页调整排除品类、预算或目标品类后重新生成洞察。'
})

async function loadInsight() {
  const id = brandId.value
  const [trendRows, competitionRows, supplyRows, cardRows, summaryRow, workflowData] = await Promise.all([
    api.getTrends(id, activePlatform.value),
    api.getCompetition(id, activePlatform.value),
    api.getSupplyDemand(id, activePlatform.value),
    api.getInsightCards(id, activePlatform.value),
    api.getInsightSummary(id, activePlatform.value),
    api.getWorkflow(id, activePlatform.value)
  ])
  trends.value = trendRows
  competition.value = competitionRows
  supplyDemand.value = supplyRows
  cards.value = cardRows
  summary.value = summaryRow
  workflow.value = workflowData
  await nextTick()
  resizeActiveChart()
}

onMounted(loadInsight)
watch(brandId, loadInsight)

watch(activeTab, async () => {
  await nextTick()
  resizeActiveChart()
  window.setTimeout(resizeActiveChart, 80)
})
watch(platformOptions, (options) => {
  if (options.length && !options.includes(activePlatform.value)) {
    activePlatform.value = '全平台'
  }
})
watch(
  () => route.query.platform,
  (value) => {
    if (typeof value === 'string' && value.trim()) {
      activePlatform.value = value
      return
    }
    activePlatform.value = '全平台'
  },
  { immediate: true }
)
watch(activePlatform, async (platform) => {
  if (!brandId.value) return
  const [summaryRow, cardRows, trendRows, competitionRows, supplyRows] = await Promise.all([
    api.getInsightSummary(brandId.value, platform),
    api.getInsightCards(brandId.value, platform),
    api.getTrends(brandId.value, platform),
    api.getCompetition(brandId.value, platform),
    api.getSupplyDemand(brandId.value, platform)
  ])
  summary.value = summaryRow
  cards.value = cardRows
  trends.value = trendRows
  competition.value = competitionRows
  supplyDemand.value = supplyRows
  await nextTick()
  resizeActiveChart()
})

function openCard(id: number) {
  router.push({
    path: `/opportunity/${id}`,
    query: { brandId: brandId.value, platform: activePlatform.value }
  })
}

function openBestCard() {
  if (primaryCard.value) openCard(primaryCard.value.id)
}

function resizeActiveChart() {
  if (activeTab.value === 'trend') trendChartRef.value?.resize()
  if (activeTab.value === 'competition') competitionChartRef.value?.resize()
  if (activeTab.value === 'supply') supplyChartRef.value?.resize()
}

function isFocusedCard(view: InsightCardView) {
  return focusMatched.value && focusedView.value?.card.id === view.card.id
}

function clearFocus() {
  router.push({ path: '/insight', query: { brandId: brandId.value, platform: activePlatform.value } })
}

function filterByPlatform<T extends { platform: string }>(rows: T[], platform: string) {
  const matched = rows.filter((item) => item.platform === platform)
  if (matched.length) return matched
  return rows.filter((item) => item.platform === '全平台')
}

function buildPlatformConclusion(
  base: string | undefined,
  rows: Array<Record<string, any>>,
  scoreKey: string,
  metricName: string
) {
  if (!base) return '暂无分析结论'
  if (activePlatform.value === '全平台' || !rows.length) return base
  const ranked = [...rows].sort((left, right) => Number(right[scoreKey]) - Number(left[scoreKey]))
  const top = ranked[0]
  return `${activePlatform.value} 视角下，${top.categoryName} 当前在${metricName}上表现最强。${base}`
}

function cardPlatformWeight(categoryName: string) {
  if (activePlatform.value === '全平台') return 0
  let score = 0
  const trendHit = trendPlatformRows.value.find((item) => item.categoryName === categoryName)
  const competitionHit = competitionPlatformRows.value.find((item) => item.categoryName === categoryName)
  const supplyHit = supplyPlatformRows.value.find((item) => item.categoryName === categoryName)
  if (trendHit) score += Number(trendHit.growthRate)
  if (competitionHit) score += Math.max(0, 50 - Number(competitionHit.cr5))
  if (supplyHit) score += Number(supplyHit.demandSupplyRatio)
  return score
}

function cardPlatformHint(categoryName: string) {
  if (activePlatform.value === '全平台') return ''
  const trendHit = trendPlatformRows.value.find((item) => item.categoryName === categoryName)
  const competitionHit = competitionPlatformRows.value.find((item) => item.categoryName === categoryName)
  const supplyHit = supplyPlatformRows.value.find((item) => item.categoryName === categoryName)
  if (trendHit && Number(trendHit.growthRate) >= 25) return `${activePlatform.value} 趋势更强`
  if (competitionHit && Number(competitionHit.cr5) <= 35) return `${activePlatform.value} 竞争更松`
  if (supplyHit && Number(supplyHit.demandSupplyRatio) >= 35) return `${activePlatform.value} 缺口更明显`
  return `${activePlatform.value} 继续验证`
}
</script>

<style scoped>
.eyebrow,
.platform-toolbar-card span,
.insight-hero-copy small {
  display: block;
}

.eyebrow {
  color: var(--muted);
  font-size: 12px;
  text-transform: uppercase;
}

.insight-hero {
  margin-bottom: 16px;
  background:
    linear-gradient(135deg, rgba(239, 246, 255, 0.92), rgba(240, 253, 250, 0.78)),
    #ffffff;
}

.insight-hero-main {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(320px, 0.8fr);
  gap: 18px;
  align-items: start;
}

.insight-hero-copy h2 {
  margin: 10px 0 0;
  color: var(--ink-strong);
  font-size: 30px;
  line-height: 1.3;
}

.insight-hero-copy p {
  margin: 12px 0 0;
  max-width: 760px;
  color: var(--muted);
  line-height: 1.8;
}

.insight-hero-copy small {
  margin-top: 10px;
  color: var(--muted);
}

.platform-toolbar-card {
  padding: 16px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.76);
  box-shadow: var(--shadow-sm);
}

.platform-toolbar-card h3 {
  margin: 10px 0 0;
  color: var(--ink-strong);
  font-size: 20px;
}

.platform-toolbar-card p {
  margin: 10px 0 0;
  color: var(--muted);
  line-height: 1.7;
}

.platform-toolbar-card :deep(.el-segmented) {
  margin-top: 14px;
}

.insight-hero-metrics {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-top: 18px;
}

.insight-hero-metrics article {
  padding: 14px 16px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: var(--shadow-sm);
}

.insight-hero-metrics span,
.insight-hero-metrics small {
  display: block;
}

.insight-hero-metrics span {
  color: var(--muted);
  font-size: 12px;
}

.insight-hero-metrics b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 22px;
  overflow-wrap: anywhere;
}

.insight-hero-metrics small {
  margin-top: 10px;
  color: var(--muted);
  line-height: 1.6;
}

.potential-panel,
.market-scale-panel {
  margin-bottom: 16px;
}

.scale-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin: 12px 0;
}

.scale-grid article {
  padding: 12px;
  border-radius: 12px;
  background: rgba(248, 251, 255, 0.92);
}

.scale-grid span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.scale-grid b {
  display: block;
  margin-top: 8px;
  font-family: "IBM Plex Mono", monospace;
}

.signal-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.focus-banner {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  margin-bottom: 16px;
  border-color: rgba(34, 197, 94, 0.22);
}

.focus-banner h2 {
  margin: 8px 0 0;
  color: var(--ink-strong);
  font-size: 20px;
}

.focus-banner p {
  margin: 10px 0 0;
  max-width: 620px;
  color: var(--muted);
  line-height: 1.7;
}

.focus-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.constraint-diagnostics {
  margin-bottom: 16px;
}

.constraint-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.constraint-head h2 {
  margin: 8px 0 0;
  font-size: 20px;
  color: var(--ink-strong);
}

.constraint-head p {
  margin: 0;
  max-width: 560px;
  color: var(--muted);
  line-height: 1.7;
}

.constraint-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-top: 16px;
}

.constraint-card {
  padding: 14px;
  border: 1px solid var(--line);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.82);
}

.constraint-card h3 {
  margin: 0;
  color: var(--ink-strong);
  font-size: 16px;
}

.constraint-card ul,
.empty-reasons {
  margin: 12px 0 0;
  padding-left: 18px;
  color: var(--ink);
  line-height: 1.7;
}

.constraint-card p {
  margin: 12px 0 0;
  color: var(--muted);
  line-height: 1.7;
}

.filtered-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  margin-top: 14px;
}

.filtered-tags span {
  color: var(--muted);
  font-size: 12px;
}

.signal-card ul {
  margin: 12px 0 0;
  padding-left: 18px;
  line-height: 1.8;
  color: var(--ink-strong);
}

.signal-card p {
  margin: 12px 0 0;
  line-height: 1.75;
}

.signal-meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
  margin-top: 14px;
}

.signal-meta article {
  padding: 12px 14px;
  border: 1px solid var(--line);
  border-radius: 12px;
  background: rgba(248, 251, 255, 0.88);
}

.signal-meta span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.signal-meta b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 14px;
  line-height: 1.6;
}

.insight-tabs {
  margin-bottom: 18px;
  padding: 14px 16px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 251, 255, 0.98)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.insight-evidence {
  margin-bottom: 18px;
}

.evidence-head {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(320px, 0.9fr);
  gap: 16px;
  align-items: start;
  margin-bottom: 14px;
}

.evidence-head h2 {
  margin: 8px 0 0;
  color: var(--ink-strong);
  font-size: 24px;
}

.evidence-head p {
  margin: 10px 0 0;
  max-width: 760px;
  color: var(--muted);
  line-height: 1.7;
}

.evidence-summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.evidence-ribbon {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 14px;
}

.evidence-ribbon article {
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.evidence-ribbon span,
.evidence-ribbon small {
  display: block;
}

.evidence-ribbon span {
  color: var(--muted);
  font-size: 12px;
}

.evidence-ribbon b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 18px;
  line-height: 1.5;
  overflow-wrap: anywhere;
}

.evidence-ribbon small {
  margin-top: 10px;
  color: var(--muted);
  line-height: 1.6;
  overflow-wrap: anywhere;
}

.evidence-summary article {
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.evidence-summary span,
.evidence-summary small {
  display: block;
}

.evidence-summary span {
  color: var(--muted);
  font-size: 12px;
}

.evidence-summary b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 16px;
  line-height: 1.5;
}

.evidence-summary small {
  margin-top: 10px;
  color: var(--muted);
  line-height: 1.6;
}

.evidence-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.5fr) minmax(320px, 0.85fr);
  gap: 16px;
  align-items: start;
}

.candidate-section {
  margin-bottom: 16px;
}

.candidate-head {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(320px, 0.9fr);
  gap: 18px;
  align-items: start;
  margin-bottom: 18px;
}

.candidate-head h2 {
  margin: 8px 0 0;
  color: var(--ink-strong);
  font-size: 24px;
}

.candidate-head p {
  margin: 10px 0 0;
  max-width: 760px;
  color: var(--muted);
  line-height: 1.7;
}

.candidate-summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.candidate-summary article {
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.candidate-summary span,
.candidate-summary small {
  display: block;
}

.candidate-summary span {
  color: var(--muted);
  font-size: 12px;
}

.candidate-summary b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 18px;
  line-height: 1.5;
  overflow-wrap: anywhere;
}

.candidate-summary small {
  margin-top: 10px;
  color: var(--muted);
  line-height: 1.6;
  overflow-wrap: anywhere;
}

.candidate-body {
  display: grid;
  grid-template-columns: minmax(0, 1.6fr) minmax(320px, 0.9fr);
  gap: 16px;
  align-items: start;
}

.candidate-main,
.candidate-side {
  min-width: 0;
}

.candidate-main {
  display: grid;
  gap: 14px;
}

.candidate-main-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  padding: 16px 18px;
  border: 1px solid var(--line);
  border-radius: 16px;
  background:
    linear-gradient(180deg, rgba(252, 253, 255, 0.98), rgba(247, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.candidate-main-head span,
.candidate-main-head small {
  display: block;
}

.candidate-main-head span {
  color: var(--muted);
  font-size: 12px;
}

.candidate-main-head b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-size: 18px;
  line-height: 1.5;
}

.candidate-main-head small {
  max-width: 300px;
  color: var(--muted);
  line-height: 1.7;
  text-align: right;
  text-wrap: pretty;
}

.candidate-side {
  display: grid;
  gap: 14px;
}

.candidate-side-head {
  padding: 16px 18px;
  border: 1px solid var(--line);
  border-radius: 16px;
  background:
    linear-gradient(180deg, rgba(252, 253, 255, 0.98), rgba(247, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.candidate-side-head span,
.candidate-side-head small {
  display: block;
}

.candidate-side-head span {
  color: var(--muted);
  font-size: 12px;
}

.candidate-side-head b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-size: 18px;
  line-height: 1.5;
}

.candidate-side-head small {
  margin-top: 10px;
  color: var(--muted);
  line-height: 1.7;
  text-wrap: pretty;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.empty-state {
  margin-bottom: 16px;
  display: grid;
  align-items: center;
  min-height: 260px;
}

.empty-actions {
  display: grid;
  justify-items: center;
  gap: 12px;
}

@media (max-width: 1280px) {
  .insight-hero-metrics,
  .signal-grid,
  .evidence-ribbon,
  .candidate-summary,
  .card-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .candidate-body {
    grid-template-columns: 1fr;
  }

  .candidate-main-head,
  .candidate-side-head {
    display: grid;
  }

  .candidate-main-head small {
    max-width: none;
    text-align: left;
  }
}

@media (max-width: 1100px) {
  .insight-hero-main,
  .insight-hero-metrics,
  .focus-banner,
  .signal-grid,
  .constraint-head,
  .constraint-grid,
  .evidence-grid,
  .evidence-head,
  .evidence-ribbon,
  .evidence-summary,
  .candidate-head,
  .candidate-summary,
  .candidate-body {
    display: grid;
  }

  .candidate-main-head,
  .candidate-side-head {
    display: grid;
  }

  .candidate-main-head small {
    max-width: none;
    text-align: left;
  }

  .card-grid {
    grid-template-columns: 1fr;
  }
}
</style>
