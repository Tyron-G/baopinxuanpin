<template>
  <component :is="wrapperTag" :class="wrapperClass">
    <span class="eyebrow">平台切入建议</span>
    <h2 v-if="variant === 'panel'">{{ title }}</h2>
    <h3 v-else>{{ firstLaunchPlatform }} 首发</h3>
    <div class="summary-ribbon">
      <article>
        <span>首发平台</span>
        <b>{{ firstLaunchPlatform }}</b>
      </article>
      <article>
        <span>验证平台</span>
        <b>{{ validationPlatform }}</b>
      </article>
      <article>
        <span>转化平台</span>
        <b>{{ conversionPlatform }}</b>
      </article>
    </div>
    <div class="mini-grid">
      <div>
        <span>首发平台</span>
        <b>{{ firstLaunchPlatform }}</b>
      </div>
      <div>
        <span>验证平台</span>
        <b>{{ validationPlatform }}</b>
      </div>
      <div>
        <span>转化平台</span>
        <b>{{ conversionPlatform }}</b>
      </div>
    </div>
    <p class="narrative-copy">{{ summary }}</p>
    <ul v-if="executionHints.length" class="hint-list">
      <li v-for="item in executionHints" :key="item">{{ item }}</li>
    </ul>
  </component>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  title?: string
  firstLaunchPlatform: string
  validationPlatform: string
  conversionPlatform: string
  summary: string
  executionHints: string[]
  variant?: 'panel' | 'card'
}>(), {
  title: '先在哪个平台起量',
  variant: 'panel'
})

const wrapperTag = computed(() => props.variant === 'panel' ? 'section' : 'article')
const wrapperClass = computed(() => props.variant === 'panel' ? 'panel pad' : 'report-card')
</script>

<style scoped>
.eyebrow {
  display: block;
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
}

h2,
h3 {
  margin: 10px 0 0;
  color: var(--ink-strong);
}

h2 {
  font-size: 20px;
}

h3 {
  font-size: 16px;
}

.summary-ribbon {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.summary-ribbon article {
  padding: 12px 14px;
  border: 1px solid var(--line);
  border-radius: 12px;
  background: rgba(248, 251, 255, 0.9);
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
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
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

.hint-list {
  margin: 12px 0 0;
  padding-left: 18px;
  color: var(--ink);
  line-height: 1.7;
}

.report-card {
  padding: 16px;
  border: 1px solid var(--line);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.82);
}

@media (max-width: 900px) {
  .summary-ribbon,
  .mini-grid {
    grid-template-columns: 1fr;
  }
}
</style>
