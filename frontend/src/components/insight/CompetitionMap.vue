<template>
  <div class="panel pad chart-panel">
    <div class="section-title chart-head">
      <div>
        <h2>竞争难不难</h2>
        <p>市场大盘 × 头部集中度 × SKU 密度</p>
      </div>
      <div class="chart-toolbar-card">
        <span>平台视角</span>
        <b>{{ activePlatform }}</b>
        <small>切换后会同步重算进入窗口和对象密度判断。</small>
        <el-segmented v-model="activePlatform" :options="platformOptions" />
      </div>
    </div>
    <div class="chart-ribbon">
      <article>
        <span>进入窗口最宽</span>
        <b>{{ easiestCategory?.categoryName ?? '暂无类目' }}</b>
        <small>{{ easiestCategory ? `CR3 ${easiestCategory.cr3}% · CR5 ${easiestCategory.cr5}%` : '等待竞争数据' }}</small>
      </article>
      <article>
        <span>CR3 最低</span>
        <b>{{ lowestCr3Category?.categoryName ?? '暂无类目' }}</b>
        <small>{{ lowestCr3Category ? `CR3 ${lowestCr3Category.cr3}%` : '等待 CR3 数据' }}</small>
      </article>
      <article>
        <span>最大市场</span>
        <b>{{ largestMarketCategory?.categoryName ?? '暂无类目' }}</b>
        <small>{{ largestMarketCategory ? `搜索量 ${largestMarketCategory.totalSearchVolume}` : '等待搜索量数据' }}</small>
      </article>
      <article>
        <span>低集中度类目</span>
        <b>{{ lowConcentrationCount }}</b>
        <small>CR5 低于 40% 的类目数量</small>
      </article>
      <article>
        <span>判读重点</span>
        <b>优先找大盘够大且集中度不高</b>
        <small>避免只看大盘，不看头部壁垒。</small>
      </article>
    </div>
    <div class="chart-brief">
      <article>
        <span>当前平台</span>
        <b>{{ activePlatform }}</b>
      </article>
      <article>
        <span>候选赛道</span>
        <b>{{ filteredRows.length }}</b>
      </article>
      <article>
        <span>竞争摘要</span>
        <b>{{ platformSummary }}</b>
      </article>
    </div>
    <div ref="chartRef" class="chart-box chart-surface"></div>
  </div>
</template>

<script setup lang="ts">
import { echarts } from '@/lib/echarts'
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import type { CompetitionData } from '@/types'

const props = defineProps<{ rows: CompetitionData[] }>()
const chartRef = ref<HTMLDivElement>()
const activePlatform = ref('全平台')
let chart: echarts.ECharts | null = null
let resizeObserver: ResizeObserver | null = null

const platformOptions = computed(() => [...new Set(props.rows.map((item) => item.platform))])
const filteredRows = computed(() => {
  const matched = props.rows.filter((item) => item.platform === activePlatform.value)
  return matched.length ? matched : props.rows.filter((item) => item.platform === '全平台')
})
const easiestCategory = computed(() => [...filteredRows.value].sort((left, right) => Number(left.cr5) - Number(right.cr5))[0])
const lowestCr3Category = computed(() => [...filteredRows.value].sort((left, right) => Number(left.cr3) - Number(right.cr3))[0])
const largestMarketCategory = computed(() => [...filteredRows.value].sort((left, right) => right.totalSearchVolume - left.totalSearchVolume)[0])
const lowConcentrationCount = computed(() => filteredRows.value.filter((item) => Number(item.cr5) < 40).length)
const platformSummary = computed(() => {
  if (!filteredRows.value.length) return '暂无竞争格局数据'
  const easiest = easiestCategory.value
  return `${activePlatform.value} 当前 ${easiest.categoryName} 的头部集中度最低，进入窗口相对更宽`
})

function resize() {
  requestAnimationFrame(() => chart?.resize())
}

function render() {
  if (!chartRef.value) return
  chart ||= echarts.init(chartRef.value)
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
      formatter: (params: any) => {
        const row = filteredRows.value[params.dataIndex]
        return `${row.categoryName}<br/>搜索量：${row.totalSearchVolume}<br/>CR3：${row.cr3}% · CR5：${row.cr5}%<br/>${row.conclusion}`
      }
    },
    grid: { left: 68, right: 38, top: 58, bottom: 56, containLabel: true },
    xAxis: {
      name: '市场大盘',
      type: 'value',
      axisLabel: { color: '#7b8798', formatter: (value: number) => `${Math.round(value / 10000)}万` },
      splitLine: { lineStyle: { color: '#e6edf5' } }
    },
    yAxis: {
      name: '头部集中度',
      type: 'value',
      max: 80,
      axisLabel: { color: '#7b8798', formatter: '{value}%' }
    },
    series: [{
      type: 'scatter',
      symbolSize: (data: number[]) => Math.max(34, Math.min(88, data[2] / 70)),
      itemStyle: {
        color: '#2563eb',
        opacity: 0.78,
        shadowBlur: 18,
        shadowColor: 'rgba(37, 99, 235, 0.16)'
      },
      data: filteredRows.value.map((row) => [row.totalSearchVolume, row.cr5, row.totalSkuCount, row.categoryName]),
      label: {
        show: true,
        formatter: (params: any) => params.data[3],
        position: 'top',
        color: '#52627a'
      }
    }]
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
  grid-template-columns: repeat(5, minmax(0, 1fr));
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
