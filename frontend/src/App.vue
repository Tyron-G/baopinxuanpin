<template>
  <ElConfigProvider>
    <div class="app-shell">
      <aside class="side-panel">
        <div class="brand-lockup">
          <span class="eyebrow">爆品选品雷达</span>
          <h1>{{ dashboard?.brandName ?? '壹沓生活' }}</h1>
          <p>从品牌约束到信号、洞察、机会与报告的完整选品链路。</p>
        </div>

        <nav class="side-nav" aria-label="主导航">
          <RouterLink v-for="item in navItems" :key="item.path" :to="item.to">
            <span>{{ item.label }}</span>
            <small>{{ item.hint }}</small>
          </RouterLink>
        </nav>

        <section v-if="dashboard" class="side-kpi">
          <article>
            <span>监控赛道</span>
            <b>{{ dashboard.monitoredCategories }}</b>
            <small>当前品牌可见品类数</small>
          </article>
          <article>
            <span>活跃信号</span>
            <b>{{ dashboard.activeSignals }}</b>
            <small>今日雷达摘要</small>
          </article>
          <article>
            <span>最佳机会分</span>
            <b>{{ dashboard.bestScore }}</b>
            <small>{{ dashboard.topCategory }}</small>
          </article>
        </section>

        <section v-if="workspaces.length > 1" class="workspace-switch panel pad">
          <span class="eyebrow">品牌工作区</span>
          <el-select
            :model-value="brandId"
            size="small"
            style="width: 100%; margin-top: 6px"
            @change="switchWorkspace"
          >
            <el-option
              v-for="item in workspaces"
              :key="item.brandId"
              :label="item.brandName"
              :value="item.brandId"
            />
          </el-select>
        </section>

        <section v-if="corePromise" class="core-promise-mini panel pad">
          <span class="eyebrow">核心承诺（推算）</span>
          <p>{{ corePromise.narrative }}</p>
        </section>

        <section v-if="productMetrics" class="product-metrics-mini">
          <span class="eyebrow">{{ productMetrics.demoData ? '运营 KPI（样例）' : '运营 KPI（应用内推算）' }}</span>
          <article v-for="item in productMetrics.metrics.slice(0, 3)" :key="item.key">
            <span>{{ item.label }}</span>
            <b>{{ item.actualValue }}</b>
          </article>
        </section>
      </aside>

      <main class="workspace">
        <WorkflowStepper
          :current="currentStep"
          :best-card-id="stepperBestCardId"
          :best-category-name="stepperBestCategory"
          :best-score="stepperBestScore"
          :workflow="workflow"
        />
        <RouterView />
      </main>
    </div>
  </ElConfigProvider>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '@/api'
import { getBrandId, setBrandId } from '@/composables/useBrandContext'
import WorkflowStepper from '@/components/WorkflowStepper.vue'
import type { BrandWorkspaceItem, CorePromiseMetrics, DashboardSummary, ProductMetricsKpi, WorkflowProgress } from '@/types'

const route = useRoute()
const router = useRouter()
const dashboard = ref<DashboardSummary>()
const workflow = ref<WorkflowProgress>()
const productMetrics = ref<ProductMetricsKpi>()
const corePromise = ref<CorePromiseMetrics>()
const workspaces = ref<BrandWorkspaceItem[]>([])

const brandId = computed(() => {
  const queryId = Number(route.query.brandId)
  if (Number.isFinite(queryId) && queryId > 0) {
    return queryId
  }
  return getBrandId()
})

const currentStep = computed(() => {
  const path = route.path
  if (path.startsWith('/data-prep')) return 'data-prep'
  if (path.startsWith('/radar')) return 'radar'
  if (path.startsWith('/insight')) return 'insight'
  if (path.startsWith('/ranking')) return 'ranking'
  if (path.startsWith('/opportunity')) return 'opportunity'
  if (path.startsWith('/report')) return 'report'
  return 'data-prep'
})

