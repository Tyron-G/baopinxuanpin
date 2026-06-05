<template>
  <section>
    <PageHero
      eyebrow="爆品机会"
      :title="detail?.insightCard?.categoryName ?? '爆品机会'"
      :description="detail?.insightCard?.recommendation ?? '加载机会点与进入窗口判断。'"
    >
      <template #actions>
        <div class="head-actions">
          <el-button :icon="Download" :loading="exporting" @click="exportReport">导出报告</el-button>
          <el-button type="primary" @click="openReport">查看报告</el-button>
          <el-button :icon="Back" @click="backToInsight">返回洞察</el-button>
        </div>
      </template>
    </PageHero>

    <WorkflowSummary current-stage="opportunity" :workflow="workflow" />

    <div v-if="loading" class="status-panel panel pad">
      <el-skeleton animated>
        <template #template>
          <el-skeleton-item variant="h3" style="width: 180px; height: 26px;" />
          <div class="status-metrics">
            <el-skeleton-item v-for="item in 4" :key="item" variant="rect" class="status-metric" />
          </div>
          <div class="status-grid">
            <el-skeleton-item variant="rect" class="status-block status-block--large" />
            <el-skeleton-item variant="rect" class="status-block" />
          </div>
        </template>
      </el-skeleton>
    </div>

    <div v-else-if="errorMessage" class="status-panel panel pad">
      <el-result icon="error" title="机会页加载失败" :sub-title="errorMessage">
        <template #extra>
          <el-button type="primary" @click="load">重新加载</el-button>
        </template>
      </el-result>
    </div>

    <div v-else-if="!hasContent" class="status-panel panel pad">
      <el-result icon="info" title="暂无机会分析" sub-title="当前类目还没有生成机会点，请先返回洞察页切换类目或稍后重试。">
        <template #extra>
          <el-button @click="backToInsight">返回洞察</el-button>
        </template>
      </el-result>
    </div>

    <template v-else-if="detail">
      <DecisionHeroPanel
        eyebrow="机会决策页"
        :title="`${detail.insightCard.categoryName} 当前值得怎么判断`"
        :description="detail.insightCard.recommendation || '当前机会分析延续自洞察页的平台上下文，报告导出也会保留这个视角。'"
        :context-items="opportunityContextItems"
        :overview-items="opportunityOverviewItems"
        :metric-items="opportunityMetricItems"
      />

      <DecisionSummary :summary="detail.decisionSummary" />

      <EntryDecisionPanel
        :decision="detail.decisionSummary.decision"
        :headline="detail.decisionSummary.headline"
        :entry-window="detail.competitionReport.entryWindow"
        :net-margin="detail.profitAnalysis.netMargin"
        :risk-hint="detail.supplyChainFeasibility.riskHint"
        :first-launch-platform="detail.platformPlaybook.firstLaunchPlatform"
        :validation-platform="detail.platformPlaybook.validationPlatform"
        :lifecycle-stage="bestPoint?.lifecycleStage"
        :entry-timing="bestPoint?.entryTiming"
      />

      <RiskSummaryPanel
        title="当前机会最该盯哪几个风险"
        :total-count="detail.decisionSummary.risks.length"
        :highest-level="primaryRisk?.level ?? 'low'"
        :primary-risk-title="primaryRisk?.title ?? '暂无显性风险'"
        :primary-risk-description="primaryRisk?.description ?? '当前未识别到高优先级风险。'"
        :suggested-attention="riskAttention"
        :summary="riskSummaryText"
      />

      <OpportunityReports
        :constraint-match="detail.constraintMatch"
        :competition-report="detail.competitionReport"
        :profit-analysis="detail.profitAnalysis"
        :supply-chain-feasibility="detail.supplyChainFeasibility"
        :platform-playbook="detail.platformPlaybook"
      />

      <section v-if="detail.entryBarrier" class="panel pad entry-barrier-panel">
        <span class="eyebrow">进入壁垒评估（PRD）</span>
        <h2>评论门槛 · 上榜周期 · CPC · 专利 · 供应链</h2>
        <p class="entry-barrier-summary">{{ detail.entryBarrier.summary }}</p>
        <div class="entry-barrier-grid">
          <article>
            <span>新品上榜周期</span>
            <b>{{ detail.entryBarrier.newProductListingCycle }}</b>
            <small>头部 SKU 进入榜单所需时间</small>
          </article>
          <article>
            <span>头部评论门槛</span>
            <b>{{ detail.entryBarrier.topCommentThreshold }}</b>
            <small>Top20 评论数均值样例</small>
          </article>
          <article>
            <span>CPC 壁垒</span>
            <b>{{ detail.entryBarrier.cpcBarrier }}</b>
            <small>相对类目均值</small>
          </article>
          <article>
            <span>专利壁垒</span>
            <b>{{ detail.entryBarrier.patentBarrier }}</b>
            <small>合规与侵权风险</small>
          </article>
          <article>
            <span>供应链壁垒</span>
            <b>{{ detail.entryBarrier.supplyChainBarrier }}</b>
            <small>MOQ 与履约风险</small>
          </article>
          <article>
            <span>综合壁垒</span>
            <b>{{ detail.entryBarrier.overallLevel }}</b>
            <small>四项综合判断</small>
          </article>
        </div>
      </section>

      <section v-if="detail.marketContext" class="panel pad market-context-panel">
        <span class="eyebrow">市场与投放上下文（PRD）</span>
        <h2>CPC · 物流 · 平台政策</h2>
        <p class="market-context-summary">{{ detail.marketContext.summary }}</p>
        <div class="market-context-grid">
          <article>
            <span>CPC 水平</span>
            <b>{{ detail.marketContext.cpcLevel }}</b>
            <small>{{ detail.marketContext.cpcVsCategory }}</small>
          </article>
          <article>
            <span>物流成本提示</span>
            <b>{{ detail.marketContext.logisticsCostHint }}</b>
            <small>重泡比 {{ detail.marketContext.weightVolumeRatio }}</small>
          </article>
          <article>
            <span>平台政策信号</span>
            <b>{{ detail.marketContext.platformPolicySignal }}</b>
            <small>流量红利：{{ detail.marketContext.trafficBonusChannel }}</small>
          </article>
        </div>
      </section>

      <OpportunityPrdAnalysis
        v-if="detail.supplyDemandGapModel"
        :gap-model="detail.supplyDemandGapModel"
        :price-bands="detail.priceBandDistribution"
        :lifecycle="detail.lifecycleInsight"
      />

      <section v-if="detail.externalDrivers?.length" class="panel pad external-drivers-panel">
        <span class="eyebrow">外部驱动因素（PRD）</span>
        <h2>政策 · 人口 · 技术 · 季节判断</h2>
        <div class="external-driver-grid">
          <article v-for="item in detail.externalDrivers" :key="item.driverType">
            <span>{{ item.driverType }}</span>
            <b>{{ item.signal }}</b>
            <small>{{ item.impact }}</small>
          </article>
        </div>
      </section>

      <section class="panel pad intel-panel">
        <span class="eyebrow">供应链与合规（PRD）</span>
        <div class="intel-grid">
          <article>
            <div class="intel-source-head">
              <h3>1688 供应链摸底</h3>
              <el-tag size="small" effect="plain">{{ detail.alibaba1688Intel.dataProvider }}</el-tag>
            </div>
            <small class="intel-sync">同步 {{ detail.alibaba1688Intel.syncedAt }} · 关键词 {{ detail.alibaba1688Intel.categoryKeyword }}</small>
            <p>{{ detail.alibaba1688Intel.summary }}</p>
            <small>{{ detail.alibaba1688Intel.priceRange }} · MOQ {{ detail.alibaba1688Intel.moq }} · {{ detail.alibaba1688Intel.factoryCapacity }}</small>
            <el-table :data="detail.alibaba1688Intel.offers" size="small" stripe class="intel-table">
              <el-table-column prop="title" label="报价 SKU" min-width="160" />
              <el-table-column prop="unitPrice" label="单价" width="80" />
              <el-table-column prop="moq" label="MOQ" width="90" />
              <el-table-column prop="factoryName" label="工厂" min-width="120" />
              <el-table-column prop="creditLevel" label="信用" width="70" />
            </el-table>
          </article>
          <article>
            <div class="intel-source-head">
              <h3>专利检索</h3>
              <el-tag size="small" effect="plain">{{ detail.patentIntel.dataProvider }}</el-tag>
            </div>
            <small class="intel-sync">同步 {{ detail.patentIntel.syncedAt }} · 检索式 {{ detail.patentIntel.searchQuery }}</small>
            <p>{{ detail.patentIntel.summary }}</p>
            <el-tag size="small">风险：{{ detail.patentIntel.riskLevel }}</el-tag>
            <ul>
              <li v-for="line in detail.patentIntel.highlights" :key="line">{{ line }}</li>
            </ul>
            <el-table :data="detail.patentIntel.records" size="small" stripe class="intel-table">
              <el-table-column prop="applicationNo" label="申请号" width="150" />
              <el-table-column prop="title" label="名称" min-width="140" />
              <el-table-column prop="status" label="状态" width="90" />
              <el-table-column prop="applicant" label="申请人" min-width="100" />
            </el-table>
          </article>
        </div>
        <h3>卖点建议（迭代1）</h3>
        <div class="selling-grid">
          <article v-for="(item, index) in detail.sellingPoints" :key="index">
            <b>{{ item.suggestedPriceBand }}</b>
            <p>{{ item.sellingPoint }}</p>
            <small>{{ item.differentiationDirection }}</small>
          </article>
        </div>
      </section>

      <section v-if="detail.relatedCompetitors.length" class="panel pad competitor-panel">
        <div class="section-head">
          <div>
            <span class="eyebrow">关联竞品</span>
            <h2>当前类目的跟踪对象</h2>
          </div>
          <div class="section-actions">
            <p>直接看同赛道里谁在上新、哪些差评主题仍未被解决，再决定机会点优先级。</p>
            <el-segmented
              v-model="competitorSortBy"
              :options="competitorSortOptions"
              size="default"
            />
          </div>
        </div>

        <div class="competitor-grid">
          <article v-for="shop in displayCompetitors" :key="`${shop.shopName}-${shop.addedAt}`" class="competitor-card">
            <header>
              <div>
                <span class="eyebrow">{{ shop.platform }}</span>
                <h3>{{ shop.shopName }}</h3>
              </div>
              <el-tag size="small" type="warning" effect="plain">{{ shop.hitProductCount }} 个爆品</el-tag>
            </header>
            <p class="competitor-summary">{{ shop.latestHit }}</p>
            <div class="priority-reason">
              <span>优先关注理由</span>
              <b>{{ competitorPriorityReason(shop) }}</b>
            </div>
            <div class="tag-row">
              <el-tag
                v-for="tag in shop.opportunityTags"
                :key="`${shop.shopName}-${tag}`"
                size="small"
                effect="plain"
              >
                {{ tag }}
              </el-tag>
            </div>
            <div class="complaint-row">
              <el-tag
                v-for="topic in shop.complaintTopics"
                :key="`${shop.shopName}-${topic}`"
                size="small"
                type="danger"
                effect="plain"
              >
                {{ topic }}
              </el-tag>
            </div>
            <div class="link-row">
              <el-button link @click="openCompetitor(shop.focusCategory, shop.platform, shop.sourceSignalType)">查看竞品页</el-button>
            </div>
          </article>
        </div>

        <div class="competitor-summary-card">
          <CompetitorSummaryPanel
            title="当前类目的竞品摘要"
            :tracked-shop-count="detail.competitorSummary.trackedShopCount"
            :covered-platforms="detail.competitorSummary.coveredPlatforms"
            :total-hit-product-count="detail.competitorSummary.totalHitProductCount"
            :strongest-signal="detail.competitorSummary.strongestSignal"
            :summary="detail.competitorSummary.summary"
            :common-complaint-topics="detail.competitorSummary.commonComplaintTopics"
          />
        </div>

        <div class="comparison-table-wrap">
          <div class="table-head">
            <div>
              <span class="table-kicker">竞品对比视图</span>
              <b>把重点对象压到一张表里看</b>
            </div>
            <small>优先比较平台一致性、爆品样本、最近上新与高频差评。</small>
          </div>
          <div class="table-ribbon">
            <article>
              <span>当前排序</span>
              <b>{{ competitorSortLabel }}</b>
              <small>{{ competitorSortNarrative }}</small>
            </article>
            <article>
              <span>平台一致对象</span>
              <b>{{ platformFitCompetitorCount }}</b>
              <small>{{ platformView }} 视角下优先对象数量</small>
            </article>
            <article>
              <span>爆品最多对象</span>
              <b>{{ topHitCompetitor?.shopName ?? '暂无对象' }}</b>
              <small>{{ topHitCompetitor ? `${topHitCompetitor.hitProductCount} 个爆品样本` : '等待竞品数据' }}</small>
            </article>
          </div>
          <table class="comparison-table">
            <thead>
              <tr>
                <th>竞品</th>
                <th>平台</th>
                <th>爆品数</th>
                <th>最近上新</th>
                <th>高频差评</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="shop in displayCompetitors" :key="`table-${shop.shopName}-${shop.addedAt}`">
                <td class="entity-cell">
                  <b>{{ shop.shopName }}</b>
                  <small>{{ competitorPriorityReason(shop) }}</small>
                </td>
                <td><el-tag size="small" effect="plain" type="info">{{ shop.platform }}</el-tag></td>
                <td><span class="metric-pill">{{ shop.hitProductCount }}</span></td>
                <td class="narrative-cell">{{ shop.recentLaunch }}</td>
                <td>
                  <div class="table-tag-list">
                    <el-tag
                      v-for="topic in shop.complaintTopics"
                      :key="`table-topic-${shop.shopName}-${topic}`"
                      size="small"
                      effect="plain"
                      type="danger"
                    >
                      {{ topic }}
                    </el-tag>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <CompetitorTimelinePanel
        v-if="competitorTimelines.length"
        :items="competitorTimelines"
        title="当前类目的竞品最近 4 周表现"
        description="结合当前平台视角，观察关联竞品的热度、销量和上新节奏是否在持续走强。"
      />

      <section v-if="detail.differentiationAdvice.length" class="panel pad advice-panel">
        <div class="section-head">
          <div>
            <span class="eyebrow">差异化建议</span>
            <h2>怎么切入更有胜率</h2>
          </div>
          <p>系统基于竞品差评主题、品牌约束和当前机会判断，给出首轮更值得验证的切入方向。</p>
        </div>

        <div class="content-ribbon">
          <article>
            <span>建议数量</span>
            <b>{{ detail.differentiationAdvice.length }}</b>
            <small>优先从最能快速验证的平台切入开始</small>
          </article>
          <article>
            <span>当前主平台</span>
            <b>{{ platformView }}</b>
            <small>建议保持与当前机会判断一致的验证语境</small>
          </article>
        </div>

        <div class="advice-list">
          <article v-for="(item, index) in detail.differentiationAdvice" :key="`${index}-${item}`" class="advice-card">
            <b>建议 {{ index + 1 }}</b>
            <p>{{ item }}</p>
          </article>
        </div>
      </section>

      <ActionSummaryPanel
        title="这轮动作当前推进到哪了"
        :total-count="detail.nextActions.length"
        :completed-count="completedActionCount"
        :in-progress-count="inProgressActionCount"
        :pending-count="pendingActionCount"
        :focus-action-title="focusAction?.title ?? '暂无动作'"
        :focus-action-status="focusAction?.status ?? '-'"
        :latest-updated-at="focusAction?.updatedAt ?? '-'"
        :summary="actionSummaryText"
      />

      <NextActionsPanel :actions="detail.nextActions" @update-status="updateActionStatus" />

      <section class="panel pad point-panel">
        <div class="section-head">
          <div>
            <span class="eyebrow">机会点候选池</span>
            <h2>按不同判断视角重看当前机会</h2>
          </div>
          <div class="section-actions">
            <p>{{ pointViewSummary }}</p>
            <el-segmented
              v-model="pointViewMode"
              :options="pointViewOptions"
              size="default"
            />
          </div>
        </div>

        <div class="content-ribbon">
          <article>
            <span>候选机会点</span>
            <b>{{ displayPoints.length }}</b>
            <small>当前列表会随视角切换重排</small>
          </article>
          <article>
            <span>当前视角</span>
            <b>{{ pointViewLabel }}</b>
            <small>{{ pointViewSummary }}</small>
          </article>
        </div>

        <div class="point-context-grid">
          <article class="point-context-card">
            <span>当前优先机会</span>
            <b>{{ displayPoints[0]?.scenarioText ?? '-' }}</b>
            <p>{{ displayPoints[0]?.targetCrowd ?? '暂无候选机会点' }}</p>
          </article>
          <article class="point-context-card">
            <span>最佳入场时机</span>
            <b>{{ displayPoints[0]?.entryTiming ?? '-' }}</b>
            <p>{{ displayPoints[0]?.lifecycleStage ?? '暂无生命周期判断' }}</p>
          </article>
          <article class="point-context-card">
            <span>最高利润弹性</span>
            <b>{{ profitLeader?.profitElasticity ?? '-' }}</b>
            <p>{{ profitLeader?.scenarioText ?? '暂无利润弹性对比' }}</p>
          </article>
          <article class="point-context-card">
            <span>最低竞争阻力</span>
            <b>{{ resistanceLeader ? `${resistanceLeader.competitionResistance}` : '-' }}</b>
            <p>{{ resistanceLeader?.scenarioText ?? '暂无竞争阻力对比' }}</p>
          </article>
        </div>
      </section>

      <section class="panel pad analytics-panel">
        <div class="section-head">
          <div>
            <span class="eyebrow">分析工作台</span>
            <h2>把机会点、舆情和人群场景放在一张分析面板里看</h2>
          </div>
          <p>先看机会点落在哪个引力区间，再结合舆情倾向和场景人群判断当前应先验证哪条内容表达。</p>
        </div>

        <div class="analytics-ribbon">
          <article>
            <span>舆情正向词</span>
            <b>{{ positiveSentimentCount }}</b>
            <small>当前词云中偏正向表达的词条数量</small>
          </article>
          <article>
            <span>舆情负向词</span>
            <b>{{ negativeSentimentCount }}</b>
            <small>适合转成差异化和售后规避的重点问题</small>
          </article>
          <article>
            <span>人群场景</span>
            <b>{{ detail.crowdScenes.length }}</b>
            <small>当前类目可优先承接的目标人群与触发场景</small>
          </article>
        </div>

        <div class="grid-2 analytics-grid">
          <OpportunityScatter :rows="displayPoints" />
          <div class="stack">
            <SentimentCloud :terms="detail.sentimentTerms" />
            <CrowdScene :scenes="detail.crowdScenes" />
          </div>
        </div>

        <CompetitionQuadrant
          v-if="detail.competitionQuadrant"
          :report="detail.competitionQuadrant"
          class="quadrant-panel"
        />
      </section>

      <section class="points-shell">
        <div class="section-head points-head">
          <div>
            <span class="eyebrow">机会点明细</span>
            <h2>逐条复核当前候选机会</h2>
          </div>
          <p>列表已跟随当前判断视角重排，适合继续确认具体人群、场景和差异化打法。</p>
        </div>
        <div class="points">
        <OpportunityPoint v-for="point in displayPoints" :key="point.id" :point="point" />
        </div>
      </section>
    </template>
  </section>
