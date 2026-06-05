<template>
  <article class="insight-card" :class="{ pinned, focused }" @click="$emit('select', view.card.id)">
    <header>
      <div class="card-title">
        <small class="card-kicker">{{ pinned ? '优先赛道' : '候选类目' }}</small>
        <h3>{{ view.card.categoryName }}</h3>
        <span class="market-caption">{{ view.card.marketSize }}</span>
      </div>
      <div class="card-status">
        <el-tag :type="tagType" effect="plain">{{ view.card.competitionLevel }}</el-tag>
        <span class="decision-pill">{{ view.decision || decisionLabel }}</span>
      </div>
    </header>

    <div v-if="view.matchTags.length" class="tag-row">
      <el-tag
        v-for="tag in view.matchTags"
        :key="tag"
        size="small"
        :type="tagTypeFor(tag)"
        effect="plain"
      >
        {{ tag }}
      </el-tag>
      <el-tag v-if="platformHint" size="small" type="warning" effect="plain">
        {{ platformHint }}
      </el-tag>
    </div>

    <div class="decision-banner">
      <div>
        <span>当前判断</span>
        <b>{{ view.decision || decisionLabel }}</b>
      </div>
      <small>{{ view.card.recommendation }}</small>
    </div>

    <div class="score-strip">
      <div class="score-item">
        <span>综合评分</span>
        <b>{{ view.scoreBreakdown.totalScore }}</b>
      </div>
      <div class="score-item">
        <span>置信度</span>
        <b>{{ view.scoreBreakdown.confidence }}%</b>
      </div>
      <div class="score-item">
        <span>决策</span>
        <b>{{ view.decision }}</b>
      </div>
    </div>

    <dl class="metric-grid metric-grid--full">
      <div>
        <dt>市场规模</dt>
        <dd>{{ view.card.marketSize }}</dd>
      </div>
      <div>
        <dt>12月同比</dt>
        <dd>{{ view.card.marketGrowth }}</dd>
      </div>
      <div>
        <dt>竞争格局</dt>
        <dd>{{ view.card.competitionPattern }}</dd>
      </div>
      <div>
        <dt>竞争难度</dt>
        <dd>{{ view.card.competitionLevel }}</dd>
      </div>
      <div>
        <dt>价格空白</dt>
        <dd>{{ view.card.priceGap }}</dd>
      </div>
      <div>
        <dt>预估启动资金</dt>
        <dd>{{ view.card.estimatedStartupCost }}</dd>
      </div>
    </dl>

    <div class="analysis-grid">
      <div class="analysis-card">
        <span class="analysis-label">核心依据</span>
        <div class="reason-list">
          <div v-for="item in view.reasons.slice(0, 2)" :key="item.title">
            <b>{{ item.title }}</b>
            <span>{{ item.description }}</span>
          </div>
        </div>
      </div>
      <div class="analysis-card risk-card">
        <span class="analysis-label">主要风险</span>
        <div v-if="view.risks.length" class="risk-row">
          <el-tag
            v-for="item in view.risks"
            :key="item.title"
            size="small"
            :type="item.level === 'high' ? 'danger' : item.level === 'medium' ? 'warning' : 'info'"
            effect="plain"
          >
            {{ item.title }}
          </el-tag>
        </div>
        <p v-else class="risk-empty">当前没有显性高优先级风险，可继续看机会页验证。</p>
      </div>
    </div>

    <div class="cta">
      <div>
        <span>下一步</span>
        <b>{{ view.decision || decisionLabel }}</b>
      </div>
      <span>查看爆品机会 →</span>
    </div>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { InsightCardView } from '@/types'

const props = defineProps<{ view: InsightCardView; focused?: boolean; platformHint?: string }>()
defineEmits<{ select: [id: number] }>()

const pinned = computed(() => props.view.pinned)
const focused = computed(() => Boolean(props.focused))

const tagType = computed(() => {
  if (props.view.card.competitionLevel.includes('高')) return 'danger'
  if (props.view.card.competitionLevel.includes('中')) return 'warning'
  return 'success'
})

const decisionLabel = computed(() => {
  if (props.view.card.recommendation.includes('推荐')) return '优先推进'
  if (props.view.card.recommendation.includes('观望')) return '谨慎验证'
  return '避免进入'
})

function tagTypeFor(tag: string) {
  if (tag.includes('风险') || tag.includes('放弃')) return 'danger'
  if (tag.includes('观望')) return 'warning'
  if (tag.includes('目标') || tag.includes('立项')) return 'success'
  return 'info'
}
</script>

<style scoped>
.insight-card {
  position: relative;
  overflow: hidden;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 250, 255, 0.96)),
    #ffffff;
  border: 1px solid var(--line);
  border-radius: 16px;
  padding: 20px;
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
  box-shadow: 0 16px 28px rgba(15, 23, 42, 0.06);
}

