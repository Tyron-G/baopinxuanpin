<template>
  <div class="panel pad chart-panel">
    <div class="section-title chart-head">
      <div>
        <h2>机会在哪里</h2>
        <p>近 6 个月搜索热度与销量趋势</p>
      </div>
      <div class="chart-toolbar-card">
        <span>平台视角</span>
        <b>{{ activePlatform }}</b>
        <small>切换后会同步重算领涨类目和图表序列。</small>
        <el-segmented v-model="activePlatform" :options="platformOptions" />
      </div>
    </div>
    <div class="chart-ribbon">
      <article>
        <span>当前领涨</span>
        <b>{{ leadCategory?.categoryName ?? '暂无类目' }}</b>
        <small>{{ leadCategory ? `最近月增速 ${leadCategory.growthRate}%` : '等待趋势数据' }}</small>
      </article>
      <article>
        <span>高增长类目</span>
        <b>{{ fastGrowthCount }}</b>
        <small>最近月增速高于 20% 的类目数量</small>
      </article>
      <article>
        <span>最高销量类目</span>
        <b>{{ topSalesCategory?.categoryName ?? '暂无类目' }}</b>
        <small>{{ topSalesCategory ? `最近销量 ${topSalesCategory.salesVolume}` : '等待销量数据' }}</small>
      </article>
      <article>
        <span>判读重点</span>
        <b>先看增速，再看销量跟随</b>
        <small>更适合判断“热度是否正在转成成交”。</small>
      </article>
    </div>
    <div class="chart-brief">
      <article>
        <span>当前平台</span>
        <b>{{ activePlatform }}</b>
      </article>
      <article>
        <span>观察类目</span>
        <b>{{ categories.length }}</b>
      </article>
      <article>
        <span>趋势摘要</span>
        <b>{{ platformSummary }}</b>
      </article>
    </div>
    <div ref="chartRef" class="chart-box chart-surface"></div>
  </div>
</template>

<script setup lang="ts">
import { echarts } from '@/lib/echarts'
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import type { CategoryTrend } from '@/types'

const props = defineProps<{ rows: CategoryTrend[] }>()
const chartRef = ref<HTMLDivElement>()
const activePlatform = ref('全平台')
let chart: echarts.ECharts | null = null
let resizeObserver: ResizeObserver | null = null

const platformOptions = computed(() => [...new Set(props.rows.map((item) => item.platform))])
const filteredRows = computed(() => {
  const matched = props.rows.filter((item) => item.platform === activePlatform.value)
  return matched.length ? matched : props.rows.filter((item) => item.platform === '全平台')
})
const months = computed(() => [...new Set(filteredRows.value.map((item) => item.trendMonth))])
const categories = computed(() => [...new Set(filteredRows.value.map((item) => item.categoryName))])
const latestRows = computed(() =>
  categories.value.map((category) =>
    [...filteredRows.value
      .filter((item) => item.categoryName === category)
      .sort((left, right) => left.trendMonth.localeCompare(right.trendMonth))].pop()
  ).filter(Boolean) as CategoryTrend[]
)
const leadCategory = computed(() => [...latestRows.value].sort((left, right) => right.growthRate - left.growthRate)[0])
const topSalesCategory = computed(() => [...latestRows.value].sort((left, right) => right.salesVolume - left.salesVolume)[0])
const fastGrowthCount = computed(() => latestRows.value.filter((item) => item.growthRate >= 20).length)
const platformSummary = computed(() => {
  if (!latestRows.value.length) return '暂无平台趋势数据'
  const best = leadCategory.value
  return `${activePlatform.value} 当前由 ${best.categoryName} 领涨，最近月增速 ${best.growthRate}%`
})

function resize() {
  requestAnimationFrame(() => chart?.resize())
}

