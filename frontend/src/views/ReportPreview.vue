<template>
  <section>
    <PageHero
      eyebrow="选品报告"
      :title="report?.title ?? '选品报告'"
      description="沉淀品牌上下文、机会判断、风险提示与下一步动作，作为本轮选品结论的正式输出。"
    >
      <template #actions>
        <div class="head-actions">
          <el-button :icon="Back" @click="backToOpportunity">返回机会</el-button>
          <el-button :loading="exporting" @click="downloadExcel">下载 Excel</el-button>
          <el-button :loading="exporting" @click="downloadPdf">下载 PDF</el-button>
          <el-button type="primary" :icon="Download" :loading="exporting" @click="downloadReport">下载 Markdown</el-button>
        </div>
      </template>
    </PageHero>

    <WorkflowSummary current-stage="report" :workflow="workflow" />

    <div v-if="loading" class="panel pad status-panel">
      <el-skeleton :rows="10" animated />
    </div>

    <div v-else-if="errorMessage" class="panel pad status-panel">
      <el-result icon="error" title="报告加载失败" :sub-title="errorMessage">
        <template #extra>
          <el-button type="primary" @click="load">重新加载</el-button>
        </template>
      </el-result>
    </div>

    <template v-else-if="report">
      <DecisionHeroPanel
        eyebrow="正式决策报告"
        :title="`${report.title} 当前的正式结论是什么`"
        description="沉淀品牌上下文、机会判断、风险提示与下一步动作，作为本轮选品结论的正式输出。"
        :context-items="reportContextItems"
        :overview-items="reportOverviewItems"
        :metric-items="reportMetricItems"
      />

      <section class="panel pad report-executive-summary">
        <div class="executive-head">
          <div>
            <span class="eyebrow">管理者摘要</span>
            <h2>先看结论，再决定是否进入明细复核</h2>
          </div>
          <p>这一层用于先压缩本轮结论、风险、动作和平台路径，适合业务负责人快速判断“是否继续推进、推进到哪一步”。</p>
        </div>
        <div class="executive-ribbon">
          <article>
            <span>结论</span>
            <b>{{ report.decisionSummary.decision }}</b>
            <small>{{ report.decisionSummary.headline }}</small>
          </article>
          <article>
            <span>最高风险</span>
            <b>{{ report.riskSummary.highestLevel }}</b>
            <small>{{ report.riskSummary.primaryRiskTitle }}</small>
          </article>
          <article>
            <span>动作主焦点</span>
            <b>{{ report.actionSummary.focusActionStatus }}</b>
            <small>{{ report.actionSummary.focusActionTitle }}</small>
          </article>
          <article>
            <span>首发平台</span>
            <b>{{ report.opportunityNarrative.platformPlaybook.firstLaunchPlatform }}</b>
            <small>{{ report.opportunityNarrative.platformPlaybook.validationPlatform }} 用于验证反馈</small>
          </article>
        </div>
      </section>

      <div class="report-grid">
        <section class="panel pad report-section">
          <span class="eyebrow">品牌上下文</span>
          <h2>本轮决策基线</h2>
          <p>{{ report.brandSummary }}</p>
        </section>

        <section class="panel pad report-section">
          <span class="eyebrow">竞争与利润</span>
          <h2>{{ report.competitionReport.marketType }}</h2>
          <div class="mini-grid">
            <div>
              <span>CR5</span>
              <b>{{ report.competitionReport.cr5 }}</b>
            </div>
            <div>
              <span>进入窗口</span>
              <b>{{ report.competitionReport.entryWindow }}</b>
            </div>
            <div>
              <span>净利率</span>
              <b>{{ report.profitAnalysis.netMargin }}</b>
            </div>
            <div>
              <span>MOQ</span>
              <b>{{ report.supplyChainFeasibility.moq }}</b>
            </div>
          </div>
        </section>
      </div>

      <DecisionSummaryCard :summary="report.decisionSummary" />

      <EntryDecisionPanel
        :decision="report.decisionSummary.decision"
        :headline="report.decisionSummary.headline"
        :entry-window="report.competitionReport.entryWindow"
        :net-margin="report.profitAnalysis.netMargin"
        :risk-hint="report.supplyChainFeasibility.riskHint"
        :first-launch-platform="report.opportunityNarrative.platformPlaybook.firstLaunchPlatform"
        :validation-platform="report.opportunityNarrative.platformPlaybook.validationPlatform"
      />

      <RiskSummaryPanel
        title="这轮最该盯哪几个风险"
        :total-count="report.riskSummary.totalCount"
        :highest-level="report.riskSummary.highestLevel"
        :primary-risk-title="report.riskSummary.primaryRiskTitle"
        :primary-risk-description="report.riskSummary.primaryRiskDescription"
        :suggested-attention="report.riskSummary.suggestedAttention"
        :summary="report.riskSummary.summary"
      />

      <div class="report-grid">
        <section class="panel pad report-section">
          <span class="eyebrow">重点信号</span>
          <h2>本轮关注的市场异常</h2>
          <div class="content-ribbon">
            <article>
              <span>信号数量</span>
              <b>{{ report.keySignals.length }}</b>
              <small>正式输出保留当前平台语境下的重点异常</small>
            </article>
            <article>
              <span>报告状态</span>
              <b>{{ workflow?.reportReady ? '可交付' : '待确认' }}</b>
              <small>{{ report.generatedAt }}</small>
            </article>
          </div>
          <ul class="report-list">
            <li v-for="item in report.keySignals" :key="item">{{ item }}</li>
          </ul>
        </section>

        <section class="panel pad report-section">
          <span class="eyebrow">机会摘要</span>
          <h2>可切入机会点</h2>
          <div class="content-ribbon">
            <article>
              <span>机会摘要数</span>
              <b>{{ report.opportunityHighlights.length }}</b>
              <small>已压缩为可直接复核的正式输出条目</small>
            </article>
            <article>
              <span>首发平台</span>
              <b>{{ report.opportunityNarrative.platformPlaybook.firstLaunchPlatform }}</b>
              <small>{{ report.opportunityNarrative.platformPlaybook.validationPlatform }} 用于验证反馈</small>
            </article>
          </div>
          <ul class="report-list">
            <li v-for="item in report.opportunityHighlights" :key="item">{{ item }}</li>
          </ul>
        </section>
      </div>

      <section v-if="report.opportunityNarrative.opportunityLensFocuses.length" class="panel pad">
        <span class="eyebrow">机会点视角判断</span>
        <h2>为什么优先看这个机会点</h2>
        <div class="lens-grid">
          <article
            v-for="item in report.opportunityNarrative.opportunityLensFocuses"
            :key="`${item.lensKey}-${item.scenarioText}`"
            class="lens-card"
          >
            <header>
              <div>
                <span>{{ item.lensLabel }}</span>
                <b>{{ item.scenarioText }}</b>
              </div>
              <el-tag size="small" effect="plain">{{ item.opportunityScore }} / {{ item.opportunityLevel }}</el-tag>
            </header>
            <p>{{ item.summary }}</p>
            <div class="lens-meta">
              <div>
                <span>目标人群</span>
                <b>{{ item.targetCrowd }}</b>
              </div>
              <div>
                <span>生命周期</span>
                <b>{{ item.lifecycleStage }}</b>
              </div>
              <div>
                <span>入场时机</span>
                <b>{{ item.entryTiming }}</b>
              </div>
            </div>
            <small>{{ item.differentiation }}</small>
          </article>
        </div>
      </section>

      <div class="report-grid">
        <CompetitorSummaryPanel
          title="当前赛道已跟踪对象"
          :tracked-shop-count="report.opportunityNarrative.competitorSummary.trackedShopCount"
          :covered-platforms="report.opportunityNarrative.competitorSummary.coveredPlatforms"
          :total-hit-product-count="report.opportunityNarrative.competitorSummary.totalHitProductCount"
          :strongest-signal="report.opportunityNarrative.competitorSummary.strongestSignal"
          :summary="report.opportunityNarrative.competitorSummary.summary"
          :common-complaint-topics="report.opportunityNarrative.competitorSummary.commonComplaintTopics"
        />

        <section class="panel pad report-section">
          <span class="eyebrow">差异化建议</span>
          <h2>建议优先验证的切入方向</h2>
          <div class="content-ribbon">
            <article>
              <span>建议数量</span>
              <b>{{ report.opportunityNarrative.differentiationAdvice.length }}</b>
              <small>用于支撑正式报告后的首轮验证动作</small>
            </article>
            <article>
              <span>平台语境</span>
              <b>{{ report.platformView }}</b>
              <small>差异化建议与当前平台判断保持一致</small>
            </article>
          </div>
          <ul class="report-list">
            <li v-for="item in report.opportunityNarrative.differentiationAdvice" :key="item">{{ item }}</li>
          </ul>
        </section>
      </div>

      <div class="report-grid">
        <PlatformPlaybookPanel
          :first-launch-platform="report.opportunityNarrative.platformPlaybook.firstLaunchPlatform"
          :validation-platform="report.opportunityNarrative.platformPlaybook.validationPlatform"
          :conversion-platform="report.opportunityNarrative.platformPlaybook.conversionPlatform"
          :summary="report.opportunityNarrative.platformPlaybook.summary"
          :execution-hints="report.opportunityNarrative.platformPlaybook.executionHints"
        />

        <section v-if="report.opportunityNarrative.competitorComparison.length" class="panel pad report-section">
          <span class="eyebrow">竞品横向对比</span>
          <h2>重点对象快照</h2>
          <div class="comparison-table-wrap">
            <div class="table-head">
              <div>
                <span class="table-kicker">正式报告附表</span>
                <b>把重点竞品先按业务快照展开</b>
              </div>
              <small>适合品牌负责人快速复核当前对象池，而不必先回到机会页逐张查看。</small>
            </div>
            <div class="table-ribbon">
              <article>
                <span>重点对象数</span>
                <b>{{ report.opportunityNarrative.competitorComparison.length }}</b>
                <small>进入正式报告附表的当前重点竞品数量</small>
              </article>
              <article>
                <span>爆品最多对象</span>
                <b>{{ reportTopHitCompetitor?.shopName ?? '暂无对象' }}</b>
                <small>{{ reportTopHitCompetitor ? `${reportTopHitCompetitor.hitProductCount} 个爆品样本` : '等待竞品数据' }}</small>
              </article>
              <article>
                <span>主平台覆盖</span>
                <b>{{ reportPlatformCoverage }}</b>
                <small>正式报告沿用当前平台语境做对象归纳</small>
              </article>
            </div>
            <table class="comparison-table">
              <thead>
                <tr>
                  <th>竞品</th>
                  <th>平台</th>
                  <th>爆品数</th>
                  <th>增长信号</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="shop in report.opportunityNarrative.competitorComparison" :key="`report-${shop.shopName}-${shop.addedAt}`">
                  <td class="entity-cell">
                    <b>{{ shop.shopName }}</b>
                    <small>{{ shop.focusCategory }}</small>
                  </td>
                  <td><el-tag size="small" effect="plain" type="info">{{ shop.platform }}</el-tag></td>
                  <td><span class="metric-pill">{{ shop.hitProductCount }}</span></td>
                  <td class="narrative-cell">{{ shop.growthSignal }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div v-if="report.opportunityNarrative.competitorFocusReasons.length" class="focus-reason-list">
            <article
              v-for="item in report.opportunityNarrative.competitorFocusReasons"
              :key="`${item.shopName}-${item.reason}`"
              class="focus-reason-card"
            >
              <span>{{ item.shopName }}</span>
              <b>{{ item.reason }}</b>
            </article>
          </div>
        </section>
      </div>

      <div class="report-grid report-grid--attachment">
        <ActionSummaryPanel
          title="这轮动作当前推进到哪了"
          :total-count="report.actionSummary.totalCount"
          :completed-count="report.actionSummary.completedCount"
          :in-progress-count="report.actionSummary.inProgressCount"
          :pending-count="report.actionSummary.pendingCount"
          :focus-action-title="report.actionSummary.focusActionTitle"
          :focus-action-status="report.actionSummary.focusActionStatus"
          :latest-updated-at="report.actionSummary.latestUpdatedAt"
          :summary="report.actionSummary.summary"
        />

        <NextActionsPanel :actions="report.nextActions" @update-status="updateActionStatus" />

        <section class="panel pad attachment-panel">
          <div class="attachment-head">
            <div>
              <span class="eyebrow">Markdown 原文</span>
              <h2>交付物导出内容</h2>
            </div>
            <div class="attachment-meta">
              <article>
                <span>文件名</span>
                <b>{{ report.fileName }}</b>
              </article>
              <article>
                <span>导出格式</span>
                <b>{{ report.format }}</b>
              </article>
            </div>
          </div>
          <div class="content-ribbon attachment-ribbon">
            <article>
              <span>章节数</span>
              <b>{{ attachmentSectionCount }}</b>
              <small>按 Markdown 标题粗略统计当前正式交付内容层次</small>
            </article>
            <article>
              <span>内容体量</span>
              <b>{{ report.content.length }}</b>
              <small>字符规模可用于判断是否需要进一步拆分专题报告</small>
            </article>
          </div>
          <div class="attachment-toolbar">
            <span>正式交付附件</span>
            <small>保留结构化内容之外的完整 Markdown 原文，便于下载、归档和二次转交。</small>
          </div>
          <pre class="report-content">{{ report.content }}</pre>
        </section>
      </div>

      <article class="panel pad report-shell">
        <header class="report-head">
          <div>
            <span class="eyebrow">成本与供应链</span>
            <h2>执行落地约束</h2>
          </div>
          <el-tag type="warning" effect="plain">{{ report.fileName }}</el-tag>
        </header>
        <div class="mini-grid">
          <div>
            <span>目标售价</span>
            <b>{{ report.profitAnalysis.targetPrice }}</b>
          </div>
          <div>
            <span>单件成本</span>
            <b>{{ report.profitAnalysis.unitCost }}</b>
          </div>
          <div>
            <span>广告成本</span>
            <b>{{ report.profitAnalysis.adCost }}</b>
          </div>
          <div>
            <span>工厂产能</span>
            <b>{{ report.supplyChainFeasibility.factoryCapacity }}</b>
          </div>
        </div>
        <div class="narrative-grid">
          <div class="narrative-block">
            <span>利润结论</span>
            <p>{{ report.profitAnalysis.summary }}</p>
          </div>
          <div class="narrative-block">
            <span>供应链结论</span>
            <p>{{ report.supplyChainFeasibility.conclusion }}</p>
          </div>
        </div>
      </article>
    </template>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Back, Download } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { api } from '@/api'
