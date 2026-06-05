<template>
  <article class="point-card">
    <header>
      <div>
        <span>{{ point.targetCrowd }}</span>
        <h3>{{ point.scenarioText }}</h3>
      </div>
      <el-tag :type="tagType">{{ point.decision }}</el-tag>
    </header>
    <div class="point-decision-banner">
      <div>
        <span>当前判断</span>
        <b>{{ point.decision }}</b>
      </div>
      <small>{{ point.reason }}</small>
    </div>
    <div class="score-line">
      <b>{{ point.opportunityScore }}</b>
      <el-progress :percentage="point.opportunityScore" :stroke-width="10" :show-text="false" />
    </div>
    <div class="meta-tags">
      <el-tag size="small" effect="plain" type="info">{{ point.opportunityLevel }}</el-tag>
      <el-tag size="small" effect="plain" type="success">{{ point.lifecycleStage }}</el-tag>
      <el-tag size="small" effect="plain" type="warning">{{ point.entryTiming }}</el-tag>
    </div>
    <p class="direction">{{ point.differentiation }}</p>
    <dl>
      <div>
        <dt>机会等级</dt>
        <dd>{{ point.opportunityLevel }}</dd>
      </div>
      <div>
        <dt>市场预估</dt>
        <dd>{{ point.marketEstimate }}</dd>
      </div>
      <div>
        <dt>生命周期</dt>
        <dd>{{ point.lifecycleStage }}</dd>
      </div>
      <div>
        <dt>入场时机</dt>
        <dd>{{ point.entryTiming }}</dd>
      </div>
    </dl>
    <div class="point-metrics-ribbon">
      <article>
        <span>机会引力</span>
        <b>{{ point.opportunityGravity }}</b>
      </article>
      <article>
        <span>竞争阻力</span>
        <b>{{ point.competitionResistance }}</b>
      </article>
      <article>
        <span>利润弹性</span>
        <b>{{ point.profitElasticity }}</b>
      </article>
    </div>
    <footer>{{ point.differentiation }}</footer>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { OpportunityPoint } from '@/types'

const props = defineProps<{ point: OpportunityPoint }>()

const tagType = computed(() => {
  if (props.point.decision === '推荐立项') return 'success'
  if (props.point.decision === '观望') return 'warning'
  return 'info'
})
</script>

<style scoped>
.point-card {
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 250, 255, 0.96)),
    #ffffff;
  border: 1px solid var(--line);
  border-radius: 14px;
  padding: 18px;
  box-shadow: var(--shadow-sm);
}

header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

span {
  color: var(--muted);
  font-size: 13px;
  text-transform: uppercase;
}

h3 {
  margin: 6px 0 0;
  color: var(--ink-strong);
  font-size: 18px;
}

.score-line {
  display: grid;
  grid-template-columns: 48px minmax(0, 1fr);
  align-items: center;
  gap: 12px;
  margin: 16px 0;
}

.point-decision-banner {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
  margin-top: 14px;
  padding: 14px 16px;
  border: 1px solid rgba(37, 99, 235, 0.14);
  border-radius: 14px;
  background: linear-gradient(180deg, rgba(239, 246, 255, 0.84), rgba(247, 250, 255, 0.94));
}

.point-decision-banner span,
.point-decision-banner small {
  display: block;
}

.point-decision-banner span {
  color: var(--muted);
  font-size: 12px;
}

.point-decision-banner b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 16px;
  line-height: 1.5;
}

.point-decision-banner small {
  max-width: 240px;
  color: var(--muted);
  line-height: 1.7;
  text-align: right;
}

.score-line b {
  color: var(--accent);
  font-family: "IBM Plex Mono", monospace;
  font-size: 30px;
}

.meta-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 14px;
}

.direction,
footer {
  line-height: 1.7;
  color: var(--muted);
}

dl {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
  margin: 16px 0;
}

dt {
  color: var(--muted);
  font-size: 12px;
}

dd {
  margin: 4px 0 0;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-weight: 700;
}

.point-metrics-ribbon {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  margin: 16px 0 0;
}

.point-metrics-ribbon article {
  padding: 12px 14px;
  border: 1px solid var(--line);
  border-radius: 12px;
  background: rgba(248, 250, 252, 0.9);
}

.point-metrics-ribbon span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.point-metrics-ribbon b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 15px;
  line-height: 1.5;
}

footer {
  border-top: 1px solid var(--line);
  padding-top: 14px;
  margin-top: 16px;
}

@media (max-width: 780px) {
  dl,
  .point-metrics-ribbon,
  .point-decision-banner {
    grid-template-columns: 1fr;
  }

  .point-decision-banner small {
    max-width: none;
    text-align: left;
  }
}
</style>