function render() {
  if (!chartRef.value) return
  chart ||= echarts.init(chartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: {
      type: 'scroll',
      top: 8,
      left: 18,
      right: 18,
      itemWidth: 12,
      itemHeight: 12,
      itemGap: 14,
      pageIconColor: '#5b8def',
      pageIconInactiveColor: '#c8d4e3',
      pageTextStyle: { color: '#7b8798' },
      textStyle: {
        color: '#5f6f86',
        fontSize: 12,
        lineHeight: 18
      }
    },
    grid: { left: 64, right: 68, top: 104, bottom: 42, containLabel: true },
    title: {
      text: platformSummary.value,
      top: 48,
      left: 18,
      textStyle: {
        color: '#6b7a90',
        fontSize: 12,
        fontWeight: 500
      }
    },
    xAxis: {
      type: 'category',
      data: months.value,
      axisLabel: { color: '#7b8798' },
      axisLine: { lineStyle: { color: '#d5dee9' } }
    },
    yAxis: [
      {
        type: 'value',
        name: '搜索热度',
        nameLocation: 'end',
        nameGap: 18,
        nameTextStyle: { color: '#7b8798', align: 'left' },
        axisLabel: { color: '#7b8798', formatter: (value: number) => `${Math.round(value / 1000)}k` },
        splitLine: { lineStyle: { color: '#e6edf5' } }
      },
      {
        type: 'value',
        name: '销量',
        nameLocation: 'end',
        nameGap: 18,
        nameTextStyle: { color: '#7b8798', align: 'right' },
        splitLine: { show: false },
        axisLabel: { color: '#7b8798', formatter: (value: number) => `${Math.round(value / 1000)}k` }
      }
    ],
    series: [
      ...categories.value.map((category) => ({
        name: `${category} 搜索`,
        type: 'line',
        smooth: true,
        symbolSize: 8,
        lineStyle: { width: 3 },
        data: months.value.map((m) => filteredRows.value.find((r) => r.categoryName === category && r.trendMonth === m)?.searchVolume ?? 0)
      })),
      ...categories.value.map((category) => ({
        name: `${category} 销量`,
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        lineStyle: { type: 'dashed' },
        symbolSize: 6,
        data: months.value.map((m) => filteredRows.value.find((r) => r.categoryName === category && r.trendMonth === m)?.salesVolume ?? 0)
      }))
    ]
  })
  resize()
}

onMounted(() => {
  nextTick(render)
  if (chartRef.value) {
    resizeObserver = new ResizeObserver(resize)
    resizeObserver.observe(chartRef.value)
  }
  window.addEventListener('resize', resize)
})

onBeforeUnmount(() => {
  resizeObserver?.disconnect()
  window.removeEventListener('resize', resize)
  chart?.dispose()
})

watch(() => props.rows, () => {
  if (!platformOptions.value.includes(activePlatform.value)) {
    activePlatform.value = '全平台'
  }
  nextTick(render)
}, { deep: true })
watch(activePlatform, () => nextTick(render))
defineExpose({ resize })
</script>

<style scoped>
.chart-panel {
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.99), rgba(248, 251, 255, 0.98)),
    #ffffff;
}

.chart-surface {
  min-height: 388px;
}

.chart-head {
  margin-bottom: 14px;
}

.chart-toolbar-card {
  min-width: 260px;
  max-width: 300px;
  padding: 14px 16px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(252, 253, 255, 0.98), rgba(246, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.chart-toolbar-card span,
.chart-toolbar-card small {
  display: block;
}

.chart-toolbar-card span {
  color: var(--muted);
  font-size: 12px;
}

.chart-toolbar-card b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 16px;
}

.chart-toolbar-card small {
  margin-top: 8px;
  color: var(--muted);
  line-height: 1.6;
}

.chart-toolbar-card :deep(.el-segmented) {
  margin-top: 12px;
}

.chart-ribbon {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 12px;
}

.chart-ribbon article,
.chart-brief article {
  padding: 12px 14px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.84);
}

.chart-ribbon span,
.chart-ribbon small,
.chart-brief span {
  display: block;
}

.chart-ribbon span,
.chart-brief span {
  color: var(--muted);
  font-size: 12px;
}

.chart-ribbon b,
.chart-brief b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  line-height: 1.6;
}

.chart-ribbon b {
  font-family: "IBM Plex Mono", monospace;
}

.chart-ribbon small {
  margin-top: 8px;
  color: var(--muted);
  line-height: 1.6;
}

.chart-brief {
  display: grid;
  grid-template-columns: 160px 160px minmax(0, 1fr);
  gap: 12px;
  margin-bottom: 12px;
}

h2 {
  margin: 0;
  font-size: 18px;
}

p {
  margin: 4px 0 0;
  color: var(--muted);
}

@media (max-width: 900px) {
  .chart-ribbon,
  .chart-brief {
    grid-template-columns: 1fr;
  }

  .chart-toolbar-card {
    min-width: 0;
    max-width: none;
  }
}
</style>
