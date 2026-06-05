<template>
  <section>
    <PageHero
      eyebrow="信号雷达"
      title="先看今天冒出来的变化，再决定是否进入洞察"
      description="每日扫描搜索飙升、社媒异常与差评痛点，把值得今天优先判断的 3-5 个信号先提出来。"
    >
      <template #actions>
        <el-button type="primary" :icon="TrendCharts" @click="router.push({ path: '/insight', query: { brandId } })">
          进入洞察
        </el-button>
      </template>
    </PageHero>

    <WorkflowSummary current-stage="radar" :workflow="workflow" />

    <section class="panel pad radar-hero">
      <div class="radar-hero-main">
        <div class="radar-copy">
          <span class="eyebrow">今日摘要</span>
          <h2>{{ brandName }} 当前先看 {{ leadSignal?.categoryName ?? '信号池' }}</h2>
          <p>{{ leadSignal?.summary ?? '先从最强信号切入，再决定是否回到洞察页做更完整的趋势、竞争与供需验证。' }}</p>
          <small>{{ leadSignal ? `${leadSignal.platform} · ${leadSignal.signalType} · 最近更新 ${leadSignal.discoveredAt}` : '当前还没有可用信号' }}</small>
        </div>
        <div class="radar-context">
          <article class="context-card">
            <span>当前主判断</span>
            <b>{{ leadSignal?.decision ?? '等待信号' }}</b>
            <small>{{ leadSignal?.recommendedAction ?? '待生成推荐动作' }}</small>
          </article>
          <article class="context-card">
            <span>信号覆盖平台</span>
            <b>{{ activePlatformCount }}</b>
            <small>{{ activePlatformText }}</small>
          </article>
          <article class="context-card">
            <span>可转竞品跟踪</span>
            <b>{{ trackableCount }}</b>
            <small>可直接进入竞品监控继续看对象</small>
          </article>
          <article class="context-card">
            <span>当前最强理由</span>
            <b>{{ leadReasonTitle }}</b>
            <small>{{ leadRiskTitle }}</small>
          </article>
        </div>
      </div>
    </section>

    <section class="panel pad push-panel">
      <div class="push-head">
        <div>
          <span class="eyebrow">信号推送（MVP P1）</span>
          <h3>钉钉 / 企业微信 / 微信群机器人</h3>
          <p>配置 Webhook 后一键推送；URL 含 <code>demo</code> / <code>example.com</code> 时将<strong>模拟外发成功</strong>并写入投递记录。</p>
        </div>
        <el-button type="primary" :loading="pushing" @click="sendDigest">推送今日摘要</el-button>
      </div>
      <div class="push-form">
        <el-select v-model="pushForm.channelType" style="width: 160px">
          <el-option label="钉钉" value="钉钉" />
          <el-option label="企业微信" value="企业微信" />
          <el-option label="微信群" value="微信群" />
        </el-select>
        <el-input v-model="pushForm.webhookUrl" placeholder="Webhook URL（可点「填入演示」）" />
        <el-switch v-model="pushForm.enabled" active-text="启用" />
        <el-button @click="fillDemoWebhook">填入演示 Webhook</el-button>
        <el-button @click="savePushConfig">保存配置</el-button>
      </div>
      <div v-if="deliveries.length" class="delivery-log">
        <span class="eyebrow">Webhook 投递记录（样例）</span>
        <ul>
          <li v-for="row in deliveries" :key="row.id">
            <b>{{ row.channelType }} · {{ row.status }}</b>
            <small>{{ row.deliveredAt }} · {{ row.webhookMasked }}</small>
            <p>{{ row.payloadPreview }}</p>
            <code>{{ row.responseBody }}</code>
          </li>
        </ul>
      </div>
    </section>

    <div class="metric-strip">
      <div class="metric">
        <span>活跃信号</span>
        <b>{{ signals.length }}</b>
      </div>
      <div class="metric">
        <span>强信号</span>
        <b>{{ strongCount }}</b>
      </div>
      <div class="metric">
        <span>监控品牌</span>
        <b>{{ brandName }}</b>
      </div>
      <div class="metric">
        <span>最近更新</span>
        <b>{{ latestTime }}</b>
      </div>
    </div>

    <div v-if="loading" class="panel pad">
      <el-skeleton :rows="4" animated />
    </div>

    <div v-else class="signal-list">
      <article v-for="item in signals" :key="item.id" class="signal-card panel pad">
        <header>
          <div class="signal-head-main">
            <div>
              <span class="eyebrow">{{ item.signalType }}</span>
              <h3>{{ item.categoryName }}</h3>
            </div>
            <div class="signal-inline-meta">
              <span>{{ item.platform }}</span>
              <span>{{ item.metric }}</span>
              <span>{{ item.discoveredAt }}</span>
            </div>
          </div>
          <div class="score-box">
            <span>机会分</span>
            <b>{{ item.score }}</b>
            <el-tag :type="strengthType(item.strength)" size="small">{{ item.strength }}</el-tag>
          </div>
        </header>
        <div class="signal-decision-banner">
          <div>
            <span>当前判断</span>
            <b>{{ item.decision }}</b>
          </div>
          <small>{{ item.recommendedAction }}</small>
        </div>
        <div class="signal-brief">
          <div>
            <span>置信度</span>
            <b>{{ item.confidence }}%</b>
          </div>
          <div>
            <span>决策口径</span>
            <b>{{ item.decision }}</b>
          </div>
          <div>
            <span>推荐动作</span>
            <b>{{ item.recommendedAction }}</b>
          </div>
          <div>
            <span>品牌匹配度</span>
            <b>{{ item.brandFitDetail?.overallFitLevel ?? '待判断' }}</b>
          </div>
        </div>
        <div v-if="item.reasonTags.length" class="reason-tags">
          <el-tag v-for="tag in item.reasonTags" :key="tag" size="small" effect="plain">{{ tag }}</el-tag>
        </div>
        <div v-if="item.brandFitDetail" class="fit-box">
          <span class="eyebrow">品牌适配说明</span>
          <p>{{ item.brandFitDetail.summary }}</p>
        </div>
        <div class="analysis-grid">
          <div class="analysis-card">
            <h4>推荐依据</h4>
            <ul v-if="item.reasons.length">
              <li v-for="reason in item.reasons.slice(0, 3)" :key="`${item.id}-${reason.title}`">
                <b>{{ reason.title }}</b>
                <span>{{ reason.description }}</span>
              </li>
            </ul>
            <p v-else>当前信号主要基于实时波动，等待更多交叉验证数据。</p>
          </div>
          <div class="analysis-card">
            <h4>主要风险</h4>
            <ul v-if="item.risks.length">
              <li v-for="risk in item.risks.slice(0, 2)" :key="`${item.id}-${risk.title}`">
                <b>{{ risk.title }}</b>
                <span>{{ risk.description }}</span>
              </li>
            </ul>
            <p v-else>当前暂无明显风险项。</p>
          </div>
        </div>
        <div v-if="item.mismatches.length" class="mismatch-box">
          <span class="eyebrow">当前约束拦截</span>
          <ul>
            <li v-for="risk in item.mismatches" :key="`${item.id}-${risk.type}`">{{ risk.message }}</li>
          </ul>
        </div>
        <p class="summary-text">{{ item.summary }}</p>
        <div v-if="item.cardId" class="card-actions">
          <div class="action-label">
            <span>后续动作</span>
            <small>{{ item.cardId ? '已关联机会分析' : '等待关联机会' }}</small>
          </div>
          <div class="action-buttons">
            <el-button type="primary" link @click="openOpportunity(item.cardId!)">查看爆品机会 →</el-button>
            <el-button
              link
              :type="isTracked(item) ? 'success' : undefined"
              :loading="trackingSignalId === item.id"
              @click="trackCompetitor(item)"
            >
              {{ isTracked(item) ? '已在竞品跟踪' : '加入竞品跟踪' }}
            </el-button>
            <el-button link @click="openInsight(item.categoryName)">回到洞察</el-button>
          </div>
        </div>
      </article>
    </div>

    <div v-if="!loading && signals.length === 0" class="panel pad">
      <el-result icon="info" title="暂无可见信号" sub-title="当前品牌约束下没有匹配信号，请调整排除品类或预算。">
        <template #extra>
          <el-button type="primary" @click="router.push('/data-prep')">返回数据准备</el-button>
        </template>
      </el-result>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { TrendCharts } from '@element-plus/icons-vue'
