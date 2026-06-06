<template>
  <div class="panel pad cloud-panel">
    <div class="cloud-head">
      <div>
        <h2>舆情词云 × 情感象限</h2>
        <p>横轴为提及强度，纵轴区分正负向；优先处理高提及负向词。</p>
      </div>
      <span class="cloud-meta">{{ terms.length }} 个高频信号</span>
    </div>
    <div class="priority-banner" :class="{ 'priority-banner--calm': !priorityTerms.length }">
      <div class="priority-flag">
        <i class="priority-dot"></i>
        <span>{{ priorityTerms.length ? '高压负向词' : '负向压力' }}</span>
      </div>
      <div class="priority-copy">
        <b>{{ prioritySummaryTitle }}</b>
        <small>{{ prioritySummaryNote }}</small>
      </div>
      <div class="priority-metric">
        <span>高压词数量</span>
        <b>{{ priorityTerms.length }}</b>
        <small>{{ priorityMetricNote }}</small>
      </div>
    </div>
    <div ref="chartRef" class="chart-box"></div>
    <div class="legend-row">
      <span class="legend legend--negative">高提及 × 负向</span>
      <span class="legend legend--positive">高提及 × 正向</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { echarts } from '@/lib/echarts'
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import type { SentimentTerm } from '@/types'

const props = defineProps<{ terms: SentimentTerm[] }>()
const chartRef = ref<HTMLDivElement>()
let chart: echarts.ECharts | null = null
let resizeObserver: ResizeObserver | null = null
type SentimentPointData = [number, number, number, 'positive' | 'negative', string, string]
type SentimentPointParam = { data: SentimentPointData; dataIndex: number }

const priorityTerms = computed(() => {
  const maxValue = Math.max(...props.terms.map((item) => item.value), 1)
  const midValue = maxValue / 2
  return props.terms
    .filter((term) => term.sentiment === 'negative' && term.value >= midValue)
    .sort((left, right) => right.value - left.value)
})

const prioritySummaryTitle = computed(() => {
  if (!priorityTerms.value.length) {
    return '当前负向压力可控'
  }
  const topTerms = priorityTerms.value.slice(0, 2).map((term) => `${term.name} ${term.value}`)
  return `当前最该先处理：${topTerms.join(' / ')}`
})

const prioritySummaryNote = computed(() => {
  if (!priorityTerms.value.length) {
    return '当前没有落入高提及负向象限的词条，可继续保持正向卖点与评论承接。'
  }
  return '先处理右下高提及负向词，再回看是否已经转成售后、转化或内容表达上的明确问题。'
})

const priorityMetricNote = computed(() => {
  if (!priorityTerms.value.length) {
    return '继续观察评论波动'
  }
  if (priorityTerms.value.length === 1) {
    return '单点问题先闭环'
  }
  return '先看前两项高压词'
})

function labelPosition(sentiment: 'positive' | 'negative', value: number, pivot: number) {
  if (sentiment === 'positive') {
    return value >= pivot ? 'top' : 'right'
  }
  return value >= pivot ? 'bottom' : 'left'
}

function resize() {
  requestAnimationFrame(() => chart?.resize())
}

