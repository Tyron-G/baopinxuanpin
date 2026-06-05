<template>
  <section class="panel pad entry-panel">
    <div class="entry-head">
      <div>
        <span class="eyebrow">入场判断</span>
        <h2>{{ decision }}</h2>
        <p>{{ headline }}</p>
      </div>
      <el-tag :type="tagType" effect="plain" size="large">{{ firstLaunchPlatform }} 先行验证</el-tag>
    </div>

    <div class="entry-grid">
      <article class="entry-card">
        <span>进入窗口</span>
        <b>{{ entryWindow }}</b>
        <p>优先在这个时间窗内完成首轮样品、素材与转化验证。</p>
      </article>
      <article class="entry-card">
        <span>利润空间</span>
        <b>{{ netMargin }}</b>
        <p>结合当前目标售价和广告成本，先确认是否能支撑冷启动试错。</p>
      </article>
      <article class="entry-card">
        <span>供应链风险</span>
        <b>{{ riskHint }}</b>
        <p>先排查会拖慢试产和履约的关键变量，避免验证阶段被供应链卡住。</p>
      </article>
      <article class="entry-card">
        <span>平台路径</span>
        <b>{{ firstLaunchPlatform }} -> {{ validationPlatform }}</b>
        <p>先在首发平台起量，再到验证平台确认内容或搜索承接是否成立。</p>
      </article>
    </div>

    <div class="entry-notes">
      <div class="entry-note">
        <span>首轮动作</span>
        <b>先做 7-14 天小样验证</b>
      </div>
      <div class="entry-note">
        <span>机会周期</span>
        <b>{{ lifecycleStage ? `${lifecycleStage} / ${entryTiming}` : entryWindow }}</b>
      </div>
      <div class="entry-note">
        <span>验证重点</span>
        <b>{{ firstLaunchPlatform }} 首发，{{ validationPlatform }} 校验反馈</b>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  decision: string
  headline: string
  entryWindow: string
  netMargin: string
  riskHint: string
  firstLaunchPlatform: string
  validationPlatform: string
  lifecycleStage?: string
  entryTiming?: string
}>()

const tagType = computed(() => {
  if (props.decision.includes('推荐')) return 'danger'
  if (props.decision.includes('观望')) return 'warning'
  return 'info'
})
</script>

<style scoped>
.entry-panel {
  margin-bottom: 16px;
}

.entry-head {
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

.entry-head h2 {
  margin: 10px 0 0;
  color: var(--ink-strong);
  font-size: 22px;
}

.entry-head p {
  margin: 8px 0 0;
  color: var(--muted);
  line-height: 1.75;
}

.entry-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  margin-top: 16px;
}

.entry-card,
.entry-note {
  padding: 14px;
  border: 1px solid var(--line);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.82);
}

.entry-card span,
.entry-note span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.entry-card b,
.entry-note b {
  display: block;
  margin-top: 6px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
}

.entry-card p {
  margin: 10px 0 0;
  color: var(--muted);
  line-height: 1.7;
}

.entry-notes {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-top: 14px;
}

@media (max-width: 1100px) {
  .entry-grid,
  .entry-notes {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .entry-head,
  .entry-grid,
  .entry-notes {
    display: grid;
  }
}
</style>