import { api } from '@/api'
import { getBrandId, setBrandId } from '@/composables/useBrandContext'
import { getApiErrorMessage } from '@/lib/apiError'
import { ElMessage } from 'element-plus'
import type { CompetitorShop, PushDeliveryRecord, SignalItem, WorkflowProgress } from '@/types'
import PageHero from '@/components/common/PageHero.vue'
import WorkflowSummary from '@/components/common/WorkflowSummary.vue'

const route = useRoute()
const router = useRouter()
const signals = ref<SignalItem[]>([])
const brandName = ref('-')
const loading = ref(false)
const workflow = ref<WorkflowProgress>()
const trackingSignalId = ref('')
const competitors = ref<CompetitorShop[]>([])
const pushing = ref(false)
const pushForm = ref({ channelType: '钉钉', webhookUrl: '', enabled: true })
const deliveries = ref<PushDeliveryRecord[]>([])

const demoWebhookByChannel: Record<string, string> = {
  钉钉: 'https://oapi.dingtalk.com/robot/send?access_token=demo-dingtalk-token',
  企业微信: 'https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=demo-wecom-key',
  微信群: 'https://example.com/webhook/demo-wechat-group'
}

const brandId = computed(() => {
  const queryId = Number(route.query.brandId)
  if (Number.isFinite(queryId) && queryId > 0) {
    setBrandId(queryId)
    return queryId
  }
  return getBrandId()
})

