<template>
  <aside class="conclusion panel pad">
    <div class="conclusion-head">
      <div>
        <span class="eyebrow">分析结论</span>
        <b>先看一句话判断，再看优先对象</b>
      </div>
      <div v-if="platformLabel" class="platform-pill">{{ platformLabel }}</div>
    </div>
    <p class="conclusion-text">{{ conclusion }}</p>
    <div v-if="top3.length" class="conclusion-ribbon">
      <article>
        <span>优先对象</span>
        <b>{{ top3[0]?.categoryName ?? '-' }}</b>
        <small>当前最值得先复核的类目</small>
      </article>
      <article>
        <span>建议数量</span>
        <b>{{ top3.length }}</b>
        <small>已压缩为当前平台语境下的重点对象</small>
      </article>
    </div>
    <ul v-if="top3.length" class="top-list">
      <li v-for="(item, index) in top3" :key="item.categoryName">
        <div class="top-item-head">
          <b>TOP{{ index + 1 }} · {{ item.categoryName }}</b>
          <span>{{ item.metric }}</span>
        </div>
        <dl class="top-detail">
          <div><dt>月搜索量</dt><dd>{{ item.monthlySearchVolume ?? '—' }}</dd></div>
          <div><dt>12月增长率</dt><dd>{{ item.growthRate12m ?? '—' }}</dd></div>
          <div><dt>社媒热度</dt><dd>{{ item.socialHeat ?? '—' }}</dd></div>
          <div><dt>飙升词</dt><dd>{{ item.risingWords ?? '—' }}</dd></div>
          <div><dt>各平台12月同比</dt><dd>{{ item.platformGrowthRates ?? '—' }}</dd></div>
          <div v-if="item.tamSamSomSummary"><dt>TAM/SAM/SOM</dt><dd>{{ item.tamSamSomSummary }}</dd></div>
        </dl>
        <small>{{ item.categoryDescription ?? item.description }}</small>
      </li>
    </ul>
  </aside>
</template>

<script setup lang="ts">
import type { CategoryBrief } from '@/types'

defineProps<{
  conclusion: string
  top3: CategoryBrief[]
  platformLabel?: string
}>()
</script>

<style scoped>
.conclusion {
  margin-top: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.99), rgba(248, 251, 255, 0.98)),
    #ffffff;
}

.conclusion-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
}

.eyebrow {
  display: block;
  color: var(--muted-soft);
  font-size: 11px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.conclusion-text {
  margin: 10px 0 14px;
  color: var(--text-secondary);
  line-height: 1.6;
}

.top-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  gap: 12px;
}

.top-item-head {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 8px;
}

.top-detail {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px 12px;
  margin: 0 0 8px;
  font-size: 12px;
}

.top-detail dt {
  color: var(--muted-soft);
}

.top-detail dd {
  margin: 0;
  color: var(--text-secondary);
}

.conclusion-ribbon {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 12px;
}

.conclusion-ribbon article {
  padding: 10px 12px;
  border-radius: 12px;
  background: rgba(15, 98, 254, 0.06);
}

.platform-pill {
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(15, 98, 254, 0.08);
  color: var(--brand);
  font-size: 12px;
}
</style>
