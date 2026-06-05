<template>
  <div class="panel pad chart-panel">
    <div class="section-title chart-head">
      <div>
        <h2>供需缺口地</h2>
        <p>{{ chartMode === 'prd' ? '单柱搜索量，柱色深浅表示供给稀缺度（PRD）' : '价格带搜索量与供给数量双柱对照' }}</p>
      </div>
      <div class="chart-toolbar-card">
        <span>平台视角</span>
        <b>{{ activePlatform }}</b>
        <small>切换后会同步重算价格带缺口和供给稀缺度。</small>
        <el-segmented v-if="!lockedPlatform" v-model="internalPlatform" :options="platformOptions" />
        <span class="chart-mode-label">图表模式</span>
        <el-segmented v-model="chartMode" :options="chartModeOptions" />
      </div>
    </div>
    <div class="chart-ribbon">
      <article>
        <span>最大缺口</span>
        <b>{{ bestGap?.categoryName ?? '暂无类目' }}</b>
        <small>{{ bestGap ? `${bestGap.priceRange} · 缺口比 ${bestGap.demandSupplyRatio}` : '等待缺口数据' }}</small>
      </article>
      <article>
        <span>最高搜索量</span>
        <b>{{ highestSearchGap?.categoryName ?? '暂无类目' }}</b>
        <small>{{ highestSearchGap ? `${highestSearchGap.priceRange} · 搜索量 ${highestSearchGap.searchVolume}` : '等待搜索量数据' }}</small>
      </article>
      <article>
        <span>高缺口样本</span>
        <b>{{ highGapCount }}</b>
        <small>缺口比高于 30 的价格带数量</small>
      </article>
      <article>
        <span>判读重点</span>
        <b>看搜索量是否明显高于供给数</b>
        <small>避免把低供给但也低需求的带宽误判成机会。</small>
      </article>
    </div>
    <div class="chart-brief">
      <article>
        <span>当前平台</span>
        <b>{{ activePlatform }}</b>
      </article>
      <article>
        <span>缺口样本</span>
        <b>{{ filteredRows.length }}</b>
      </article>
      <article>
        <span>缺口摘要</span>
        <b>{{ platformSummary }}</b>
      </article>
    </div>
    <div ref="chartRef" class="chart-box chart-surface"></div>
  </div>
</template>

<script setup lang="ts">
import { echarts } from '@/lib/echarts'
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import type { SupplyDemand } from '@/types'

const props = defineProps<{ rows: SupplyDemand[]; platform?: string }>()
const chartRef = ref<HTMLDivElement>()
const internalPlatform = ref('全平台')
const activePlatform = computed(() => props.platform ?? internalPlatform.value)
const lockedPlatform = computed(() => Boolean(props.platform))
const chartMode = ref<'prd' | 'dual'>('prd')
const chartModeOptions = [
  { label: 'PRD单柱', value: 'prd' },
  { label: '双柱对比', value: 'dual' }
]
let chart: echarts.ECharts | null = null
let resizeObserver: ResizeObserver | null = null

const platformOptions = computed(() => [...new Set(props.rows.map((item) => item.platform))])
const filteredRows = computed(() => {
  const matched = props.rows.filter((item) => item.platform === activePlatform.value)
  return matched.length ? matched : props.rows.filter((item) => item.platform === '全平台')
})
const bestGap = computed(() => [...filteredRows.value].sort((left, right) => right.demandSupplyRatio - left.demandSupplyRatio)[0])
const highestSearchGap = computed(() => [...filteredRows.value].sort((left, right) => right.searchVolume - left.searchVolume)[0])
const highGapCount = computed(() => filteredRows.value.filter((item) => item.demandSupplyRatio >= 30).length)
const platformSummary = computed(() => {
  if (!filteredRows.value.length) return '暂无供需缺口数据'
  return `${activePlatform.value} 当前在 ${bestGap.value.categoryName} 的 ${bestGap.value.priceRange} 存在最明显价格带缺口`
})

function resize() {
  requestAnimationFrame(() => chart?.resize())
}

function supplyColor(supplyCount: number) {
  const maxSupply = Math.max(...filteredRows.value.map((item) => item.supplyCount), 1)
  const scarcity = 1 - supplyCount / maxSupply
  const lightness = Math.round(28 + scarcity * 42)
  return `hsl(168, 62%, ${lightness}%)`
}

function render() {
  if (!chartRef.value) return
  chart ||= echarts.init(chartRef.value)
  const categories = filteredRows.value.map((item) => `${item.categoryName}\n${item.priceRange}`)
  const isPrd = chartMode.value === 'prd'
  chart.setOption({
    title: {
      text: platformSummary.value,
      top: 14,
      left: 18,
      textStyle: {
        color: '#6b7a90',
        fontSize: 12,
        fontWeight: 500
      }
    },
    tooltip: {
      trigger: 'axis',
      formatter: isPrd
        ? (params: { dataIndex: number; value: number }[]) => {
            const index = params[0]?.dataIndex ?? 0
            const row = filteredRows.value[index]
            if (!row) return ''
            return `${row.categoryName} ${row.priceRange}<br/>搜索量 ${row.searchVolume}<br/>供给 ${row.supplyCount}（色越深供给越少）`
          }
        : undefined
    },
    legend: { top: 26, textStyle: { color: '#5f6f86' }, show: !isPrd },
    grid: { left: 66, right: 62, top: 54, bottom: 88, containLabel: true },
    xAxis: {
      type: 'category',
      data: categories,
      axisLabel: { color: '#7b8798', interval: 0, width: 92, overflow: 'break' },
      axisLine: { lineStyle: { color: '#d5dee9' } }
    },
    yAxis: isPrd
      ? {
          type: 'value',
          name: '搜索量',
          axisLabel: { color: '#7b8798', formatter: (value: number) => `${Math.round(value / 1000)}k` },
          splitLine: { lineStyle: { color: '#e6edf5' } }
        }
      : [
          {
            type: 'value',
            name: '搜索量',
            axisLabel: { color: '#7b8798', formatter: (value: number) => `${Math.round(value / 1000)}k` },
            splitLine: { lineStyle: { color: '#e6edf5' } }
          },
          {
            type: 'value',
            name: '供给数',
            axisLabel: { color: '#7b8798' }
          }
        ],
    series: isPrd
      ? [
          {
            name: '搜索量',
            type: 'bar',
            data: filteredRows.value.map((item) => ({
              value: item.searchVolume,
              supplyCount: item.supplyCount,
              itemStyle: {
                color: supplyColor(item.supplyCount),
                borderRadius: [6, 6, 0, 0]
              }
            })),
            tooltip: {
              valueFormatter: (value: number) => `${value}`
            }
          }
        ]
      : [
          {
            name: '搜索量',
            type: 'bar',
            data: filteredRows.value.map((item) => item.searchVolume),
            itemStyle: { color: '#0f766e', borderRadius: [6, 6, 0, 0] }
          },
          {
            name: '供给数量',
            type: 'bar',
            yAxisIndex: 1,
            data: filteredRows.value.map((item) => item.supplyCount),
            itemStyle: { color: '#d97706', borderRadius: [6, 6, 0, 0] }
          }
        ]
  }, true)
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
  if (!lockedPlatform.value && !platformOptions.value.includes(internalPlatform.value)) {
    internalPlatform.value = '全平台'
  }
  nextTick(render)
}, { deep: true })
watch([activePlatform, () => props.rows], () => nextTick(render), { deep: true })
watch(chartMode, () => nextTick(render))
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

.chart-mode-label {
  display: block;
  margin-top: 12px;
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