const strongCount = computed(() => signals.value.filter((item) => item.strength === '强').length)
const latestTime = computed(() => signals.value[0]?.discoveredAt ?? '-')
const leadSignal = computed(() => signals.value[0])
const activePlatforms = computed(() => [...new Set(signals.value.map((item) => item.platform))])
const activePlatformCount = computed(() => activePlatforms.value.length)
const activePlatformText = computed(() => activePlatforms.value.join(' / ') || '当前暂无平台数据')
const trackableCount = computed(() => signals.value.filter((item) => Boolean(item.cardId)).length)
const leadReasonTitle = computed(() => leadSignal.value?.reasons[0]?.title ?? '等待更多证据')
const leadRiskTitle = computed(() => leadSignal.value?.risks[0]?.title ?? '当前暂无显性风险')

async function load() {
  loading.value = true
  try {
    const [signalRows, dashboard, workflowData, competitorRows] = await Promise.all([
      api.getSignals(brandId.value),
      api.getDashboard(brandId.value),
      api.getWorkflow(brandId.value),
      api.getCompetitors(brandId.value)
    ])
    signals.value = signalRows
    brandName.value = dashboard.brandName
    workflow.value = workflowData
    competitors.value = competitorRows
    const configs = await api.getPushConfig(brandId.value)
    const current = configs.find((item) => item.channelType === pushForm.value.channelType)
    if (current) {
      pushForm.value = { channelType: current.channelType, webhookUrl: current.webhookUrl, enabled: current.enabled }
    }
    deliveries.value = await api.getPushDeliveries(brandId.value, 8)
  } finally {
    loading.value = false
  }
}

async function savePushConfig() {
  await api.savePushConfig(brandId.value, pushForm.value)
  ElMessage.success('推送配置已保存')
}

function fillDemoWebhook() {
  pushForm.value.webhookUrl = demoWebhookByChannel[pushForm.value.channelType] ?? demoWebhookByChannel['钉钉']
  ElMessage.info('已填入演示 Webhook，保存后推送将模拟外发成功')
}

async function sendDigest() {
  pushing.value = true
  try {
    const result = await api.pushDigest(brandId.value)
    const detail = result.channelResults?.join('；') ?? ''
    ElMessage[result.success ? 'success' : 'warning'](detail ? `${result.message}（${detail}）` : result.message)
    deliveries.value = result.deliveries?.length
      ? result.deliveries
      : await api.getPushDeliveries(brandId.value, 8)
  } catch (error) {
    ElMessage.error(getApiErrorMessage(error, '推送失败'))
  } finally {
    pushing.value = false
  }
}