</template>

<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Back, Download } from '@element-plus/icons-vue'
import { api } from '@/api'
import { getBrandId, setBrandId } from '@/composables/useBrandContext'
import { getApiErrorMessage } from '@/lib/apiError'
import { ElMessage } from 'element-plus'
import type {
  CompetitorShop,
  CompetitorSummary,
  CompetitorTimeline,
  OpportunityDetail,
  PlatformPlaybook,
  SentimentTerm,
  WorkflowProgress
} from '@/types'
import ActionSummaryPanel from '@/components/common/ActionSummaryPanel.vue'
import CompetitorSummaryPanel from '@/components/common/CompetitorSummaryPanel.vue'
import DecisionHeroPanel from '@/components/common/DecisionHeroPanel.vue'
import DecisionSummary from '@/components/common/DecisionSummary.vue'
import EntryDecisionPanel from '@/components/common/EntryDecisionPanel.vue'
import PageHero from '@/components/common/PageHero.vue'
import RiskSummaryPanel from '@/components/common/RiskSummaryPanel.vue'
import CompetitorTimelinePanel from '@/components/common/CompetitorTimelinePanel.vue'
import WorkflowSummary from '@/components/common/WorkflowSummary.vue'
import SentimentCloud from '@/components/opportunity/SentimentCloud.vue'
import CrowdScene from '@/components/opportunity/CrowdScene.vue'
import NextActionsPanel from '@/components/opportunity/NextActionsPanel.vue'
import OpportunityPoint from '@/components/opportunity/OpportunityPoint.vue'
import OpportunityReports from '@/components/opportunity/OpportunityReports.vue'
import OpportunityPrdAnalysis from '@/components/opportunity/OpportunityPrdAnalysis.vue'
import { isLowerResistance, resistanceMagnitude } from '@/lib/opportunityMetrics'

