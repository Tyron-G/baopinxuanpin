<template>
  <div class="panel pad chart-panel">
    <div class="section-title chart-head">
      <div>
        <span class="eyebrow">竞争格局四象限</span>
        <h2>价格 × 功能分布</h2>
        <p>按竞品价格带与功能完整度识别空白区间，优先验证竞品密度最低的象限。</p>
      </div>
      <div class="chart-note">
        <span>空白区间</span>
        <b>{{ report.blankZone }}</b>
        <small>{{ report.summary }}</small>
      </div>
    </div>
    <div class="chart-ribbon">
      <article>
        <span>样本数量</span>
        <b>{{ report.points.length }}</b>
        <small>含关联竞品与系统建议切入带</small>
      </article>
      <article>
        <span>低价高功能</span>
        <b>{{ quadrantCount('低价高功能') }}</b>
        <small>性价比竞争带</small>
      </article>
      <article>
        <span>高价高功能</span>
        <b>{{ quadrantCount('高价高功能') }}</b>
        <small>品牌溢价带</small>
      </article>
      <article>
        <span>判读重点</span>
        <b>优先验证 {{ report.blankZone }}</b>
        <small>避免与头部在同象限硬碰</small>
      </article>
    </div>
    <div ref="chartRef" class="chart-box chart-surface"></div>
  </div>
</template>

<script setup lang="ts">
import { echarts } from '@/lib/echarts'
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import type { CompetitionQuadrantReport } from '@/types'

const props = defineProps<{ report: CompetitionQuadrantReport }>()
const chartRef = ref<HTMLDivElement>()
let chart: echarts.ECharts | null = null
let resizeObserver: ResizeObserver | null = null

const quadrantCount = (name: string) => props.report.points.filter((item) => item.quadrant === name).length

const seriesData = computed(() =>
  props.report.points.map((item) => [
    item.priceIndex,
    item.functionIndex,
    item.label,
    item.quadrant,
    item.role
  ])
)

function resize() {
  requestAnimationFrame(() => chart?.resize())
}

function render() {
  if (!chartRef.value) return
  chart ||= echarts.init(chartRef.value)
  chart.setOption({
    tooltip: {
      formatter: (params: { data: [number, number, string, string, string] }) => {
        const [, , label, quadrant, role] = params.data
        return `${label}<br/>象限：${quadrant}<br/>角色：${role}`
      }
    },
    grid: { left: 72, right: 36, top: 40, bottom: 72 },
    xAxis: {
      name: '价格指数',
      min: 0,
      max: 100,
      splitLine: { lineStyle: { color: 'rgba(143, 160, 183, 0.18)' } }
    },
    yAxis: {
      name: '功能指数',
      min: 0,
      max: 100,
      splitLine: { lineStyle: { color: 'rgba(143, 160, 183, 0.18)' } }
    },
    series: [
      {
        type: 'scatter',
        symbolSize: 18,
        data: seriesData.value,
        itemStyle: { color: '#0f766e' },
        markLine: {
          silent: true,
          symbol: 'none',
          lineStyle: { color: '#94a3b8', type: 'dashed' },
          data: [{ xAxis: 50 }, { yAxis: 50 }]
        },
        markArea: {
          silent: true,
          itemStyle: { color: 'rgba(15, 118, 110, 0.06)' },
          data: [[{ xAxis: 50, yAxis: 50 }, { xAxis: 100, yAxis: 100 }]]
        }
      }
    ]
  }, true)
}

onMounted(async () => {
  await nextTick()
  render()
  resizeObserver = new ResizeObserver(() => resize())
  if (chartRef.value) resizeObserver.observe(chartRef.value)
})

watch(() => props.report, async () => {
  await nextTick()
  render()
}, { deep: true })

onBeforeUnmount(() => {
  resizeObserver?.disconnect()
  chart?.dispose()
  chart = null
})
</script>
