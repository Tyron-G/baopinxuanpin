<template>
  <section class="panel pad decision-hero">
    <div class="decision-hero-main">
      <div class="decision-copy">
        <span class="eyebrow">{{ eyebrow }}</span>
        <h2>{{ title }}</h2>
        <p>{{ description }}</p>
      </div>
      <div v-if="contextItems.length" class="decision-context">
        <article v-for="item in contextItems" :key="item.label" class="context-card">
          <span>{{ item.label }}</span>
          <b>{{ item.value }}</b>
          <small v-if="item.hint">{{ item.hint }}</small>
        </article>
      </div>
    </div>

    <div v-if="overviewItems.length" class="decision-overview">
      <article v-for="item in overviewItems" :key="item.label" class="overview-card">
        <span>{{ item.label }}</span>
        <b>{{ item.value }}</b>
        <small v-if="item.hint">{{ item.hint }}</small>
      </article>
    </div>

    <div v-if="metricItems.length" class="decision-metrics">
      <article v-for="item in metricItems" :key="item.label" class="metric-card">
        <span>{{ item.label }}</span>
        <b>{{ item.value }}</b>
        <small v-if="item.hint">{{ item.hint }}</small>
      </article>
    </div>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  eyebrow: string
  title: string
  description: string
  contextItems: Array<{ label: string; value: string | number; hint?: string }>
  overviewItems: Array<{ label: string; value: string | number; hint?: string }>
  metricItems: Array<{ label: string; value: string | number; hint?: string }>
}>()
</script>

<style scoped>
.decision-hero {
  margin-bottom: 16px;
  background:
    linear-gradient(135deg, rgba(239, 246, 255, 0.92), rgba(240, 253, 250, 0.8)),
    #ffffff;
}

.decision-hero-main {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(320px, 0.9fr);
  gap: 18px;
}

.eyebrow {
  display: block;
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
}

.decision-copy h2 {
  margin: 10px 0 0;
  color: var(--ink-strong);
  font-size: 30px;
  line-height: 1.3;
}

.decision-copy p {
  margin: 12px 0 0;
  max-width: 760px;
  color: var(--muted);
  line-height: 1.8;
}

.decision-copy::after {
  content: '';
  display: block;
  width: 72px;
  height: 3px;
  margin-top: 18px;
  border-radius: 999px;
  background: linear-gradient(90deg, rgba(37, 99, 235, 0.5), rgba(15, 118, 110, 0.3));
}

.decision-context,
.decision-overview,
.decision-metrics {
  display: grid;
  gap: 12px;
}

.decision-context {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.decision-overview {
  grid-template-columns: repeat(4, minmax(0, 1fr));
  margin-top: 18px;
}

.decision-metrics {
  grid-template-columns: repeat(4, minmax(0, 1fr));
  margin-top: 12px;
}

.context-card,
.overview-card,
.metric-card {
  padding: 14px 16px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(247, 250, 255, 0.9)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.context-card span,
.overview-card span,
.metric-card span,
.context-card small,
.overview-card small,
.metric-card small {
  display: block;
}

.context-card span,
.overview-card span,
.metric-card span {
  color: var(--muted);
  font-size: 12px;
}

.context-card b,
.overview-card b,
.metric-card b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 20px;
  line-height: 1.4;
}

.context-card small,
.overview-card small,
.metric-card small {
  margin-top: 10px;
  color: var(--muted);
  line-height: 1.6;
}

@media (max-width: 1100px) {
  .decision-hero-main,
  .decision-overview,
  .decision-metrics {
    grid-template-columns: 1fr;
  }

  .decision-context {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 760px) {
  .decision-context {
    grid-template-columns: 1fr;
  }
}
</style>
