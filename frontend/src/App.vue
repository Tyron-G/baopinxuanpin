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

        <section v-if="productMetrics" class="product-metrics-mini">
          <span class="eyebrow">运营 KPI（样例）</span>
          <article v-for="item in productMetrics.metrics.slice(0, 3)" :key="item.key">
            <span>{{ item.label }}</span>
            <b>{{ item.actualValue }}</b>
          </article>
        </section>
      </aside>

      <main class="workspace">
        <WorkflowStepper
          :current="currentStep"
          :best-card-id="dashboard?.bestCardId ?? 1"
          :workflow="workflow"
        />
        <RouterView />
      </main>
    </div>
  </ElConfigProvider>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { api } from '@/api'
import { getBrandId, setBrandId } from '@/composables/useBrandContext'
import WorkflowStepper from '@/components/WorkflowStepper.vue'
import type { DashboardSummary, ProductMetricsKpi, WorkflowProgress } from '@/types'

const route = useRoute()
const dashboard = ref<DashboardSummary>()
const workflow = ref<WorkflowProgress>()
const productMetrics = ref<ProductMetricsKpi>()

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
  if (path.startsWith('/insight') || path.startsWith('/ranking')) return 'insight'
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
  { label: '品牌模型', hint: '专属权重', path: '/brand-model', to: { path: '/brand-model', query: { brandId: brandId.value } } }
])

async function loadShellData() {
  const id = brandId.value
  setBrandId(id)
  const [dash, flow, metrics] = await Promise.all([
    api.getDashboard(id),
    api.getWorkflow(id),
    api.getProductMetrics()
  ])
  dashboard.value = dash
  workflow.value = flow
  productMetrics.value = metrics
}

onMounted(loadShellData)
watch(() => route.query.brandId, loadShellData)
</script>
