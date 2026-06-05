<template>
  <div class="panel pad summary">
    <div class="summary-head">
      <div>
        <span class="eyebrow">决策总卡</span>
        <h2>{{ summary.decision }}</h2>
        <p>{{ summary.headline }}</p>
      </div>
      <el-tag :type="tagType" size="large">{{ summary.confidence }}% 置信度</el-tag>
    </div>

    <ScoreBreakdown :breakdown="summary.scoreBreakdown" />

    <div class="summary-grid">
      <div class="summary-block">
        <span class="eyebrow">核心依据</span>
        <ul>
          <li v-for="item in summary.reasons" :key="item.title">
            <b>{{ item.title }}</b>
            <span>{{ item.description }}</span>
          </li>
        </ul>
      </div>

      <div class="summary-block">
        <span class="eyebrow">主要风险</span>
        <ul>
          <li v-for="item in summary.risks" :key="item.title">
            <b>{{ item.title }}</b>
            <span>{{ item.description }}</span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { DecisionSummary } from '@/types'
import ScoreBreakdown from '@/components/common/ScoreBreakdown.vue'

const props = defineProps<{ summary: DecisionSummary }>()

const tagType = computed(() => {
  if (props.summary.decision.includes('推荐')) return 'danger'
  if (props.summary.decision.includes('观望')) return 'warning'
  return 'info'
})
</script>

<style scoped>
.summary {
  margin-bottom: 16px;
}

.summary-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 14px;
}

.eyebrow {
  display: block;
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
}

h2 {
  margin: 10px 0 0;
  color: var(--ink-strong);
  font-size: 24px;
}

p {
  margin: 8px 0 0;
  color: var(--muted);
  line-height: 1.75;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-top: 14px;
}

.summary-block {
  border: 1px solid var(--line);
  border-radius: 14px;
  padding: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

ul {
  margin: 12px 0 0;
  padding-left: 18px;
  display: grid;
  gap: 10px;
}

li b,
li span {
  display: block;
}

li b {
  color: var(--ink-strong);
}

li span {
  margin-top: 4px;
  color: var(--muted);
  line-height: 1.7;
}

@media (max-width: 900px) {
  .summary-head,
  .summary-grid {
    display: grid;
  }
}
</style>