import { getBrandId, setBrandId } from '@/composables/useBrandContext'
import { getApiErrorMessage } from '@/lib/apiError'
import type {
  CompetitorSummary,
  OpportunityLensFocus,
  PlatformPlaybook,
  ReportAction,
  ReportActionSummary,
  ReportRiskSummary,
  SelectionReport,
  WorkflowProgress
} from '@/types'
import ActionSummaryPanel from '@/components/common/ActionSummaryPanel.vue'
import CompetitorSummaryPanel from '@/components/common/CompetitorSummaryPanel.vue'
import DecisionHeroPanel from '@/components/common/DecisionHeroPanel.vue'
import DecisionSummaryCard from '@/components/common/DecisionSummary.vue'
import EntryDecisionPanel from '@/components/common/EntryDecisionPanel.vue'
import PageHero from '@/components/common/PageHero.vue'
import PlatformPlaybookPanel from '@/components/common/PlatformPlaybookPanel.vue'
import RiskSummaryPanel from '@/components/common/RiskSummaryPanel.vue'
import WorkflowSummary from '@/components/common/WorkflowSummary.vue'
import NextActionsPanel from '@/components/opportunity/NextActionsPanel.vue'

const props = defineProps<{ cardId: string }>()
const route = useRoute()
const router = useRouter()
const loading = ref(false)
const exporting = ref(false)
const errorMessage = ref('')
const report = ref<SelectionReport>()
const workflow = ref<WorkflowProgress>()
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

