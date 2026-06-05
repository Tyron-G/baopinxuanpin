<template>
  <header class="page-hero panel pad">
    <div class="hero-main">
      <div class="hero-copy">
        <span class="eyebrow">{{ eyebrow }}</span>
        <h1>{{ title }}</h1>
        <p>{{ description }}</p>
      </div>
      <div v-if="$slots.actions" class="hero-actions">
        <slot name="actions" />
      </div>
    </div>
    <div class="hero-foot">
      <div class="hero-note">
        <span>当前页面</span>
        <b>{{ eyebrow }}</b>
      </div>
      <div class="hero-note">
        <span>阅读方式</span>
        <b>先看摘要，再进明细</b>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
defineProps<{
  eyebrow: string
  title: string
  description: string
}>()
</script>

<style scoped>
.page-hero {
  display: grid;
  gap: 16px;
  margin-bottom: 18px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.985), rgba(244, 248, 253, 0.98)),
    #ffffff;
}

.hero-main {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 18px;
  align-items: end;
}

.eyebrow {
  display: block;
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
}

.hero-copy h1 {
  margin: 10px 0 0;
  color: var(--ink-strong);
  font-size: 34px;
  line-height: 1.2;
}

.hero-copy p {
  margin: 10px 0 0;
  max-width: 820px;
  color: var(--muted);
  line-height: 1.8;
}

.hero-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.hero-foot {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 220px));
  gap: 12px;
}

.hero-note {
  padding: 12px 14px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(251, 252, 255, 0.98), rgba(246, 249, 253, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.hero-note span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.hero-note b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-size: 14px;
  line-height: 1.5;
}

@media (max-width: 900px) {
  .hero-main,
  .hero-foot {
    grid-template-columns: 1fr;
  }

  .hero-actions {
    justify-content: flex-start;
  }
}
</style>