.insight-card::before {
  content: '';
  position: absolute;
  inset: 0 auto 0 0;
  width: 3px;
  background: linear-gradient(180deg, rgba(37, 99, 235, 0.74), rgba(15, 118, 110, 0.26));
  opacity: 0;
  transition: opacity 0.18s ease;
}

.insight-card.pinned {
  border-color: rgba(37, 99, 235, 0.24);
  box-shadow: 0 0 0 1px rgba(37, 99, 235, 0.08), 0 18px 36px rgba(37, 99, 235, 0.08);
}

.insight-card.focused {
  border-color: rgba(15, 118, 110, 0.32);
  box-shadow: 0 0 0 1px rgba(15, 118, 110, 0.12), 0 18px 36px rgba(15, 23, 42, 0.08);
}

.insight-card:hover {
  transform: translateY(-3px);
  border-color: rgba(37, 99, 235, 0.24);
  box-shadow: 0 18px 36px rgba(15, 23, 42, 0.08);
}

.insight-card.pinned::before,
.insight-card.focused::before,
.insight-card:hover::before {
  opacity: 1;
}

header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
}

.card-title {
  min-width: 0;
}

.card-status {
  display: grid;
  gap: 8px;
  justify-items: end;
}

.decision-pill {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 0 12px;
  border: 1px solid rgba(37, 99, 235, 0.14);
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.08);
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
}

.card-kicker {
  display: inline-flex;
  align-items: center;
  min-height: 24px;
  padding: 0 8px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.08);
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 12px;
}

.score-strip {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  margin-top: 14px;
}

.score-item,
.metric-grid div,
.analysis-card {
  padding: 12px 14px;
  border: 1px solid var(--line);
  border-radius: 12px;
  background:
    linear-gradient(180deg, rgba(251, 252, 255, 0.98), rgba(246, 249, 253, 0.96)),
    #ffffff;
}

.decision-banner {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
  margin-top: 14px;
  padding: 14px 16px;
  border: 1px solid rgba(37, 99, 235, 0.14);
  border-radius: 14px;
  background: linear-gradient(180deg, rgba(239, 246, 255, 0.86), rgba(247, 250, 255, 0.92));
}

.score-item span,
.decision-banner span,
.decision-banner small {
  display: block;
}

.score-item span,
.decision-banner span {
  color: var(--muted);
  font-size: 12px;
}

.score-item b,
.decision-banner b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
}

.score-item b {
  font-size: 20px;
  line-height: 1.4;
}

.decision-banner b {
  font-size: 16px;
  line-height: 1.5;
}

.decision-banner small {
  max-width: 220px;
  margin: 0;
  color: var(--muted);
  line-height: 1.7;
  text-transform: none;
  font-weight: 500;
  text-align: right;
}

h3,
small,
span,
dt,
p {
  color: inherit;
}

small {
  display: inline-block;
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
  margin-bottom: 8px;
}

h3 {
  margin: 0;
  color: var(--ink-strong);
  font-size: 22px;
  line-height: 1.25;
}

.market-caption,
.analysis-label,
dt {
  color: var(--muted);
  font-size: 12px;
}

.market-caption {
  display: block;
  margin-top: 8px;
}

dd {
  margin: 6px 0 0;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-weight: 700;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  margin: 14px 0;
}

.metric-grid--full {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.analysis-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(0, 0.8fr);
  gap: 10px;
}

.analysis-label {
  display: block;
  margin-bottom: 10px;
  text-transform: uppercase;
}

.reason-list {
  display: grid;
  gap: 10px;
}

.reason-list b,
.reason-list span {
  display: block;
}

.reason-list b {
  color: var(--ink-strong);
  font-size: 13px;
}

.reason-list span {
  margin-top: 4px;
  color: var(--muted);
  font-size: 13px;
  line-height: 1.6;
}

.risk-card {
  display: grid;
  align-content: start;
}

.risk-row {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.risk-empty {
  margin: 0;
  color: var(--muted);
  line-height: 1.7;
}

.cta {
  margin-top: 14px;
  display: flex;
  justify-content: space-between;
  align-items: end;
  gap: 12px;
  color: var(--accent);
  font-size: 13px;
  font-weight: 700;
  padding-top: 14px;
  border-top: 1px solid var(--line);
}

.cta div span {
  display: block;
  color: var(--muted);
  font-size: 12px;
  font-weight: 500;
}

.cta b {
  display: block;
  margin-top: 6px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 13px;
}

@media (prefers-reduced-motion: reduce) {
  .insight-card {
    transition: none;
  }
}

@media (max-width: 1280px) {
  .decision-banner {
    grid-template-columns: 1fr;
  }

  .card-status {
    justify-items: start;
  }

  .decision-banner small {
    max-width: none;
    text-align: left;
  }
}

@media (max-width: 700px) {
  .score-strip,
  .metric-grid,
  .analysis-grid,
  .decision-banner {
    grid-template-columns: 1fr;
  }

  .card-status {
    justify-items: start;
  }

  .decision-banner small {
    max-width: none;
    text-align: left;
  }
}
</style>