function strengthType(strength: string) {
  if (strength === '强') return 'danger'
  if (strength === '中') return 'warning'
  return 'info'
}

function openOpportunity(cardId: number) {
  const signal = signals.value.find((item) => item.cardId === cardId)
  router.push({
    path: `/opportunity/${cardId}`,
    query: {
      brandId: brandId.value,
      ...(signal?.platform ? { platform: signal.platform } : {})
    }
  })
}

function openInsight(category: string) {
  const signal = signals.value.find((item) => item.categoryName === category)
  router.push({
    path: '/insight',
    query: {
      brandId: brandId.value,
      category,
      ...(signal?.platform ? { platform: signal.platform } : {})
    }
  })
}

async function trackCompetitor(item: SignalItem) {
  if (isTracked(item)) {
    ElMessage.info('该信号对应对象已在竞品跟踪中')
    router.push({
      path: '/competitor',
      query: {
        brandId: brandId.value,
        category: item.categoryName,
        platform: item.platform,
        insightPlatform: item.platform,
        source: item.signalType
      }
    })
    return
  }
  trackingSignalId.value = item.id
  try {
    await api.addCompetitor(brandId.value, {
      shopName: `${item.categoryName}${item.platform}观察店`,
      platform: item.platform,
      focusCategory: item.categoryName,
      cardId: item.cardId,
      sourceSignalId: item.id,
      sourceSignalType: item.signalType
    })
    ElMessage.success('已加入竞品跟踪')
    await load()
    router.push({
      path: '/competitor',
      query: {
        brandId: brandId.value,
        category: item.categoryName,
        platform: item.platform,
        insightPlatform: item.platform,
        source: item.signalType
      }
    })
  } catch (error) {
    ElMessage.error(getApiErrorMessage(error, '加入竞品跟踪失败，请稍后重试'))
  } finally {
    trackingSignalId.value = ''
  }
}

function isTracked(item: SignalItem) {
  return competitors.value.some((shop) => {
    const sameCategory = shop.focusCategory === item.categoryName
    const samePlatform = shop.platform === item.platform
    const sameSource = shop.sourceSignalId === item.id || shop.sourceSignalType === item.signalType
    return sameCategory && samePlatform && sameSource
  })
}

onMounted(load)
watch(brandId, load)
</script>

<style scoped>
.signal-list {
  display: grid;
  gap: 14px;
}

.signal-card {
  position: relative;
  overflow: hidden;
}

.signal-card::before {
  content: '';
  position: absolute;
  inset: 0 auto 0 0;
  width: 3px;
  background: linear-gradient(180deg, rgba(37, 99, 235, 0.74), rgba(15, 118, 110, 0.22));
}

.radar-hero {
  margin-bottom: 16px;
  background:
    linear-gradient(135deg, rgba(239, 246, 255, 0.92), rgba(240, 253, 250, 0.8)),
    #ffffff;
}

.radar-hero-main {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(320px, 0.9fr);
  gap: 18px;
}

.radar-copy small,
.context-card span,
.context-card small {
  display: block;
}

.radar-copy h2 {
  margin: 10px 0 0;
  color: var(--ink-strong);
  font-size: 30px;
  line-height: 1.3;
}

.radar-copy p {
  margin: 12px 0 0;
  max-width: 760px;
  color: var(--muted);
  line-height: 1.8;
}

.radar-copy small {
  margin-top: 10px;
  color: var(--muted);
  line-height: 1.6;
}

.push-panel {
  margin-bottom: 14px;
}

.push-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
  margin-bottom: 12px;
}

.push-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
}

.delivery-log {
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px solid rgba(148, 163, 184, 0.2);
}

.delivery-log ul {
  list-style: none;
  padding: 0;
  margin: 8px 0 0;
  display: grid;
  gap: 10px;
}

.delivery-log li {
  padding: 10px;
  border-radius: 10px;
  background: rgba(248, 251, 255, 0.92);
}

.delivery-log small,
.delivery-log p,
.delivery-log code {
  display: block;
  margin-top: 4px;
  color: var(--muted);
  font-size: 12px;
  word-break: break-all;
}