const OpportunityScatter = defineAsyncComponent(() => import('@/components/opportunity/OpportunityScatter.vue'))
const CompetitionQuadrant = defineAsyncComponent(() => import('@/components/opportunity/CompetitionQuadrant.vue'))

const props = defineProps<{ cardId: string }>()
const route = useRoute()
const router = useRouter()
const detail = ref<OpportunityDetail>()
const competitorTimelines = ref<CompetitorTimeline[]>([])
const loading = ref(false)
const exporting = ref(false)
const errorMessage = ref('')
const workflow = ref<WorkflowProgress>()
const competitorSortBy = ref<'platform-fit' | 'hit-count' | 'complaint-density' | 'source-signal'>('platform-fit')
const competitorSortOptions = [
  { label: '平台一致', value: 'platform-fit' },
  { label: '爆品样本', value: 'hit-count' },
  { label: '差评痛点', value: 'complaint-density' },
  { label: '来源信号', value: 'source-signal' }
] as const
const pointViewMode = ref<'balanced' | 'timing' | 'profit' | 'scenario'>('balanced')
const pointViewOptions = [
  { label: '综合判断', value: 'balanced' },
  { label: '入场时机', value: 'timing' },
  { label: '利润弹性', value: 'profit' },
  { label: '人群场景', value: 'scenario' }
] as const
const platformView = computed(() => {
  const raw = route.query.platform
  return typeof raw === 'string' && raw.trim() ? raw : '全平台'
})
const brandId = computed(() => {
  const queryId = Number(route.query.brandId)
  if (Number.isFinite(queryId) && queryId > 0) {
    setBrandId(queryId)
    return queryId
  }
  return getBrandId()
})