function buildFallbackRiskSummary(reportData: SelectionReport): ReportRiskSummary {
  const risks = reportData.decisionSummary?.risks ?? []
  const highestRisk = [...risks].sort((left, right) => {
    const leftRank = left.level === 'high' ? 0 : left.level === 'medium' ? 1 : 2
    const rightRank = right.level === 'high' ? 0 : right.level === 'medium' ? 1 : 2
    return leftRank - rightRank
  })[0]
  const highestLevel = highestRisk?.level ?? 'low'

  return {
    totalCount: risks.length,
    highestLevel,
    primaryRiskTitle: highestRisk?.title ?? '暂无显性风险',
    primaryRiskDescription: highestRisk?.description ?? '当前未识别到高优先级风险。',
    suggestedAttention:
      highestLevel === 'high'
        ? '优先补齐关键履约、预算或竞争壁垒验证，再考虑扩大投入。'
        : highestLevel === 'medium'
          ? '保持小样验证节奏，同时持续复核差异化卖点和供应链稳定性。'
          : '维持当前验证节奏，重点跟踪转化效率与售后反馈变化。',
    summary: `当前共识别 ${risks.length} 条主要风险，最高等级为「${highestLevel}」，应优先盯防「${highestRisk?.title ?? '暂无显性风险'}」。`
  }
}

