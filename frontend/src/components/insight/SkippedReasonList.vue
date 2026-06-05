<template>
  <div v-if="items.length" class="skipped-box" :class="{ compact }">
    <div class="head">
      <div>
        <span class="eyebrow">为什么没优先推荐它</span>
        <h2>次优候选说明</h2>
      </div>
      <p>这些类目不是没有机会，而是在当前品牌约束下，优先级低于第一推荐赛道。</p>
    </div>

    <div class="skipped-grid">
      <article v-for="item in items" :key="item.card.id" class="skipped-card">
        <header>
          <div>
            <h3>{{ item.card.categoryName }}</h3>
            <span>{{ item.brandFitDetail.overallFitLevel }}</span>
          </div>
          <el-tag type="warning" effect="plain">{{ item.decision }}</el-tag>
        </header>
        <p class="summary">{{ item.brandFitDetail.summary }}</p>
        <ul>
          <li v-for="risk in normalizedMismatches(item)" :key="`${item.card.id}-${risk}`">
            {{ risk }}
          </li>
        </ul>
      </article>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { InsightCardView } from '@/types'

defineProps<{ items: InsightCardView[]; compact?: boolean }>()

function normalizedMismatches(item: InsightCardView) {
  if (item.mismatches.length) {
    return item.mismatches.map((risk) => risk.message)
  }
  return ['当前类目仍有机会，但综合分与品牌适配度低于第一推荐赛道。']
}
</script>

<style scoped>
.skipped-box {
  padding: 22px 24px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background: var(--panel-bg);
  box-shadow: var(--shadow-md);
  margin-bottom: 16px;
}

.skipped-box.compact {
  margin-bottom: 0;
  position: sticky;
  top: 24px;
}

.head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.eyebrow {
  display: block;
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
}

h2,
h3 {
  margin: 0;
  color: var(--ink-strong);
}

.head h2 {
  margin-top: 8px;
  font-size: 20px;
}

.head p {
  margin: 0;
  max-width: 520px;
  color: var(--muted);
  line-height: 1.7;
}

.skipped-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-top: 16px;
}

.skipped-card {
  border: 1px solid var(--line);
  border-radius: 12px;
  padding: 14px;
  background: rgba(255, 255, 255, 0.9);
}

.skipped-card header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.skipped-card span {
  display: block;
  margin-top: 6px;
  color: var(--muted);
  font-size: 12px;
}

.summary {
  margin: 12px 0 0;
  color: var(--muted);
  line-height: 1.7;
}

ul {
  margin: 12px 0 0;
  padding-left: 18px;
  color: var(--ink);
  line-height: 1.7;
}

@media (max-width: 900px) {
  .head,
  .skipped-grid {
    display: grid;
  }
}
</style>