function pickValidationPlatform(platform: string) {
  if (platform === '抖音') return '小红书'
  if (platform === '小红书') return '抖音'
  return '抖音'
}

function buildFallbackCompetitorSummary(shops: CompetitorShop[]): CompetitorSummary {
  const platforms = [...new Set(shops.map((shop) => shop.platform).filter(Boolean))]
  const commonComplaintTopics = Object.entries(
    shops.reduce<Record<string, number>>((accumulator, shop) => {
      shop.complaintTopics.forEach((topic) => {
        accumulator[topic] = (accumulator[topic] ?? 0) + 1
      })
      return accumulator
    }, {})
  )
    .sort((left, right) => right[1] - left[1])
    .slice(0, 4)
    .map(([topic]) => topic)

  return {
    trackedShopCount: shops.length,
    coveredPlatforms: platforms.join(' / ') || platformView.value,
    totalHitProductCount: shops.reduce((total, shop) => total + shop.hitProductCount, 0),
    strongestSignal: shops[0]?.sourceSignalType ?? '当前暂无竞品跟踪信号',
    summary: shops.length
      ? `当前已跟踪 ${shops.length} 个竞品对象，可先按平台一致性与高频差评主题判断谁更值得优先复核。`
      : `当前类目还没有挂载竞品样本，建议先从 ${platformView.value} 视角补充 2-3 个跟踪对象。`,
    commonComplaintTopics
  }
}

function buildFallbackPlatformPlaybook(detailData: OpportunityDetail): PlatformPlaybook {
  const launchPlatform = platformView.value === '全平台' ? '天猫' : platformView.value
  const validationPlatform = pickValidationPlatform(launchPlatform)
  const topPoint = detailData.points?.[0]

  return {
    firstLaunchPlatform: launchPlatform,
    validationPlatform,
    conversionPlatform: launchPlatform === '抖音' || launchPlatform === '小红书' ? '天猫' : launchPlatform,
    executionHints: [
      topPoint?.scenarioText ? `先围绕「${topPoint.scenarioText}」组织首轮验证素材。` : '先围绕当前类目的核心使用场景组织首轮验证素材。',
      '用小样投放先验证点击、收藏与评论反馈，再决定是否放大备货。',
      '把差评高频问题提前写进卖点与售后承诺，降低首轮试错损耗。'
    ],
    summary: `建议先在 ${launchPlatform} 启动首轮验证，再用 ${validationPlatform} 复核反馈，最后把转化承接压回更稳定的成交阵地。`
  }
}