function buildFallbackActionSummary(actions: ReportAction[]): ReportActionSummary {
  const completedCount = actions.filter((item) => item.status.includes('完成')).length
  const inProgressCount = actions.filter((item) => item.status.includes('进行')).length
  const pendingCount = actions.length - completedCount - inProgressCount
  const focusAction = [...actions].sort((left, right) => actionPriorityRank(left.priority, left.status) - actionPriorityRank(right.priority, right.status))[0]

  return {
    totalCount: actions.length,
    completedCount,
    inProgressCount,
    pendingCount,
    focusActionTitle: focusAction?.title ?? '暂无动作',
    focusActionStatus: focusAction?.status ?? '-',
    latestUpdatedAt: focusAction?.updatedAt ?? '-',
    summary: `当前共 ${actions.length} 个推进动作，已完成 ${completedCount} 个，进行中 ${inProgressCount} 个，待推进 ${pendingCount} 个。`
  }
}

function buildFallbackCompetitorSummary(): CompetitorSummary {
  return {
    trackedShopCount: 0,
    coveredPlatforms: platformView.value,
    totalHitProductCount: 0,
    strongestSignal: '报告接口尚未返回竞品快照',
    summary: `当前正式报告仍以前台主结论为主，建议后续补齐 ${platformView.value} 视角下的竞品样本后再做横向复核。`,
    commonComplaintTopics: []
  }
}

