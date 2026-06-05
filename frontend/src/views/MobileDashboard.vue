<template>
  <section class="mobile-shell">
    <header class="mobile-header">
      <span>爆品选品雷达</span>
      <b>{{ dashboard?.brandName ?? '移动端看板' }}</b>
    </header>
    <div class="mobile-grid">
      <article><span>活跃信号</span><b>{{ dashboard?.activeSignals ?? 0 }}</b></article>
      <article><span>监控品类</span><b>{{ dashboard?.monitoredCategories ?? 0 }}</b></article>
      <article><span>最佳分数</span><b>{{ dashboard?.bestScore ?? '-' }}</b></article>
      <article><span>关注列表</span><b>{{ dashboard?.watchlistCount ?? 0 }}</b></article>
      <article><span>TOP50</span><b>{{ dashboard?.opportunityRankingTotal ?? 50 }}</b></article>
      <article><span>优先赛道</span><b class="small">{{ dashboard?.topCategory ?? '-' }}</b></article>
      <article v-if="kpi('nps_iter1')"><span>NPS（样例）</span><b>{{ kpi('nps_iter1') }}</b></article>
      <article v-if="kpi('retention')"><span>留存（样例）</span><b>{{ kpi('retention') }}</b></article>
      <article v-if="kpi('arr')"><span>ARR（样例）</span><b class="small">{{ kpi('arr') }}</b></article>
      <article v-if="kpi('enterprise')"><span>企业版（样例）</span><b>{{ kpi('enterprise') }}</b></article>
    </div>
    <nav class="mobile-nav">
      <RouterLink to="/radar">信号</RouterLink>
      <RouterLink to="/ranking">榜单</RouterLink>
      <RouterLink to="/insight">洞察</RouterLink>
      <RouterLink to="/competitor">竞品</RouterLink>
    </nav>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { api } from '@/api'
import { getBrandId } from '@/composables/useBrandContext'
import type { DashboardSummary, ProductMetricsKpi } from '@/types'

const dashboard = ref<DashboardSummary>()
const metrics = ref<ProductMetricsKpi>()

function kpi(key: string) {
  return metrics.value?.metrics.find((item) => item.key === key)?.actualValue
}

onMounted(async () => {
  const brandId = getBrandId()
  const [dash, kpiData] = await Promise.all([api.getDashboard(brandId), api.getProductMetrics()])
  dashboard.value = dash
  metrics.value = kpiData
})
</script>

<style scoped>
.mobile-shell {
  max-width: 480px;
  margin: 0 auto;
  padding: 16px;
}

.mobile-header b {
  display: block;
  font-size: 20px;
  margin-top: 4px;
}

.mobile-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  margin: 16px 0;
}

.mobile-grid article {
  padding: 12px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 8px 24px rgba(15, 35, 80, 0.06);
}

.mobile-grid b.small {
  font-size: 13px;
}

.mobile-nav {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
}

.mobile-nav a {
  text-align: center;
  padding: 10px 6px;
  border-radius: 10px;
  background: rgba(15, 98, 254, 0.08);
  color: var(--brand);
  text-decoration: none;
  font-size: 13px;
}
</style>