function buildFallbackDifferentiationAdvice(detailData: OpportunityDetail, playbook: PlatformPlaybook) {
  const topPoint = detailData.points?.[0]
  const topNegativeTerm = (detailData.sentimentTerms ?? [])
    .filter((term: SentimentTerm) => term.sentiment === 'negative')
    .sort((left, right) => right.value - left.value)[0]

  return [
    topPoint?.differentiation
      ? `首轮优先验证「${topPoint.differentiation}」这条差异化表达，避免同时铺开过多卖点。`
      : '首轮优先聚焦单一核心卖点，避免验证阶段信息过载。',
    topNegativeTerm
      ? `把「${topNegativeTerm.name}」对应问题前置写进卖点与售后承诺，减少用户顾虑。`
      : '把高频顾虑提前转成卖点说明和售后承诺，减少冷启动阶段的阻力。',
    `先在 ${playbook.firstLaunchPlatform} 跑小样，再用 ${playbook.validationPlatform} 复核内容反馈与转化承接。`
  ]
}

function normalizeOpportunityDetail(detailData: OpportunityDetail): OpportunityDetail {
  const relatedCompetitors = Array.isArray((detailData as Partial<OpportunityDetail>).relatedCompetitors)
    ? (detailData as Partial<OpportunityDetail>).relatedCompetitors as CompetitorShop[]
    : []
  const competitorSummary =
    (detailData as Partial<OpportunityDetail>).competitorSummary ?? buildFallbackCompetitorSummary(relatedCompetitors)
  const platformPlaybook =
    (detailData as Partial<OpportunityDetail>).platformPlaybook ?? buildFallbackPlatformPlaybook(detailData)
  const differentiationAdvice =
    Array.isArray((detailData as Partial<OpportunityDetail>).differentiationAdvice) &&
    ((detailData as Partial<OpportunityDetail>).differentiationAdvice?.length ?? 0) > 0
      ? (detailData as Partial<OpportunityDetail>).differentiationAdvice as string[]
      : buildFallbackDifferentiationAdvice(detailData, platformPlaybook)

  return {
    ...detailData,
    points: Array.isArray(detailData.points) ? detailData.points : [],
    nextActions: Array.isArray(detailData.nextActions) ? detailData.nextActions : [],
    sentimentTerms: Array.isArray(detailData.sentimentTerms) ? detailData.sentimentTerms : [],
    crowdScenes: Array.isArray(detailData.crowdScenes) ? detailData.crowdScenes : [],
    relatedCompetitors,
    competitorSummary,
    platformPlaybook,
    differentiationAdvice
  }
}