function buildFallbackPlatformPlaybook(reportData: SelectionReport): PlatformPlaybook {
  const launchPlatform = platformView.value === '全平台' ? '天猫' : platformView.value
  const validationPlatform = pickValidationPlatform(launchPlatform)

  return {
    firstLaunchPlatform: launchPlatform,
    validationPlatform,
    conversionPlatform: launchPlatform === '抖音' || launchPlatform === '小红书' ? '天猫' : launchPlatform,
    executionHints: [
      reportData.opportunityHighlights[0]
        ? `围绕「${reportData.opportunityHighlights[0].split('|')[0].trim()}」先做首轮素材验证。`
        : '围绕当前优先机会点先做首轮素材验证。',
      '用小预算试投先确认点击率、收藏率和评论反馈，再决定是否放量。',
      '把供应链风险和差评痛点写进验证清单，避免报告结论与执行脱节。'
    ],
    summary: `建议先在 ${launchPlatform} 落地首轮验证，再用 ${validationPlatform} 复核反馈，并将成交承接压回更稳定的平台链路。`
  }
}

function buildFallbackDifferentiationAdvice(reportData: SelectionReport, playbook: PlatformPlaybook) {
  const firstHighlight = reportData.opportunityHighlights[0]?.split('|').map((item) => item.trim()) ?? []
  const firstSignal = reportData.keySignals[0]?.split('|').map((item) => item.trim()) ?? []

  return [
    firstHighlight[2]
      ? `首轮优先验证「${firstHighlight[2]}」这条差异化表达，避免同时铺开过多卖点。`
      : '首轮优先聚焦一个可解释的差异化卖点，避免验证阶段过度分散。',
    firstSignal[2]
      ? `把「${firstSignal[2]}」对应信号转成内容切入口，先验证用户是否愿意为这类问题买单。`
      : '把当前最强信号转成内容切入口，先验证用户是否愿意为该问题买单。',
    `先在 ${playbook.firstLaunchPlatform} 跑小样，再用 ${playbook.validationPlatform} 复核互动反馈与转化承接。`
  ]
}

