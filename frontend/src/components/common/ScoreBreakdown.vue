<template>
  <div class="breakdown">
    <div class="score-top">
      <div>
        <span>综合评分</span>
        <b>{{ breakdown.totalScore }}</b>
      </div>
      <div>
        <span>置信度</span>
        <b>{{ breakdown.confidence }}%</b>
      </div>
    </div>
    <div class="score-grid">
      <div><span>趋势</span><b>{{ breakdown.trendScore }}</b></div>
      <div><span>竞争</span><b>{{ breakdown.competitionScore }}</b></div>
      <div><span>供需</span><b>{{ breakdown.supplyGapScore }}</b></div>
      <div><span>品牌</span><b>{{ breakdown.brandFitScore }}</b></div>
      <div><span>风险扣分</span><b>-{{ breakdown.riskPenalty }}</b></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ScoreBreakdown } from '@/types'

defineProps<{ breakdown: ScoreBreakdown }>()
</script>

<style scoped>
.breakdown {
  border: 1px solid var(--line);
  border-radius: 8px;
  padding: 14px;
  background: rgba(255, 255, 255, 0.82);
}

.score-top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.score-top span,
.score-grid span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.score-top b {
  display: block;
  margin-top: 6px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 24px;
}

.score-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 10px;
}

.score-grid b {
  display: block;
  margin-top: 4px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 13px;
}

@media (max-width: 900px) {
  .score-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