const navItems = computed(() => [
  { label: '数据准备', hint: '建档', path: '/data-prep', to: { path: '/data-prep', query: { brandId: brandId.value } } },
  { label: '信号雷达', hint: '每日扫信号', path: '/radar', to: { path: '/radar', query: { brandId: brandId.value } } },
  { label: '洞察发现', hint: '三维度筛选', path: '/insight', to: { path: '/insight', query: { brandId: brandId.value } } },
  { label: '机会榜单', hint: 'TOP50', path: '/ranking', to: { path: '/ranking', query: { brandId: brandId.value } } },
  { label: '竞品监控', hint: '对标跟踪', path: '/competitor', to: { path: '/competitor', query: { brandId: brandId.value } } },
  { label: '移动看板', hint: 'H5', path: '/mobile', to: { path: '/mobile', query: { brandId: brandId.value } } },
  { label: '测款优化', hint: '第7天复盘', path: '/test-run', to: { path: '/test-run', query: { brandId: brandId.value } } },
  { label: '开放 API', hint: '联调控制台', path: '/open-api', to: { path: '/open-api', query: { brandId: brandId.value } } },
  { label: '归因报告', hint: '迭代2', path: '/attribution', to: { path: '/attribution', query: { brandId: brandId.value } } },
  { label: '品牌模型', hint: '专属权重', path: '/brand-model', to: { path: '/brand-model', query: { brandId: brandId.value } } },
  { label: '供应链匹配', hint: 'P2', path: '/supply-match', to: { path: '/supply-match', query: { brandId: brandId.value } } },
  { label: '团队协作', hint: 'P2', path: '/team', to: { path: '/team', query: { brandId: brandId.value } } }
])

const shellPlatform = computed(() => {
  const raw = route.query.platform
  return typeof raw === 'string' && raw.trim() ? raw : '全平台'
})

const flowCardId = computed(() => {
  const match = route.path.match(/^\/(?:report|opportunity)\/(\d+)/)
  return match ? Number(match[1]) : null
})

const flowCategoryName = ref<string>()
const flowBestScore = ref<number>()

const stepperBestCardId = computed(() => flowCardId.value ?? dashboard.value?.bestCardId ?? null)
const stepperBestCategory = computed(() => {
  const flowName = flowCategoryName.value?.trim()
  if (flowName) return flowName
  return dashboard.value?.topCategory
})
const stepperBestScore = computed(() => flowBestScore.value ?? dashboard.value?.bestScore)

async function resolveFlowContext() {
  const cardId = flowCardId.value
  if (!cardId) {
    flowCategoryName.value = undefined
    flowBestScore.value = undefined
    return
  }
  if (dashboard.value?.bestCardId === cardId && dashboard.value?.topCategory) {
    flowCategoryName.value = dashboard.value.topCategory
    flowBestScore.value = dashboard.value.bestScore
    return
  }
  try {
    const detail = await api.getOpportunity(cardId, brandId.value, shellPlatform.value)
    flowCategoryName.value = detail.insightCard.categoryName
    flowBestScore.value = detail.points?.[0]?.opportunityScore
  } catch {
    flowCategoryName.value = undefined
    flowBestScore.value = undefined
  }
}

async function loadShellData() {
  const id = brandId.value
  setBrandId(id)
  const platform = shellPlatform.value
  const results = await Promise.allSettled([
    api.getDashboard(id, platform),
    api.getWorkflow(id, platform),
    api.getProductMetrics(id),
    api.getCorePromiseMetrics(id),
    api.listBrandWorkspaces(id)
  ])
  if (results[0].status === 'fulfilled') dashboard.value = results[0].value
  if (results[1].status === 'fulfilled') workflow.value = results[1].value
  if (results[2].status === 'fulfilled') productMetrics.value = results[2].value
  if (results[3].status === 'fulfilled') corePromise.value = results[3].value
  if (results[4].status === 'fulfilled') workspaces.value = results[4].value
  await resolveFlowContext()
}

function switchWorkspace(nextId: number) {
  setBrandId(nextId)
  router.replace({ path: route.path, query: { ...route.query, brandId: nextId } })
  loadShellData()
}

onMounted(loadShellData)
watch(() => route.query.brandId, loadShellData)
watch(shellPlatform, loadShellData)
watch(() => route.path, () => {
  void resolveFlowContext()
})
</script>