function render() {
  if (!chartRef.value || props.terms.length === 0) return
  chart ||= echarts.init(chartRef.value)

  const maxValue = Math.max(...props.terms.map((item) => item.value), 1)
  const midValue = maxValue / 2
  const highlightedPriorityTerms = props.terms.filter((term) => term.sentiment === 'negative' && term.value >= midValue)
  const normalTerms = props.terms.filter((term) => !(term.sentiment === 'negative' && term.value >= midValue))

  const toSeriesData = (terms: SentimentTerm[]): SentimentPointData[] => terms.map((term) => [
    term.value,
    term.sentiment === 'positive' ? 0.72 : -0.72,
    term.value,
    term.sentiment,
    term.name,
    labelPosition(term.sentiment, term.value, midValue)
  ])

  chart.setOption({
    tooltip: {
      formatter: (params: SentimentPointParam) => {
        const [, , value, sentiment, name] = params.data
        return `${name}<br/>提及强度：${value}<br/>情感：${sentiment === 'positive' ? '正向' : '负向'}`
      }
    },
    grid: { left: 78, right: 38, top: 34, bottom: 72, containLabel: false },
    xAxis: {
      name: '提及强度',
      nameLocation: 'middle',
      nameGap: 42,
      nameTextStyle: {
        color: '#5f6f86',
        fontSize: 13,
        fontWeight: 600
      },
      min: 0,
      max: maxValue + 10,
      splitLine: { lineStyle: { color: 'rgba(143, 160, 183, 0.18)' } },
      axisLabel: { color: '#8b98a8', margin: 10 },
      axisLine: {
        show: true,
        lineStyle: { color: '#8fa0b7', width: 1.5 }
      },
      axisTick: { show: false }
    },
    yAxis: {
      name: '情感',
      nameLocation: 'middle',
      nameGap: 54,
      nameRotate: 90,
      nameTextStyle: {
        color: '#5f6f86',
        fontSize: 13,
        fontWeight: 600
      },
      min: -1,
      max: 1,
      splitLine: { lineStyle: { color: 'rgba(143, 160, 183, 0.18)' } },
      axisLabel: {
        color: '#8b98a8',
        margin: 12,
        formatter: (value: number) => (value > 0 ? '正向' : value < 0 ? '负向' : '')
      },
      axisLine: {
        show: true,
        lineStyle: { color: '#8fa0b7', width: 1.5 }
      },
      axisTick: { show: false }
    },
    graphic: [
      {
        type: 'text',
        left: 78,
        bottom: 16,
        style: {
          text: '低提及',
          fill: '#94a3b8',
          fontSize: 12
        }
      },
      {
        type: 'text',
        right: 28,
        bottom: 16,
        style: {
          text: '高提及',
          fill: '#2563eb',
          fontSize: 12,
          fontWeight: 600
        }
      },
      {
        type: 'text',
        left: 18,
        top: 28,
        style: {
          text: '正向机会',
          fill: '#059669',
          fontSize: 12,
          fontWeight: 600
        }
      },
      {
        type: 'text',
        left: 18,
        bottom: 76,
        style: {
          text: '负向问题',
          fill: '#dc2626',
          fontSize: 12,
          fontWeight: 600
        }
      },
      {
        type: 'text',
        right: 40,
        bottom: 102,
        silent: true,
        style: {
          text: highlightedPriorityTerms.length ? '先看右下高提及负向词' : '',
          fill: '#b42318',
          fontSize: 12,
          fontWeight: 700,
          backgroundColor: 'rgba(255, 236, 234, 0.92)',
          padding: [4, 8],
          borderRadius: 10
        }
      }
    ],
    series: [
      {
        type: 'scatter',
        symbolSize: (data: SentimentPointData) => Math.max(28, data[2] / 2.2),
        itemStyle: {
          color: (params: SentimentPointParam) => (params.data[3] === 'positive' ? '#1f8f74' : '#d94848'),
          opacity: 0.8
        },
        data: toSeriesData(normalTerms),
        label: {
          show: true,
          formatter: (params: SentimentPointParam) => `${params.data[4]} ${params.data[2]}`,
          color: '#5f6f86',
          fontSize: 11,
          fontWeight: 600,
          backgroundColor: 'rgba(255, 255, 255, 0.92)',
          borderColor: 'rgba(148, 163, 184, 0.2)',
          borderWidth: 1,
          borderRadius: 10,
          padding: [4, 8],
          position: (params: SentimentPointParam) => params.data[5]
        },
        markLine: {
          silent: true,
          symbol: 'none',
          lineStyle: { color: 'rgba(95, 111, 134, 0.26)', type: 'dashed', width: 1.2 },
          data: [
            { xAxis: midValue },
            { yAxis: 0 }
          ]
        }
      },
      {
        type: 'scatter',
        z: 3,
        symbolSize: (data: SentimentPointData) => Math.max(34, data[2] / 2),
        itemStyle: {
          color: '#e35d5b',
          opacity: 0.92,
          borderColor: '#b42318',
          borderWidth: 2,
          shadowBlur: 16,
          shadowColor: 'rgba(180, 35, 24, 0.24)'
        },
        data: toSeriesData(highlightedPriorityTerms),
        label: {
          show: true,
          formatter: (params: SentimentPointParam) => `优先 · ${params.data[4]} ${params.data[2]}`,
          color: '#9f1239',
          fontSize: 11,
          fontWeight: 700,
          backgroundColor: 'rgba(255, 244, 243, 0.98)',
          borderColor: 'rgba(180, 35, 24, 0.34)',
          borderWidth: 1.5,
          borderRadius: 10,
          padding: [5, 9],
          position: (params: SentimentPointParam) => params.data[5]
        }
      }
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

watch(() => props.terms, () => nextTick(render), { deep: true })
</script>

<style scoped>
.cloud-panel {
  min-height: 320px;
}

.cloud-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
  margin-bottom: 12px;
}

h2 {
  margin: 0;
  font-size: 18px;
}

p {
  margin: 6px 0 0;
  line-height: 1.7;
  color: var(--muted);
}

.cloud-meta {
  color: var(--muted);
  font-family: "IBM Plex Mono", monospace;
  font-size: 13px;
}

.priority-banner {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  gap: 14px;
  align-items: center;
  padding: 12px 14px;
  margin-bottom: 12px;
  border: 1px solid rgba(180, 35, 24, 0.14);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 247, 245, 0.98), rgba(255, 252, 251, 0.98)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.priority-banner--calm {
  border-color: rgba(15, 118, 110, 0.16);
  background:
    linear-gradient(180deg, rgba(243, 251, 249, 0.98), rgba(251, 255, 254, 0.98)),
    #ffffff;
}

.priority-flag,
.priority-copy,
.priority-metric {
  min-width: 0;
}

.priority-flag {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 7px 10px;
  border-radius: 999px;
  background: rgba(180, 35, 24, 0.08);
}

.priority-banner--calm .priority-flag {
  background: rgba(15, 118, 110, 0.08);
}

.priority-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #b42318;
  box-shadow: 0 0 0 4px rgba(180, 35, 24, 0.12);
}

.priority-banner--calm .priority-dot {
  background: #0f766e;
  box-shadow: 0 0 0 4px rgba(15, 118, 110, 0.12);
}

.priority-flag span,
.priority-metric span {
  color: #b42318;
  font-size: 12px;
  font-weight: 700;
}

.priority-banner--calm .priority-flag span,
.priority-banner--calm .priority-metric span {
  color: #0f766e;
}

.priority-copy {
  display: grid;
  gap: 4px;
}

.priority-copy b {
  color: var(--ink-strong);
  font-size: 16px;
  line-height: 1.5;
}

.priority-copy small,
.priority-metric small {
  color: var(--muted);
  line-height: 1.6;
}

.priority-metric {
  display: grid;
  justify-items: end;
  gap: 2px;
  padding-left: 14px;
  border-left: 1px solid rgba(148, 163, 184, 0.18);
  text-align: right;
}

.priority-metric b {
  color: var(--ink-strong);
  font-size: 24px;
  line-height: 1;
  font-variant-numeric: tabular-nums;
}

.chart-box {
  min-height: 260px;
}

.legend-row {
  display: flex;
  gap: 16px;
  margin-top: 10px;
}

.legend {
  font-size: 12px;
  color: var(--muted);
}

.legend--negative::before,
.legend--positive::before {
  content: '';
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 6px;
}

.legend--negative::before {
  background: #d94848;
}

.legend--positive::before {
  background: #1f8f74;
}

@media (max-width: 900px) {
  .priority-banner {
    grid-template-columns: 1fr;
    align-items: stretch;
  }

  .priority-metric {
    justify-items: start;
    padding-left: 0;
    border-left: 0;
    padding-top: 10px;
    border-top: 1px solid rgba(148, 163, 184, 0.18);
    text-align: left;
  }
}
</style>
