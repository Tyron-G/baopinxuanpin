<template>
  <section class="panel pad">
    <span class="eyebrow">竞品对比摘要</span>
    <h2>{{ title }}</h2>
    <div class="summary-ribbon">
      <article>
        <span>跟踪对象</span>
        <b>{{ trackedShopCount }}</b>
      </article>
      <article>
        <span>覆盖平台</span>
        <b>{{ coveredPlatforms }}</b>
      </article>
      <article>
        <span>最强信号</span>
        <b>{{ strongestSignal }}</b>
      </article>
    </div>
    <div class="mini-grid">
      <div>
        <span>跟踪对象</span>
        <b>{{ trackedShopCount }}</b>
      </div>
      <div>
        <span>覆盖平台</span>
        <b>{{ coveredPlatforms }}</b>
      </div>
      <div>
        <span>爆品样本</span>
        <b>{{ totalHitProductCount }}</b>
      </div>
      <div>
        <span>最强信号</span>
        <b>{{ strongestSignal }}</b>
      </div>
    </div>
    <p class="narrative-copy">{{ summary }}</p>
    <div v-if="commonComplaintTopics.length" class="tag-row">
      <el-tag
        v-for="item in commonComplaintTopics"
        :key="item"
        size="small"
        type="danger"
        effect="plain"
      >
        {{ item }}
      </el-tag>
    </div>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  trackedShopCount: number
  coveredPlatforms: string
  totalHitProductCount: number
  strongestSignal: string
  summary: string
  commonComplaintTopics: string[]
}>()
</script>

<style scoped>
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
}

.summary-ribbon {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.summary-ribbon article {
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(251, 252, 255, 0.98), rgba(246, 249, 253, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.summary-ribbon span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.summary-ribbon b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 15px;
  line-height: 1.5;
}

.mini-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.mini-grid div {
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 251, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.mini-grid span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.mini-grid b {
  display: block;
  margin-top: 6px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
}

.narrative-copy {
  margin: 14px 0 0;
  color: var(--muted);
  line-height: 1.75;
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

@media (max-width: 900px) {
  .summary-ribbon,
  .mini-grid {
    grid-template-columns: 1fr;
  }
}
</style>
