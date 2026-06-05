<template>
  <section class="panel pad dimension-panel">
    <div class="dimension-head">
      <span class="eyebrow">数据维度覆盖</span>
      <b>PRD 五类数据在本地 MVP 的映射</b>
      <p>不接入真实 API 时，由内置样例 + 连接器面板覆盖各维度，便于审计与演示说明。</p>
    </div>
    <div class="dimension-grid">
      <article v-for="item in dimensions" :key="item.key">
        <span>{{ item.label }}</span>
        <b>{{ item.source }}</b>
        <small>{{ item.hint }}</small>
      </article>
    </div>
  </section>
</template>

<script setup lang="ts">
const dimensions = [
  { key: 'market', label: '市场数据', source: '趋势/竞争/供需 API', hint: '12 月搜索量、CR3、价格带' },
  { key: 'product', label: '商品数据', source: '洞察卡片 + TOP50', hint: '规模、增速、机会分' },
  { key: 'competition', label: '竞争数据', source: '竞品监控 + 内置种子', hint: '差评主题、爆款时间轴' },
  { key: 'user', label: '用户数据', source: '痛点清单 + 人群画像', hint: '差评极性、场景摘要' },
  { key: 'supply', label: '供应链数据', source: '1688 情报 + 供应链匹配', hint: 'MOQ、工厂匹配分' }
]
</script>

<style scoped>
.dimension-panel {
  margin-bottom: 16px;
}

.dimension-head p {
  margin: 8px 0 0;
  color: var(--muted);
  font-size: 13px;
  line-height: 1.5;
}

.dimension-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 10px;
  margin-top: 14px;
}

.dimension-grid article {
  padding: 10px 12px;
  border-radius: 12px;
  background: rgba(15, 98, 254, 0.05);
  border: 1px solid rgba(148, 163, 184, 0.16);
}

.dimension-grid span {
  display: block;
  font-size: 11px;
  color: var(--muted-soft);
}

.dimension-grid b {
  display: block;
  margin: 4px 0;
  font-size: 13px;
}

.dimension-grid small {
  color: var(--muted);
  font-size: 11px;
  line-height: 1.4;
}

@media (max-width: 1100px) {
  .dimension-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