const hasContent = computed(() => Boolean(detail.value?.points.length))
const bestPoint = computed(() => detail.value?.points[0])
const primaryRisk = computed(() => {
  const risks = [...(detail.value?.decisionSummary.risks ?? [])]
  return risks.sort((left, right) => riskLevelRank(left.level) - riskLevelRank(right.level))[0]
})
const completedActionCount = computed(() => (detail.value?.nextActions ?? []).filter(item => item.status.includes('完成')).length)
const inProgressActionCount = computed(() => (detail.value?.nextActions ?? []).filter(item => item.status.includes('进行')).length)
const pendingActionCount = computed(() => (detail.value?.nextActions ?? []).length - completedActionCount.value - inProgressActionCount.value)
const focusAction = computed(() => {
  const actions = [...(detail.value?.nextActions ?? [])]
  return actions.sort((left, right) => actionPriorityRank(left.priority, left.status) - actionPriorityRank(right.priority, right.status))[0]
})
const riskAttention = computed(() => {
  const level = primaryRisk.value?.level?.trim().toLowerCase?.() ?? 'low'
  if (level === 'high') {
    return '优先处理预算、竞争壁垒或关键履约问题，再扩大验证投入。'
  }
  if (level === 'medium') {
    return '保持小样验证节奏，同时补齐场景、素材或供应链稳定性验证。'
  }
  return '持续跟踪 CPC、转化率和售后反馈，防止低风险项在放量时放大。'
})
const riskSummaryText = computed(() => {
  const count = detail.value?.decisionSummary.risks.length ?? 0
  const title = primaryRisk.value?.title ?? '暂无显性风险'
  const level = primaryRisk.value?.level ?? 'low'
  return `当前共识别 ${count} 条主要风险，其中最高等级为「${level}」，首要风险是「${title}」。`
})
const actionSummaryText = computed(() => {
  const total = detail.value?.nextActions.length ?? 0
  const focus = focusAction.value
  return `当前共 ${total} 个推进动作，已完成 ${completedActionCount.value} 个，进行中 ${inProgressActionCount.value} 个，待推进 ${pendingActionCount.value} 个。当前主焦点为「${focus?.title ?? '暂无动作'}」，状态为「${focus?.status ?? '-'}」。`
})
const opportunityContextItems = computed(() => {
  if (!detail.value) return []
  return [
    { label: '平台视角', value: platformView.value, hint: '延续自洞察页的平台判断上下文' },
    { label: '关联竞品', value: detail.value.competitorSummary.coveredPlatforms, hint: `${detail.value.competitorSummary.trackedShopCount} 个跟踪对象` },
    { label: '爆品样本', value: detail.value.competitorSummary.totalHitProductCount, hint: detail.value.competitorSummary.strongestSignal },
    { label: '当前主焦点', value: focusAction.value?.status ?? '-', hint: focusAction.value?.title ?? '暂无动作' }
  ]
})
const opportunityOverviewItems = computed(() => {
  if (!detail.value) return []
  return [
    { label: '能不能进', value: detail.value.decisionSummary.decision, hint: detail.value.decisionSummary.headline },
    { label: '风险高不高', value: primaryRisk.value?.level ?? 'low', hint: primaryRisk.value?.title ?? '暂无显性风险' },
    { label: '动作推进到哪', value: focusAction.value?.status ?? '-', hint: focusAction.value?.title ?? '暂无动作' },
    { label: '先去哪里起量', value: detail.value.platformPlaybook.firstLaunchPlatform, hint: `${detail.value.platformPlaybook.validationPlatform} 验证反馈` }
  ]
})
const opportunityMetricItems = computed(() => {
  if (!detail.value) return []
  return [
    { label: '市场规模', value: detail.value.insightCard.marketSize },
    { label: '市场增速', value: detail.value.insightCard.marketGrowth },
    { label: '竞争难度', value: detail.value.insightCard.competitionLevel },
    { label: '启动资金', value: detail.value.insightCard.estimatedStartupCost }
  ]
})
const displayPoints = computed(() => {
  const points = [...(detail.value?.points ?? [])]
  const score = (point: OpportunityDetail['points'][number]) => {
    switch (pointViewMode.value) {
      case 'timing':
        return point.opportunityScore * 10 + lifecyclePriority(point.lifecycleStage) * 20 + entryTimingPriority(point.entryTiming)
      case 'profit':
        return point.profitElasticity * 100 + point.opportunityScore - resistanceMagnitude(point.competitionResistance)
      case 'scenario':
        return scenarioPriority(point.scenarioText, point.targetCrowd) * 100 + point.opportunityGravity * 2 + point.opportunityScore
      case 'balanced':
      default:
        return point.opportunityScore * 100 + point.profitElasticity * 2 - resistanceMagnitude(point.competitionResistance) + point.opportunityGravity
    }
  }
  return points.sort((left, right) => {
    const scoreGap = score(right) - score(left)
    if (scoreGap !== 0) {
      return scoreGap
    }
    return right.opportunityScore - left.opportunityScore
  })
})
const positiveSentimentCount = computed(() => (detail.value?.sentimentTerms ?? []).filter(item => item.sentiment === 'positive').length)
const negativeSentimentCount = computed(() => (detail.value?.sentimentTerms ?? []).filter(item => item.sentiment === 'negative').length)
const profitLeader = computed(() => {
  return [...(detail.value?.points ?? [])].sort((left, right) => right.profitElasticity - left.profitElasticity)[0]
})
const resistanceLeader = computed(() => {
  return [...(detail.value?.points ?? [])].sort((left, right) =>
    isLowerResistance(left.competitionResistance, right.competitionResistance) ? -1 : 1
  )[0]
})
const pointViewSummary = computed(() => {
  const lead = displayPoints.value[0]
  if (!lead) {
    return '当前还没有可排序的机会点。'
  }
  if (pointViewMode.value === 'timing') {
    return `当前优先看“谁更适合现在进场”，系统会优先提升处于${lead.lifecycleStage}、且入场时机为${lead.entryTiming}的候选点。`
  }
  if (pointViewMode.value === 'profit') {
    return `当前优先看利润弹性，系统会把更容易跑出毛利空间、且综合分仍然健康的机会点排在前面。`
  }
  if (pointViewMode.value === 'scenario') {
    return `当前优先看人群与场景表达，系统会优先提升更贴近内容传播和场景切入的候选机会点。`
  }
  return `当前按综合判断排序，优先兼顾机会评分、利润弹性、竞争阻力和机会引力。`
})
const pointViewLabel = computed(() => {
  if (pointViewMode.value === 'timing') return '入场时机'
  if (pointViewMode.value === 'profit') return '利润弹性'
  if (pointViewMode.value === 'scenario') return '人群场景'
  return '综合判断'
})
const competitorSortLabel = computed(() => competitorSortOptions.find((item) => item.value === competitorSortBy.value)?.label ?? '平台一致')
const competitorSortNarrative = computed(() => {
  if (competitorSortBy.value === 'hit-count') return '优先把爆品样本更丰富的对象压到前排，便于看成熟打法。'
  if (competitorSortBy.value === 'complaint-density') return '优先看差评痛点更密集的对象，便于寻找差异化切口。'
  if (competitorSortBy.value === 'source-signal') return '优先看来自当前信号链路的对象，便于保持洞察来源一致。'
  return `优先看与当前 ${platformView.value} 视角一致的对象，减少平台语境偏差。`
})
const displayCompetitors = computed(() => {
  const shops = [...(detail.value?.relatedCompetitors ?? [])]
  const currentPlatform = platformView.value
  const score = (shop: CompetitorShop) => {
    switch (competitorSortBy.value) {
      case 'hit-count':
        return shop.hitProductCount * 100 + (shop.platform === currentPlatform ? 10 : 0)
      case 'complaint-density':
        return shop.complaintTopics.length * 100 + shop.hitProductCount
      case 'source-signal':
        return (shop.sourceSignalType ? 1000 : 0) + shop.hitProductCount * 10 + shop.complaintTopics.length
      case 'platform-fit':
      default:
        return (shop.platform === currentPlatform ? 1000 : 0) + shop.hitProductCount * 10 + shop.complaintTopics.length
    }
  }
  return shops.sort((left, right) => {
    const scoreGap = score(right) - score(left)
    if (scoreGap !== 0) {
      return scoreGap
    }
    return left.shopName.localeCompare(right.shopName, 'zh-CN')
  })
})
const platformFitCompetitorCount = computed(() => displayCompetitors.value.filter((shop) => shop.platform === platformView.value).length)
const topHitCompetitor = computed(() => [...displayCompetitors.value].sort((left, right) => right.hitProductCount - left.hitProductCount)[0])

async function load() {
  loading.value = true
  errorMessage.value = ''
  try {
    const [detailData, workflowData] = await Promise.all([
      api.getOpportunity(Number(props.cardId), brandId.value, platformView.value),
      api.getWorkflow(brandId.value)
    ])
    detail.value = normalizeOpportunityDetail(detailData)
    workflow.value = workflowData
    try {
      competitorTimelines.value = await api.getCompetitorTimelines(
        brandId.value,
        detailData.insightCard.categoryName,
        platformView.value
      )
    } catch (timelineError) {
      competitorTimelines.value = []
      console.warn('2026-06-04 Opportunity timeline fallback:', timelineError)
    }
  } catch (error) {
    detail.value = undefined
    competitorTimelines.value = []
    workflow.value = undefined
    errorMessage.value = getApiErrorMessage(error, '请检查后端服务是否已启动，或稍后重试。')
  } finally {
    loading.value = false
  }
}

function backToInsight() {
  router.push({ path: '/insight', query: { brandId: brandId.value, platform: platformView.value } })
}

function openReport() {
  router.push({ path: `/report/${props.cardId}`, query: { brandId: brandId.value, platform: platformView.value } })
}

function openCompetitor(category: string, platform: string, source?: string | null) {
  router.push({
    path: '/competitor',
    query: {
      brandId: brandId.value,
      cardId: props.cardId,
      category,
      platform,
      insightPlatform: platformView.value,
      ...(source ? { source } : {})
    }
  })
}

function competitorPriorityReason(shop: CompetitorShop) {
  const reasons = []
  if (shop.platform === platformView.value) {
    reasons.push(`与当前 ${platformView.value} 视角一致`)
  }
  if (shop.hitProductCount >= 3) {
    reasons.push('爆品样本更多')
  }
  if (shop.complaintTopics.length >= 3) {
    reasons.push('差评痛点更集中')
  }
  if (shop.sourceSignalType) {
    reasons.push(`来源信号为${shop.sourceSignalType}`)
  }
  if (!reasons.length) {
    reasons.push('覆盖当前类目的基础跟踪对象')
  }
  return reasons.slice(0, 2).join('，')
}