.push-form .el-input {
  flex: 1;
  min-width: 240px;
}

.radar-context {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.context-card {
  padding: 14px 16px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: var(--shadow-sm);
}

.context-card span {
  color: var(--muted);
  font-size: 12px;
}

.context-card b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 18px;
  line-height: 1.5;
}

.context-card small {
  margin-top: 10px;
  color: var(--muted);
  line-height: 1.6;
}

.signal-card header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
  padding-bottom: 14px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.14);
}

.signal-decision-banner {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
  margin-top: 14px;
  padding: 14px 16px;
  border: 1px solid rgba(37, 99, 235, 0.14);
  border-radius: 14px;
  background: linear-gradient(180deg, rgba(239, 246, 255, 0.84), rgba(247, 250, 255, 0.94));
}

.signal-decision-banner span,
.signal-decision-banner small {
  display: block;
}

.signal-decision-banner span {
  color: var(--muted);
  font-size: 12px;
}

.signal-decision-banner b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 16px;
  line-height: 1.5;
}

.signal-decision-banner small {
  max-width: 220px;
  color: var(--muted);
  line-height: 1.7;
  text-align: right;
}

.signal-head-main {
  display: grid;
  gap: 12px;
}

.signal-inline-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.signal-inline-meta span {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: rgba(248, 250, 252, 0.96);
  border: 1px solid rgba(148, 163, 184, 0.14);
  color: var(--muted);
  font-family: "IBM Plex Mono", monospace;
  font-size: 12px;
}

.eyebrow {
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
}

h3 {
  margin: 8px 0 0;
  color: var(--ink-strong);
}

.score-box {
  display: grid;
  justify-items: end;
  gap: 8px;
  min-width: 96px;
  text-align: right;
}

.score-box span {
  color: var(--muted);
  font-size: 12px;
}

.score-box b {
  display: block;
  color: var(--accent);
  font-family: "IBM Plex Mono", monospace;
  font-size: 28px;
}

.signal-brief {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-top: 14px;
  padding: 14px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 251, 255, 0.96)),
    #ffffff;
}

.signal-brief span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.signal-brief b {
  display: block;
  margin-top: 6px;
  color: var(--ink-strong);
  font-size: 14px;
}

.reason-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.fit-box,
.mismatch-box {
  margin-bottom: 12px;
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 251, 255, 0.96)),
    #ffffff;
}

.fit-box p,
.mismatch-box ul {
  margin: 8px 0 0;
}

.mismatch-box ul {
  padding-left: 18px;
  color: var(--ink);
  line-height: 1.7;
}

.analysis-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 12px;
}

.analysis-card {
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 251, 255, 0.96)),
    #ffffff;
}

.analysis-card h4 {
  margin: 0;
  color: var(--ink-strong);
  font-size: 14px;
}

.analysis-card ul {
  margin: 10px 0 0;
  padding-left: 18px;
  color: var(--ink);
  line-height: 1.7;
}

.analysis-card li + li {
  margin-top: 8px;
}

.analysis-card b,
.analysis-card span {
  display: block;
}

.analysis-card span {
  color: var(--muted);
  font-size: 13px;
}

p {
  margin: 0;
  line-height: 1.75;
  color: var(--muted);
}

.summary-text {
  margin-top: 2px;
}

.card-actions {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px solid var(--line);
}

.action-label span,
.action-label small {
  display: block;
}

.action-label span {
  color: var(--muted);
  font-size: 12px;
}

.action-label small {
  margin-top: 6px;
  color: var(--muted);
  line-height: 1.6;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: flex-end;
}

@media (max-width: 1280px) {
  .signal-decision-banner small {
    max-width: none;
    text-align: left;
  }
}

@media (max-width: 760px) {
  .radar-hero-main,
  .radar-context,
  .signal-brief,
  .analysis-grid,
  .signal-decision-banner {
    grid-template-columns: 1fr;
  }

  .card-actions {
    display: grid;
  }

  .action-buttons {
    justify-content: flex-start;
  }

  .signal-decision-banner small {
    max-width: none;
    text-align: left;
  }
}
</style>
