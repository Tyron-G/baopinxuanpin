<template>
  <section class="panel pad">
    <span class="eyebrow">推进状态摘要</span>
    <h2>{{ title }}</h2>
    <div class="summary-ribbon">
      <article>
        <span>动作总数</span>
        <b>{{ totalCount }}</b>
      </article>
      <article>
        <span>完成进度</span>
        <b>{{ completedCount }}/{{ totalCount }}</b>
      </article>
      <article>
        <span>当前主焦点</span>
        <b>{{ focusActionStatus }}</b>
      </article>
    </div>
    <div class="mini-grid">
      <div>
        <span>动作总数</span>
        <b>{{ totalCount }}</b>
      </div>
      <div>
        <span>已完成</span>
        <b>{{ completedCount }}</b>
      </div>
      <div>
        <span>进行中</span>
        <b>{{ inProgressCount }}</b>
      </div>
      <div>
        <span>待推进</span>
        <b>{{ pendingCount }}</b>
      </div>
    </div>
    <p class="narrative-copy">{{ summary }}</p>
    <div class="focus-reason-list">
      <article class="focus-reason-card">
        <span>当前主焦点</span>
        <b>{{ focusActionTitle }}（{{ focusActionStatus }}）</b>
      </article>
      <article class="focus-reason-card">
        <span>最近更新时间</span>
        <b>{{ latestUpdatedAt }}</b>
      </article>
    </div>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  totalCount: number
  completedCount: number
  inProgressCount: number
  pendingCount: number
  focusActionTitle: string
  focusActionStatus: string
  latestUpdatedAt: string
  summary: string
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

.focus-reason-list {
  display: grid;
  gap: 12px;
  margin-top: 14px;
}

.focus-reason-card {
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 251, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.focus-reason-card span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.focus-reason-card b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  line-height: 1.7;
}

@media (max-width: 900px) {
  .summary-ribbon,
  .mini-grid {
    grid-template-columns: 1fr;
  }
}
</style>
