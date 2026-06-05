<template>
  <div class="panel pad chart-panel">
    <div class="section-title chart-head">
      <div>
        <span class="eyebrow">机会定位图</span>
        <h2>机会引力 × 竞争阻力</h2>
        <p>气泡大小代表利润弹性，优先关注高引力、低阻力且具备利润弹性的机会带。</p>
      </div>
      <div class="chart-note">
        <span>判读原则</span>
        <b>先看象限，再看气泡大小</b>
        <small>更适合在管理讨论里快速判断“先验证谁”。</small>
      </div>
    </div>
    <div class="chart-ribbon">
      <article>
        <span>优先机会</span>
        <b>{{ leadPoint?.scenarioText ?? '暂无机会点' }}</b>
        <small>{{ leadPoint ? `${leadPoint.targetCrowd} · ${leadPoint.entryTiming}` : '等待机会点生成' }}</small>
      </article>
      <article>
        <span>最佳利润弹性</span>
        <b>{{ highestProfitPoint ? `${highestProfitPoint.profitElasticity}` : '-' }}</b>
        <small>{{ highestProfitPoint?.scenarioText ?? '暂无利润弹性对比' }}</small>
      </article>
      <article>
        <span>最低竞争阻力</span>
        <b>{{ lowestResistancePoint ? `${lowestResistancePoint.competitionResistance}` : '-' }}</b>
        <small>{{ lowestResistancePoint?.scenarioText ?? '暂无竞争阻力对比' }}</small>
      </article>
      <article>
        <span>高优先级数量</span>
        <b>{{ priorityPointCount }}</b>
        <small>优先看右下区域和高评分气泡</small>
      </article>
    </div>
    <div class="chart-meta">
      <article>
        <span>横轴</span>
        <b>机会引力</b>
        <small>越往右说明需求吸引力越强</small>
      </article>
      <article>
        <span>纵轴</span>
        <b>竞争阻力</b>
        <small>越靠下越适合优先进入验证</small>
      </article>
      <article>
        <span>气泡大小</span>
        <b>利润弹性</b>
        <small>PRD 第三轴：同质化越低、溢价空间越大</small>
      </article>
    </div>
    <div ref="chartRef" class="chart-box chart-surface"></div>
  </div>
</template>

<script setup lang="ts">
import { echarts } from '@/lib/echarts'
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import type { OpportunityPoint } from '@/types'

const props = defineProps<{ rows: OpportunityPoint[] }>()
const chartRef = ref<HTMLDivElement>()
let chart: echarts.ECharts | null = null
let resizeObserver: ResizeObserver | null = null

const leadPoint = computed(() => props.rows[0])
const highestProfitPoint = computed(() => [...props.rows].sort((left, right) => right.profitElasticity - left.profitElasticity)[0])
const lowestResistancePoint = computed(() => [...props.rows].sort((left, right) => left.competitionResistance - right.competitionResistance)[0])
const priorityPointCount = computed(() => props.rows.filter((item) => item.opportunityScore >= 80).length)
const priorityRows = computed(() => props.rows.filter((item) => item.opportunityScore >= 80))
const normalRows = computed(() => props.rows.filter((item) => item.opportunityScore < 80))

function shortScenarioLabel(row: OpportunityPoint) {
  const base = row.scenarioText.replace(/[·•]/g, ' ').trim()
  if (base.length <= 8) {
    return `${base} ${row.opportunityScore}`
  }
  return `${base.slice(0, 8)}… ${row.opportunityScore}`
}

function labelPosition(gravity: number, resistance: number) {
  if (gravity >= 70 && resistance <= 55) return 'right'
  if (gravity >= 70 && resistance > 55) return 'top'
  if (gravity < 70 && resistance <= 55) return 'bottom'
  return 'left'
}

function toSeriesData(rows: OpportunityPoint[]) {
  return rows.map((row) => [
    row.opportunityGravity,
    row.competitionResistance,
    row.profitElasticity,
    row.opportunityLevel,
    row.scenarioText,
    row.targetCrowd,
    labelPosition(row.opportunityGravity, row.competitionResistance)
  ])
}

function resize() {
  requestAnimationFrame(() => chart?.resize())
}