function lifecyclePriority(value: string) {
  if (value.includes('成长期')) return 4
  if (value.includes('导入期')) return 3
  if (value.includes('成熟期')) return 2
  return 1
}

function entryTimingPriority(value: string) {
  if (value.includes('最佳')) return 40
  if (value.includes('优先')) return 30
  if (value.includes('可')) return 20
  if (value.includes('验证')) return 15
  return 10
}

function scenarioPriority(scene: string, crowd: string) {
  const text = `${scene} ${crowd}`
  const keywords = ['露营', '独自在家', '办公室', '看护', '礼物', '多宠']
  return keywords.reduce((total, keyword) => total + (text.includes(keyword) ? 1 : 0), 0)
}

function riskLevelRank(level: string) {
  const value = level?.trim().toLowerCase?.() ?? 'low'
  if (value === 'high') return 0
  if (value === 'medium') return 1
  return 2
}

function actionPriorityRank(priority: string, status: string) {
  const priorityRank = priority === 'P0' ? 0 : priority === 'P1' ? 1 : priority === 'P2' ? 2 : 3
  let statusRank = 3
  if (status?.includes('进行')) {
    statusRank = 0
  } else if (status?.includes('待')) {
    statusRank = 1
  } else if (status?.includes('完成')) {
    statusRank = 2
  }
  return priorityRank * 10 + statusRank
}

async function exportReport() {
  exporting.value = true
  try {
    const report = await api.exportReport(Number(props.cardId), brandId.value, platformView.value)
    const blob = new Blob([report.content], { type: 'text/markdown;charset=utf-8' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = report.fileName
    link.click()
    URL.revokeObjectURL(url)
    ElMessage.success('选品报告已导出')
  } catch (error) {
    ElMessage.error(getApiErrorMessage(error, '导出失败，请稍后重试'))
  } finally {
    exporting.value = false
  }
}

async function updateActionStatus(payload: { title: string; status: string }) {
  try {
    await api.updateOpportunityAction(Number(props.cardId), payload.title, { status: payload.status })
    await load()
    ElMessage.success(`动作已更新为「${payload.status}」`)
  } catch (error) {
    ElMessage.error(getApiErrorMessage(error, '动作状态更新失败，请稍后重试'))
  }
}

onMounted(load)
watch(() => [props.cardId, brandId.value, platformView.value], load)
</script>

<style scoped>
.status-panel {
  min-height: 320px;
  display: grid;
  align-items: center;
}

.head-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.status-metrics {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-top: 20px;
}

.status-metric,
.status-block {
  width: 100%;
  border-radius: 8px;
}

.status-metric {
  height: 92px;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.status-block {
  height: 240px;
}

.status-block--large {
  height: 360px;
}

.grid-2 {
  align-items: start;
  margin-top: 16px;
}

.market-context-panel {
  margin-bottom: 16px;
}

.market-context-summary,
.entry-barrier-summary {
  margin: 8px 0 14px;
  color: var(--muted);
}

.entry-barrier-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.entry-barrier-grid article {
  padding: 12px;
  border-radius: 12px;
  background: rgba(248, 251, 255, 0.9);
}

.entry-barrier-grid span,
.entry-barrier-grid small {
  display: block;
  color: var(--muted);
}

.entry-barrier-grid b {
  display: block;
  margin: 6px 0;
}

.quadrant-panel {
  margin-top: 16px;
}

.market-context-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.market-context-grid article {
  padding: 12px;
  border-radius: 12px;
  background: rgba(248, 251, 255, 0.9);
}

.market-context-grid span,
.market-context-grid small {
  display: block;
  color: var(--muted);
}

.market-context-grid b {
  display: block;
  margin: 6px 0;
}

.external-driver-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 12px;
}

.external-driver-grid article {
  padding: 12px 14px;
  border-radius: 12px;
  background: rgba(248, 251, 255, 0.95);
  border: 1px solid rgba(15, 23, 42, 0.06);
}

.external-driver-grid span {
  display: block;
  color: var(--muted-soft);
  font-size: 11px;
}

.external-driver-grid b {
  display: block;
  margin: 6px 0;
}

.external-driver-grid small {
  color: var(--text-secondary);
  line-height: 1.5;
}

.intel-panel .intel-grid,
.intel-panel .selling-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin: 12px 0;
}

.intel-panel article {
  padding: 12px;
  border-radius: 12px;
  background: rgba(248, 251, 255, 0.9);
}

.intel-source-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.intel-sync {
  display: block;
  margin: 6px 0 10px;
  color: var(--muted);
}

.intel-table {
  margin-top: 10px;
}

.selling-grid {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.competitor-panel {
  margin-top: 16px;
}

.advice-panel {
  margin-top: 16px;
}

.point-panel {
  margin-top: 16px;
}

.analytics-panel {
  margin-top: 16px;
}

.content-ribbon {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.content-ribbon article {
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.content-ribbon article,
.analytics-ribbon article,
.table-ribbon article,
.point-context-card,
.advice-card {
  min-height: 100%;
}

.content-ribbon span,
.content-ribbon small {
  display: block;
}

.content-ribbon span {
  color: var(--muted);
  font-size: 12px;
}

.content-ribbon b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 16px;
  line-height: 1.5;
}

.content-ribbon small {
  margin-top: 10px;
  color: var(--muted);
  line-height: 1.6;
}

.analytics-ribbon {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.analytics-ribbon article {
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.analytics-ribbon span,
.analytics-ribbon small {
  display: block;
}

.analytics-ribbon span {
  color: var(--muted);
  font-size: 12px;
}

.analytics-ribbon b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 18px;
  line-height: 1.5;
}

.analytics-ribbon small {
  margin-top: 10px;
  color: var(--muted);
  line-height: 1.6;
}

.analytics-grid {
  margin-top: 16px;
}

.section-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.section-actions {
  display: grid;
  justify-items: end;
  gap: 12px;
}

.section-head h2 {
  margin: 8px 0 0;
  color: var(--ink-strong);
  font-size: 18px;
}

.section-head p {
  margin: 0;
  max-width: 560px;
  color: var(--muted);
  line-height: 1.7;
  text-align: right;
}

.competitor-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-top: 16px;
}

.competitor-summary-card {
  margin-top: 14px;
  padding: 14px;
  border: 1px solid var(--line);
  border-radius: 16px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.99), rgba(247, 250, 255, 0.98)),
    #ffffff;
  box-shadow: 0 16px 28px rgba(15, 23, 42, 0.05);
}

.summary-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.summary-metrics span,
.competitor-summary-card small {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.summary-metrics b {
  display: block;
  margin-top: 6px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
}

.competitor-summary-card p {
  margin: 12px 0 0;
  color: var(--muted);
  line-height: 1.7;
}

.competitor-summary-card small {
  margin-top: 12px;
}

.comparison-table-wrap {
  margin-top: 14px;
  overflow-x: auto;
  border: 1px solid rgba(148, 163, 184, 0.18);
  border-radius: 16px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.99), rgba(248, 250, 252, 0.99)),
    #ffffff;
  box-shadow: 0 18px 30px rgba(15, 23, 42, 0.05);
}