function buildFallbackLensFocuses(reportData: SelectionReport): OpportunityLensFocus[] {
  return (reportData.opportunityHighlights ?? []).slice(0, 2).map((item, index) => {
    const segments = item.split('|').map((segment) => segment.trim())
    return {
      lensKey: index === 0 ? 'balanced' : 'scenario',
      lensLabel: index === 0 ? '综合判断' : '人群场景',
      summary: reportData.decisionSummary.headline,
      targetCrowd: segments[0]?.split('·')?.[0]?.trim() ?? '待补充目标人群',
      scenarioText: segments[0] ?? '待补充机会点',
      differentiation: segments[2] ?? '建议进一步补充差异化表达',
      opportunityScore: reportData.decisionSummary.confidence ?? 0,
      opportunityLevel: reportData.decisionSummary.decision ?? '待判断',
      entryTiming: reportData.competitionReport.entryWindow ?? '待补充入场时机',
      lifecycleStage: '验证期',
      reason: reportData.decisionSummary.headline
    }
  })
}

function normalizeReport(reportData: SelectionReport): SelectionReport {
  const nextActions = Array.isArray(reportData.nextActions) ? reportData.nextActions : []
  const riskSummary = (reportData as Partial<SelectionReport>).riskSummary ?? buildFallbackRiskSummary(reportData)
  const actionSummary = (reportData as Partial<SelectionReport>).actionSummary ?? buildFallbackActionSummary(nextActions)
  const fallbackPlaybook = buildFallbackPlatformPlaybook(reportData)
  const opportunityNarrative = {
    competitorSummary: (reportData as Partial<SelectionReport>).opportunityNarrative?.competitorSummary ?? buildFallbackCompetitorSummary(),
    platformPlaybook: (reportData as Partial<SelectionReport>).opportunityNarrative?.platformPlaybook ?? fallbackPlaybook,
    competitorComparison: (reportData as Partial<SelectionReport>).opportunityNarrative?.competitorComparison ?? [],
    differentiationAdvice:
      (reportData as Partial<SelectionReport>).opportunityNarrative?.differentiationAdvice?.length
        ? (reportData as Partial<SelectionReport>).opportunityNarrative!.differentiationAdvice
        : buildFallbackDifferentiationAdvice(reportData, fallbackPlaybook),
    competitorFocusReasons: (reportData as Partial<SelectionReport>).opportunityNarrative?.competitorFocusReasons ?? [],
    opportunityLensFocuses:
      (reportData as Partial<SelectionReport>).opportunityNarrative?.opportunityLensFocuses?.length
        ? (reportData as Partial<SelectionReport>).opportunityNarrative!.opportunityLensFocuses
        : buildFallbackLensFocuses(reportData)
  }

  return {
    ...reportData,
    platformView: reportData.platformView || platformView.value,
    keySignals: Array.isArray(reportData.keySignals) ? reportData.keySignals : [],
    opportunityHighlights: Array.isArray(reportData.opportunityHighlights) ? reportData.opportunityHighlights : [],
    nextActions,
    riskSummary,
    actionSummary,
    opportunityNarrative
  }
}
const reportContextItems = computed(() => {
  if (!report.value) return []
  return [
    { label: '平台视角', value: report.value.platformView, hint: '延续自洞察页的平台判断上下文' },
    { label: '竞品覆盖', value: report.value.opportunityNarrative.competitorSummary.coveredPlatforms, hint: `${report.value.opportunityNarrative.competitorSummary.trackedShopCount} 个跟踪对象` },
    { label: '爆品样本', value: report.value.opportunityNarrative.competitorSummary.totalHitProductCount, hint: report.value.opportunityNarrative.competitorSummary.strongestSignal },
    { label: '报告就绪', value: workflow.value?.reportReady ? '已就绪' : '未就绪', hint: workflow.value?.brandName ?? '-' }
  ]
})
const reportOverviewItems = computed(() => {
  if (!report.value) return []
  return [
    { label: '能不能进', value: report.value.decisionSummary.decision, hint: report.value.decisionSummary.headline },
    { label: '风险高不高', value: report.value.riskSummary.highestLevel, hint: report.value.riskSummary.primaryRiskTitle },
    { label: '动作推进到哪', value: report.value.actionSummary.focusActionStatus, hint: report.value.actionSummary.focusActionTitle },
    { label: '先去哪里起量', value: report.value.opportunityNarrative.platformPlaybook.firstLaunchPlatform, hint: `${report.value.opportunityNarrative.platformPlaybook.validationPlatform} 验证反馈` }
  ]
})
const reportMetricItems = computed(() => {
  if (!report.value) return []
  return [
    { label: '报告格式', value: report.value.format },
    { label: '生成时间', value: report.value.generatedAt },
    { label: '净利率', value: report.value.profitAnalysis.netMargin },
    { label: 'MOQ', value: report.value.supplyChainFeasibility.moq }
  ]
})
const attachmentSectionCount = computed(() => {
  const content = report.value?.content ?? ''
  return content.split('\n').filter(line => line.trim().startsWith('#')).length
})
const reportTopHitCompetitor = computed(() =>
  [...(report.value?.opportunityNarrative.competitorComparison ?? [])].sort((left, right) => right.hitProductCount - left.hitProductCount)[0]
)
const reportPlatformCoverage = computed(() => {
  const platforms = [...new Set((report.value?.opportunityNarrative.competitorComparison ?? []).map((item) => item.platform))]
  return platforms.join(' / ') || '-'
})