function render() {
  if (!chartRef.value) return
  chart ||= echarts.init(chartRef.value)
  chart.setOption({
    tooltip: {
      formatter: (params: any) => {
        const [gravity, resistance, elasticity, , scenario, crowd] = params.data
        const row = props.rows.find((item) => item.scenarioText === scenario)
        const score = row?.opportunityScore ?? '-'
        return `${crowd}<br/>${scenario}<br/>机会评分：${score}<br/>机会引力：${gravity} / 竞争阻力：${resistance}<br/>利润弹性：${elasticity}`
      }
    },
    grid: { left: 84, right: 46, top: 48, bottom: 82, containLabel: false },
    xAxis: {
      name: '机会引力',
      nameLocation: 'middle',
      nameGap: 44,
      nameTextStyle: {
        color: '#5f6f86',
        fontSize: 13,
        fontWeight: 600
      },
      type: 'value',
      min: 40,
      max: 100,
      splitNumber: 6,
      axisLabel: {
        color: '#6b746f',
        margin: 10
      },
      axisLine: {
        show: true,
        lineStyle: {
          color: '#8fa0b7',
          width: 1.5
        }
      },
      axisTick: {
        show: false
      },
      splitLine: {
        lineStyle: {
          color: 'rgba(143, 160, 183, 0.18)'
        }
      }
    },
    yAxis: {
      name: '竞争阻力',
      nameLocation: 'middle',
      nameGap: 58,
      nameRotate: 90,
      nameTextStyle: {
        color: '#5f6f86',
        fontSize: 13,
        fontWeight: 600
      },
      type: 'value',
      min: 20,
      max: 90,
      splitNumber: 7,
      axisLabel: {
        color: '#6b746f',
        margin: 12
      },
      axisLine: {
        show: true,
        lineStyle: {
          color: '#8fa0b7',
          width: 1.5
        }
      },
      axisTick: {
        show: false
      },
      splitLine: {
        lineStyle: {
          color: 'rgba(143, 160, 183, 0.18)'
        }
      }
    },
    graphic: [
      {
        type: 'rect',
        shape: {
          x: 84 + ((100 - 70) / (100 - 40)) * (chartRef.value?.clientWidth ? (chartRef.value.clientWidth - 130) : 0),
          y: 48 + ((90 - 55) / (90 - 20)) * (chartRef.value?.clientHeight ? (chartRef.value.clientHeight - 130) : 0),
          width: (chartRef.value?.clientWidth ? (chartRef.value.clientWidth - 130) : 0) * 0.5,
          height: (chartRef.value?.clientHeight ? (chartRef.value.clientHeight - 130) : 0) * 0.5
        },
        silent: true,
        style: {
          fill: 'rgba(15, 118, 110, 0.05)',
          stroke: 'rgba(15, 118, 110, 0.16)',
          lineWidth: 1,
          radius: 16
        }
      },
      {
        type: 'text',
        left: 84,
        bottom: 18,
        style: {
          text: '低引力',
          fill: '#94a3b8',
          fontSize: 12
        }
      },
      {
        type: 'text',
        right: 36,
        bottom: 18,
        style: {
          text: '高引力',
          fill: '#2563eb',
          fontSize: 12,
          fontWeight: 600
        }
      },
      {
        type: 'text',
        left: 18,
        top: 42,
        style: {
          text: '高阻力',
          fill: '#dc2626',
          fontSize: 12,
          fontWeight: 600
        }
      },
      {
        type: 'text',
        left: 18,
        bottom: 82,
        style: {
          text: '低阻力',
          fill: '#059669',
          fontSize: 12,
          fontWeight: 600
        }
      },
      {
        type: 'text',
        right: 72,
        bottom: 118,
        silent: true,
        style: {
          text: '优先验证区',
          fill: '#0f766e',
          fontSize: 12,
          fontWeight: 700
        }
      },
      {
        type: 'text',
        right: 44,
        top: 22,
        silent: true,
        style: {
          text: '越右越值得看，越下越适合先试',
          fill: '#7b8798',
          fontSize: 12
        }
      }
    ],
    series: [
      {
        type: 'scatter',
        symbolSize: (data: number[]) => Math.max(34, data[2] / 1.45),
        markLine: {
          silent: true,
          symbol: 'none',
          label: {
            show: false
          },
          lineStyle: {
            color: 'rgba(95, 111, 134, 0.26)',
            type: 'dashed',
            width: 1.2
          },
          data: [
            { xAxis: 70 },
            { yAxis: 55 }
          ]
        },
        itemStyle: {
          color: (params: any) => {
            const scenario = params.data[4]
            const score = props.rows.find((row) => row.scenarioText === scenario)?.opportunityScore ?? 0
            if (score >= 60) return '#2f80ed'
            return '#8a928f'
          },
          opacity: 0.78
        },
        label: {
          show: true,
          formatter: (params: any) => shortScenarioLabel(props.rows.find((row) => row.scenarioText === params.data[4]) ?? props.rows[0]),
          color: '#5f6f86',
          fontSize: 11,
          fontWeight: 600,
          backgroundColor: 'rgba(255, 255, 255, 0.94)',
          borderColor: 'rgba(148, 163, 184, 0.22)',
          borderWidth: 1,
          borderRadius: 10,
          padding: [4, 8],
          position: (params: any) => params.data[6]
        },
        data: toSeriesData(normalRows.value)
      },
      {
        type: 'scatter',
        z: 3,
        symbolSize: (data: number[]) => Math.max(42, data[2] / 1.22),
        itemStyle: {
          color: (params: any) => {
            const scenario = params.data[4]
            const score = props.rows.find((row) => row.scenarioText === scenario)?.opportunityScore ?? 0
            return score >= 90 ? '#e85d58' : '#f2a541'
          },
          opacity: 0.92,
          borderColor: 'rgba(180, 83, 9, 0.34)',
          borderWidth: 2,
          shadowBlur: 18,
          shadowColor: 'rgba(232, 93, 88, 0.18)'
        },
        label: {
          show: true,
          formatter: (params: any) => `优先 · ${shortScenarioLabel(props.rows.find((row) => row.scenarioText === params.data[4]) ?? props.rows[0])}`,
          color: '#9a3412',
          fontSize: 11,
          fontWeight: 700,
          backgroundColor: 'rgba(255, 249, 240, 0.98)',
          borderColor: 'rgba(245, 158, 11, 0.28)',
          borderWidth: 1.5,
          borderRadius: 10,
          padding: [5, 9],
          position: (params: any) => params.data[6]
        },
        data: toSeriesData(priorityRows.value)
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

watch(() => props.rows, () => nextTick(render), { deep: true })
defineExpose({ resize })
</script>

<style scoped>
.chart-ribbon {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 14px;
}

.chart-ribbon article {
  padding: 12px 14px;
  border: 1px solid var(--line);
  border-radius: 12px;
  background:
    linear-gradient(180deg, rgba(252, 253, 255, 0.98), rgba(246, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.chart-ribbon span,
.chart-ribbon small {
  display: block;
}

.chart-ribbon span {
  color: var(--muted);
  font-size: 12px;
}

.chart-ribbon b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 15px;
  line-height: 1.5;
}

.chart-ribbon small {
  margin-top: 8px;
  color: var(--muted);
  line-height: 1.6;
}

.chart-meta {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 14px;
}

.chart-meta article {
  padding: 12px 14px;
  border: 1px solid var(--line);
  border-radius: 12px;
  background:
    linear-gradient(180deg, rgba(252, 253, 255, 0.98), rgba(246, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.eyebrow,
.chart-meta span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.eyebrow {
  color: var(--accent);
  font-weight: 700;
  text-transform: uppercase;
}

.chart-meta b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 14px;
}

.chart-meta small,
.chart-note small {
  display: block;
  margin-top: 8px;
  color: var(--muted);
  line-height: 1.6;
}

.chart-panel {
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.99), rgba(248, 251, 255, 0.98)),
    #ffffff;
}

.chart-head {
  margin-bottom: 16px;
}

.chart-note {
  min-width: 240px;
  max-width: 280px;
  padding: 14px 16px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(248, 251, 255, 0.98), rgba(241, 246, 253, 0.98)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.chart-note span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.chart-note b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  line-height: 1.5;
}

.chart-surface {
  min-height: 360px;
}

p {
  margin: 6px 0 0;
  line-height: 1.7;
}

@media (max-width: 760px) {
  .chart-ribbon,
  .chart-meta {
    grid-template-columns: 1fr;
  }
}
</style>