.table-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  padding: 18px 20px 16px;
  border-bottom: 1px solid var(--line);
  background:
    linear-gradient(180deg, rgba(252, 253, 255, 0.98), rgba(247, 250, 255, 0.96)),
    #ffffff;
}

.table-head b,
.entity-cell b {
  display: block;
  color: var(--ink-strong);
}

.table-head small,
.entity-cell small,
.table-kicker {
  display: block;
}

.table-kicker {
  color: var(--muted);
  font-size: 12px;
  text-transform: uppercase;
  font-weight: 700;
}

.table-head small {
  max-width: 340px;
  color: var(--muted);
  line-height: 1.6;
  text-align: right;
}

.table-ribbon {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  padding: 14px 18px;
  border-bottom: 1px solid var(--line);
  background: rgba(250, 252, 255, 0.92);
}

.table-ribbon article {
  padding: 12px 14px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.88);
}

.table-ribbon span,
.table-ribbon small {
  display: block;
}

.table-ribbon span {
  color: var(--muted);
  font-size: 12px;
}

.table-ribbon b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 14px;
  line-height: 1.5;
}

.table-ribbon small {
  margin-top: 8px;
  color: var(--muted);
  line-height: 1.6;
}

.comparison-table {
  width: 100%;
  min-width: 760px;
  border-collapse: collapse;
  background: transparent;
}

.comparison-table th,
.comparison-table td {
  padding: 14px 16px;
  text-align: left;
  border-bottom: 1px solid var(--line);
  color: var(--ink);
  vertical-align: top;
  line-height: 1.6;
}

.comparison-table th {
  color: var(--muted);
  font-size: 12px;
  text-transform: uppercase;
  font-weight: 700;
  background: rgba(244, 247, 251, 0.86);
  letter-spacing: 0.02em;
}

.comparison-table tbody tr:hover {
  background: linear-gradient(90deg, rgba(37, 99, 235, 0.035), rgba(15, 118, 110, 0.025));
}

.entity-cell small {
  margin-top: 6px;
  color: var(--muted);
  line-height: 1.6;
}

.metric-pill {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.08);
  color: var(--accent);
  font-family: "IBM Plex Mono", monospace;
  font-size: 12px;
  font-weight: 700;
}

.narrative-cell {
  color: var(--muted);
  min-width: 180px;
}

.table-tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.competitor-card {
  position: relative;
  overflow: hidden;
  padding: 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 251, 255, 0.96)),
    #ffffff;
  box-shadow: 0 14px 24px rgba(15, 23, 42, 0.05);
}

.competitor-card::before {
  content: '';
  position: absolute;
  inset: 0 auto 0 0;
  width: 3px;
  background: linear-gradient(180deg, rgba(37, 99, 235, 0.78), rgba(15, 118, 110, 0.28));
}

.competitor-card header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.competitor-card h3 {
  margin: 8px 0 0;
  color: var(--ink-strong);
  font-size: 16px;
}

.competitor-summary {
  margin: 12px 0 0;
  color: var(--muted);
  line-height: 1.7;
}

.priority-reason {
  margin-top: 12px;
  padding: 12px;
  border: 1px solid var(--line);
  border-radius: 12px;
  background:
    linear-gradient(180deg, rgba(249, 251, 254, 0.98), rgba(243, 248, 253, 0.98)),
    #ffffff;
}

.priority-reason span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.priority-reason b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  line-height: 1.7;
}

.tag-row,
.complaint-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.link-row {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--line);
}

.advice-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-top: 16px;
}

.point-context-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  margin-top: 16px;
}

.point-context-card {
  padding: 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 251, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.point-context-card span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.point-context-card b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  line-height: 1.6;
}

.point-context-card p {
  margin: 10px 0 0;
  color: var(--muted);
  line-height: 1.7;
}

.advice-card {
  position: relative;
  overflow: hidden;
  padding: 16px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 251, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.advice-card::before {
  content: '';
  position: absolute;
  inset: 0 auto 0 0;
  width: 3px;
  background: linear-gradient(180deg, rgba(37, 99, 235, 0.78), rgba(15, 118, 110, 0.26));
}

.advice-card b {
  display: inline-flex;
  align-items: center;
  min-height: 26px;
  padding: 0 10px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.08);
  color: var(--accent);
  font-family: "IBM Plex Mono", monospace;
  font-size: 12px;
}

.advice-card p {
  margin: 10px 0 0;
  color: var(--muted);
  line-height: 1.7;
  text-wrap: pretty;
}

.stack {
  display: grid;
  gap: 16px;
}

.analytics-grid {
  align-items: stretch;
}

.points-shell {
  margin-top: 16px;
}

.points-head {
  margin-bottom: 14px;
}

.points {
  display: grid;
  gap: 14px;
}

@media (max-width: 1280px) {
  .section-head,
  .table-head {
    display: grid;
  }

  .section-actions {
    justify-items: start;
  }

  .section-head p,
  .table-head small {
    max-width: none;
    text-align: left;
  }

  .point-context-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .analytics-grid {
    grid-template-columns: 1fr;
  }

  .stack {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    align-items: start;
  }
}

@media (max-width: 900px) {
  .head-actions,
  .status-metrics,
  .status-grid,
  .section-head,
  .section-actions,
  .content-ribbon,
  .analytics-ribbon,
  .table-ribbon,
  .table-head,
  .summary-metrics,
  .competitor-grid,
  .advice-list,
  .point-context-grid {
    grid-template-columns: 1fr;
  }

  .section-head p {
    text-align: left;
  }

  .table-head small {
    text-align: left;
  }

  .head-actions {
    display: grid;
  }

  .stack {
    grid-template-columns: 1fr;
  }
}
</style>