async function load() {
  loading.value = true
  errorMessage.value = ''
  try {
    const [reportData, workflowData] = await Promise.all([
      api.exportReport(Number(props.cardId), brandId.value, platformView.value),
      api.getWorkflow(brandId.value, platformView.value)
    ])
    report.value = normalizeReport(reportData)
    workflow.value = workflowData
  } catch (error) {
    report.value = undefined
    workflow.value = undefined
    errorMessage.value = getApiErrorMessage(error, '请检查后端服务是否已启动，或稍后重试。')
  } finally {
    loading.value = false
  }
}

function backToOpportunity() {
  router.push({ path: `/opportunity/${props.cardId}`, query: { brandId: brandId.value, platform: platformView.value } })
}

function downloadBlob(blob: Blob, fileName: string) {
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = fileName
  link.click()
  URL.revokeObjectURL(url)
}

function downloadReport() {
  if (!report.value) {
    ElMessage.warning('报告尚未加载完成')
    return
  }
  exporting.value = true
  try {
    downloadBlob(new Blob([report.value.content], { type: 'text/markdown;charset=utf-8' }), report.value.fileName)
    ElMessage.success('选品报告已导出')
  } finally {
    exporting.value = false
  }
}

async function downloadExcel() {
  exporting.value = true
  try {
    const response = await api.downloadReportExcel(Number(props.cardId), brandId.value, platformView.value)
    downloadBlob(response.data, `${report.value?.fileName ?? 'report'}.xlsx`)
    ElMessage.success('Excel 报告已导出')
  } catch (error) {
    ElMessage.error(getApiErrorMessage(error, 'Excel 导出失败'))
  } finally {
    exporting.value = false
  }
}

