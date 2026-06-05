<template>
  <section v-if="metrics" class="panel pad market-metrics-bar">
    <span class="eyebrow">类目竞争指标（PRD）</span>
    <h2>在售 SKU · 头部销量占比 · 同质化评分</h2>
    <p class="metrics-summary">{{ metrics.summary }}</p>
    <div class="metrics-grid">
      <article>
        <span>平台视角</span>
        <b>{{ metrics.platform }}</b>
      </article>
      <article>
        <span>在售 SKU 总数</span>
        <b>{{ metrics.totalSkuCount }}</b>
        <small>类目供给密度参考</small>
      </article>
      <article>
        <span>Top10 销量占比</span>
        <b>{{ metrics.top10SalesRatio }}%</b>
        <small>头部垄断度辅助判断</small>
      </article>
      <article>
        <span>同质化评分</span>
        <b>{{ metrics.homogeneityScore }}</b>
        <small>越低越易做差异化</small>
      </article>
      <article>
        <span>类目搜索量</span>
        <b>{{ metrics.totalSearchVolume }}</b>
        <small>与洞察竞争图一致口径</small>
      </article>
    </div>
  </section>
</template>

<script setup lang="ts">
import type { CategoryMarketMetrics } from '@/types'

defineProps<{ metrics?: CategoryMarketMetrics }>()
</script>

<style scoped>
.market-metrics-bar h2 {
  margin: 6px 0 10px;
}

.metrics-summary {
  margin: 0 0 12px;
  color: var(--muted);
  line-height: 1.55;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 10px;
}

.metrics-grid article {
  padding: 10px 12px;
  border-radius: 10px;
  background: rgba(248, 251, 255, 0.95);
}

.metrics-grid span,
.metrics-grid small {
  display: block;
  color: var(--muted);
}

.metrics-grid b {
  display: block;
  margin: 4px 0;
}

@media (max-width: 1100px) {
  .metrics-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
