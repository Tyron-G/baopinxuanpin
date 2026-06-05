<template>
  <section class="panel pad timeline-panel">
    <div class="section-head">
      <div>
        <span class="eyebrow">竞品表现时间轴</span>
        <h2>{{ title }}</h2>
      </div>
      <p>{{ description }}</p>
    </div>

    <div v-if="items.length" class="timeline-ribbon">
      <article>
        <span>跟踪对象</span>
        <b>{{ items.length }}</b>
        <small>当前视角下已串联到时间轴的竞品数</small>
      </article>
      <article>
        <span>最强趋势</span>
        <b>{{ strongestTrendLabel }}</b>
        <small>{{ strongestTrendShop }}</small>
      </article>
      <article>
        <span>覆盖平台</span>
        <b>{{ coveredPlatforms }}</b>
        <small>便于快速判断平台节奏是否一致</small>
      </article>
      <article>
        <span>最高热度点</span>
        <b>{{ highestHeatPoint }}</b>
        <small>{{ highestHeatNote }}</small>
      </article>
    </div>

    <div v-if="items.length" class="timeline-grid">
      <article v-for="item in items" :key="`${item.shopName}-${item.platform}`" class="timeline-card">
        <header>
          <div>
            <span class="eyebrow">{{ item.platform }}</span>
            <h3>{{ item.shopName }}</h3>
            <small>{{ item.focusCategory }}</small>
          </div>
          <el-tag size="small" effect="plain" :type="tagType(item.trendLabel)">{{ item.trendLabel }}</el-tag>
        </header>

        <p class="summary">{{ item.summary }}</p>
        <div class="timeline-metrics">
          <article>
            <span>峰值热度</span>
            <b>{{ maxHeat(item) }}</b>
          </article>
          <article>
            <span>峰值销量</span>
            <b>{{ maxSales(item) }}</b>
          </article>
          <article>
            <span>最近一期</span>
            <b>{{ latestPeriod(item) }}</b>
          </article>
        </div>

        <ol class="timeline-list">
          <li v-for="point in item.points" :key="`${item.shopName}-${point.period}`" class="timeline-item">
            <div class="timeline-marker" />
            <div class="timeline-body">
              <div class="timeline-head">
                <b>{{ point.period }}</b>
                <span>热度 {{ point.heatIndex }} / 销量 {{ point.salesIndex }}</span>
              </div>
              <p>{{ point.note }}</p>
            </div>
          </li>
        </ol>
      </article>
    </div>

    <el-empty v-else description="当前视角下暂无竞品时间轴数据" />
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { CompetitorTimeline } from '@/types'

const props = withDefaults(
  defineProps<{
    items: CompetitorTimeline[]
    title?: string
    description?: string
  }>(),
  {
    title: '最近 4 周的起量节奏',
    description: '把竞品的热度、销量和上新动作按时间串起来，辅助判断它是在持续走强，还是一次性脉冲。'
  }
)

const strongestTrend = computed(() => props.items.find((item) => item.trendLabel.includes('走强')) ?? props.items[0])
const strongestTrendLabel = computed(() => strongestTrend.value?.trendLabel ?? '暂无趋势')
const strongestTrendShop = computed(() => strongestTrend.value?.shopName ?? '暂无对象')
const coveredPlatforms = computed(() => [...new Set(props.items.map((item) => item.platform))].join(' / ') || '-')
const highestHeatRecord = computed(() => {
  const records = props.items.flatMap((item) =>
    item.points.map((point) => ({
      shopName: item.shopName,
      period: point.period,
      heatIndex: point.heatIndex,
      note: point.note
    }))
  )
  return records.sort((left, right) => right.heatIndex - left.heatIndex)[0]
})
const highestHeatPoint = computed(() =>
  highestHeatRecord.value ? `${highestHeatRecord.value.shopName} · ${highestHeatRecord.value.period}` : '-'
)
const highestHeatNote = computed(() => highestHeatRecord.value?.note ?? '暂无热度峰值说明')

function tagType(label: string) {
  if (label.includes('走强')) return 'success'
  if (label.includes('分化')) return 'warning'
  return 'info'
}

function maxHeat(item: CompetitorTimeline) {
  return Math.max(...item.points.map((point) => point.heatIndex))
}

function maxSales(item: CompetitorTimeline) {
  return Math.max(...item.points.map((point) => point.salesIndex))
}

function latestPeriod(item: CompetitorTimeline) {
  return item.points[item.points.length - 1]?.period ?? '-'
}
</script>

<style scoped>
.timeline-panel {
  margin-top: 16px;
}

.section-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.section-head h2 {
  margin: 8px 0 0;
  color: var(--ink-strong);
  font-size: 18px;
}

.section-head p {
  margin: 0;
  max-width: 560px;
  color: var(--muted);
  line-height: 1.7;
}

.timeline-ribbon {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.timeline-ribbon article,
.timeline-metrics article {
  padding: 12px 14px;
  border: 1px solid var(--line);
  border-radius: 12px;
  background:
    linear-gradient(180deg, rgba(252, 253, 255, 0.98), rgba(246, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.timeline-ribbon span,
.timeline-ribbon small,
.timeline-metrics span {
  display: block;
}

.timeline-ribbon span,
.timeline-metrics span {
  color: var(--muted);
  font-size: 12px;
}

.timeline-ribbon b,
.timeline-metrics b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  line-height: 1.5;
}

.timeline-ribbon small {
  margin-top: 8px;
  color: var(--muted);
  line-height: 1.6;
}

.timeline-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 14px;
  margin-top: 16px;
}

.timeline-card {
  height: 100%;
  padding: 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.timeline-card header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.eyebrow {
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
}

.timeline-card h3 {
  margin: 8px 0 0;
  color: var(--ink-strong);
  font-size: 16px;
}

.timeline-card small {
  color: var(--muted);
}

.summary {
  margin: 14px 0 0;
  color: var(--muted);
  line-height: 1.7;
}

.timeline-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  margin-top: 14px;
}

.timeline-list {
  list-style: none;
  margin: 16px 0 0;
  padding: 0;
  min-height: 220px;
}

.timeline-item {
  position: relative;
  display: grid;
  grid-template-columns: 20px minmax(0, 1fr);
  gap: 12px;
  padding-bottom: 14px;
}

.timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-item:not(:last-child)::after {
  content: '';
  position: absolute;
  left: 8px;
  top: 18px;
  bottom: 0;
  width: 1px;
  background: var(--line);
}

.timeline-marker {
  width: 16px;
  height: 16px;
  margin-top: 2px;
  border: 2px solid rgba(37, 99, 235, 0.46);
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.12);
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.06);
}

.timeline-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: baseline;
}

.timeline-head b {
  color: var(--ink-strong);
}

.timeline-head span {
  color: var(--muted);
  font-size: 12px;
}

.timeline-body p {
  margin: 8px 0 0;
  color: var(--muted);
  line-height: 1.7;
}

@media (max-width: 900px) {
  .section-head,
  .timeline-ribbon,
  .timeline-grid,
  .timeline-metrics,
  .timeline-head {
    grid-template-columns: 1fr;
  }

  .timeline-grid {
    display: grid;
    grid-template-columns: 1fr;
  }

  .timeline-head {
    display: block;
  }
}
</style>