async function downloadPdf() {
  exporting.value = true
  try {
    const response = await api.downloadReportPdf(Number(props.cardId), brandId.value, platformView.value)
    downloadBlob(response.data, `${report.value?.fileName ?? 'report'}.pdf`)
    ElMessage.success('PDF 报告已导出')
  } catch (error) {
    ElMessage.error(getApiErrorMessage(error, 'PDF 导出失败'))
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

.report-shell {
  overflow: hidden;
}

.report-executive-summary {
  margin-bottom: 16px;
}

.executive-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 16px;
}

.executive-head h2 {
  margin: 10px 0 0;
  color: var(--ink-strong);
  font-size: 22px;
}

.executive-head p {
  margin: 0;
  max-width: 560px;
  color: var(--muted);
  line-height: 1.7;
  text-align: right;
}

.executive-ribbon {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.executive-ribbon article {
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.executive-ribbon span,
.executive-ribbon small {
  display: block;
}

.executive-ribbon span {
  color: var(--muted);
  font-size: 12px;
}

.executive-ribbon b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 18px;
  line-height: 1.5;
  overflow-wrap: anywhere;
}

.executive-ribbon small {
  margin-top: 10px;
  color: var(--muted);
  line-height: 1.6;
  overflow-wrap: anywhere;
}

.attachment-panel {
  overflow: hidden;
}

.report-grid--attachment {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.report-grid--attachment .attachment-panel {
  grid-column: 1 / -1;
}

.report-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.report-section {
  position: relative;
}

.report-section::before {
  content: '';
  position: absolute;
  left: 24px;
  right: 24px;
  top: 0;
  height: 3px;
  border-radius: 999px;
  background: linear-gradient(90deg, rgba(37, 99, 235, 0.22), rgba(15, 118, 110, 0.16));
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
.executive-ribbon article,
.table-ribbon article,
.lens-card,
.focus-reason-card,
.narrative-block {
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

.report-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 16px;
}

.attachment-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 14px;
}

.attachment-head h2 {
  margin: 10px 0 0;
  color: var(--ink-strong);
}

.attachment-meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.attachment-meta article {
  padding: 12px 14px;
  border: 1px solid var(--line);
  border-radius: 12px;
  background:
    linear-gradient(180deg, rgba(251, 252, 255, 0.98), rgba(246, 249, 253, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.attachment-meta span,
.attachment-toolbar span,
.attachment-toolbar small {
  display: block;
}

.attachment-meta span,
.attachment-toolbar span {
  color: var(--muted);
  font-size: 12px;
}

.attachment-meta b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 13px;
  line-height: 1.6;
  overflow-wrap: anywhere;
}

.attachment-toolbar {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  margin-bottom: 14px;
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(252, 253, 255, 0.98), rgba(247, 250, 255, 0.96)),
    #ffffff;
}

.attachment-ribbon {
  margin-top: 0;
  margin-bottom: 14px;
}

.attachment-toolbar small {
  max-width: 420px;
  color: var(--muted);
  line-height: 1.6;
  text-align: right;
}

.eyebrow {
  display: block;
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
}

.report-head h2 {
  margin: 10px 0 0;
  color: var(--ink-strong);
}

.mini-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.mini-grid span,
.narrative-block span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.mini-grid b {
  display: block;
  margin-top: 6px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
}

.report-list {
  margin: 14px 0 0;
  padding-left: 18px;
  display: grid;
  gap: 10px;
  color: var(--ink);
  line-height: 1.7;
  text-wrap: pretty;
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.narrative-copy {
  margin: 14px 0 0;
  color: var(--muted);
  line-height: 1.75;
  text-wrap: pretty;
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

.table-kicker,
.table-head small,
.entity-cell small {
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
  min-width: 560px;
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

.focus-reason-list {
  display: grid;
  gap: 12px;
  margin-top: 14px;
}

.lens-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-top: 16px;
}

.lens-card {
  padding: 14px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.lens-card header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
}

.lens-card header span,
.lens-meta span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.lens-card header b,
.lens-meta b {
  display: block;
  margin-top: 6px;
  color: var(--ink-strong);
  line-height: 1.6;
}

.lens-card p {
  margin: 12px 0 0;
  color: var(--muted);
  line-height: 1.75;
}

.lens-meta {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 14px;
}

.lens-card small {
  display: block;
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid var(--line);
  color: var(--muted);
  line-height: 1.7;
}

.focus-reason-card {
  padding: 14px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 251, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.focus-reason-card span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.focus-reason-card b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  line-height: 1.7;
}

.report-content {
  margin: 0;
  max-height: 640px;
  overflow: auto;
  padding: 22px 24px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(249, 251, 255, 0.98), rgba(244, 247, 252, 0.98)),
    #f8fafc;
  color: var(--ink);
  font-family: "IBM Plex Mono", monospace;
  font-size: 13px;
  line-height: 1.75;
  white-space: pre-wrap;
  word-break: break-word;
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.82),
    inset 0 0 0 1px rgba(255, 255, 255, 0.12);
}

@media (max-width: 1280px) {
  .executive-head,
  .attachment-head,
  .report-head,
  .attachment-toolbar,
  .table-head {
    display: grid;
  }

  .executive-head p,
  .attachment-toolbar small,
  .table-head small {
    max-width: none;
    text-align: left;
  }

  .executive-ribbon {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .report-grid--attachment,
  .attachment-meta,
  .content-ribbon,
  .narrative-grid {
    grid-template-columns: 1fr;
  }
}

.narrative-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.narrative-block {
  padding: 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 251, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.narrative-block p {
  margin: 8px 0 0;
  color: var(--muted);
  line-height: 1.75;
}

@media (max-width: 900px) {
  .head-actions,
  .executive-head,
  .executive-ribbon,
  .report-head,
  .attachment-head,
  .attachment-meta,
  .attachment-toolbar,
  .content-ribbon,
  .report-grid,
  .table-ribbon,
  .table-head,
  .lens-grid,
  .lens-meta,
  .mini-grid,
  .narrative-grid {
    display: grid;
  }

  .table-head small {
    text-align: left;
  }

  .attachment-toolbar small {
    text-align: left;
  }

  .executive-ribbon {
    grid-template-columns: 1fr;
  }

  .head-actions {
    display: grid;
  }
}
</style>
