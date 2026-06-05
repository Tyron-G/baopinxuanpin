<template>
  <section v-if="workflow && currentStageDetail" class="workflow-summary panel pad">
    <div class="workflow-main">
      <div class="workflow-copy">
        <span class="eyebrow">当前阶段</span>
        <h2>{{ currentStageDetail.title }}</h2>
        <p>{{ currentStageDetail.summary }}</p>
      </div>
      <div class="workflow-side">
        <div class="workflow-progress">
          <span>流程节奏</span>
          <b>{{ activeStepCount }}/{{ totalSteps }}</b>
          <small>{{ progressText }}</small>
        </div>
        <div class="workflow-next" :class="{ 'workflow-next--done': isFinalStage }">
          <span>{{ isFinalStage ? '流程状态' : '下一步' }}</span>
          <b>{{ isFinalStage ? '已到终态' : (nextStageDetail?.title ?? currentStageDetail.title) }}</b>
          <small>{{ isFinalStage ? finalStageHint : currentStageDetail.nextAction }}</small>
        </div>
      </div>
    </div>

    <div class="workflow-ribbon">
      <article>
        <span>当前状态</span>
        <b>{{ currentStageStatusLabel }}</b>
        <small>{{ currentStageDetail.nextAction }}</small>
      </article>
      <article>
        <span>{{ isFinalStage ? '终态说明' : '下一步焦点' }}</span>
        <b>{{ isFinalStage ? '报告可交付' : (nextStageDetail?.title ?? currentStageDetail.title) }}</b>
        <small>{{ isFinalStage ? finalStageHint : (nextStageDetail?.summary ?? currentStageDetail.summary) }}</small>
      </article>
      <article>
        <span>进度比例</span>
        <b>{{ progressPercent }}%</b>
        <div class="workflow-progress-track">
          <span class="workflow-progress-fill" :style="{ width: `${progressPercent}%` }"></span>
        </div>
      </article>
    </div>

    <div class="workflow-metrics">
      <div>
        <span>品牌</span>
        <b>{{ workflow.brandName }}</b>
      </div>
      <div>
        <span>信号</span>
        <b>{{ workflow.signalCount }}</b>
      </div>
      <div>
        <span>赛道</span>
        <b>{{ workflow.insightCount }}</b>
      </div>
      <div>
        <span>机会点</span>
        <b>{{ workflow.opportunityCount }}</b>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { WorkflowProgress } from '@/types'
import {
  PRODUCT_PATH_TOTAL,
  computeProductPathProgress,
  resolveProductPathStage
} from '@/constants/productPath'

const props = defineProps<{
  workflow?: WorkflowProgress
  currentStage: string
}>()

const workflow = computed(() => props.workflow)

function resolveStageDetail(stageKey: string) {
  return resolveProductPathStage(stageKey, props.workflow?.stages)
}

const currentStageDetail = computed(() => resolveStageDetail(props.currentStage))

const pathProgress = computed(() => computeProductPathProgress(props.currentStage))
const totalSteps = computed(() => PRODUCT_PATH_TOTAL)
const activeStepCount = computed(() => pathProgress.value.activeStepCount)
const completedBeforeCurrent = computed(() => pathProgress.value.completedBeforeCurrent)
const isFinalStage = computed(() => pathProgress.value.isFinalStage)
const nextStageKey = computed(() => pathProgress.value.nextStageKey)
const nextStageDetail = computed(() => {
  if (!nextStageKey.value) return undefined
  return resolveStageDetail(nextStageKey.value)
})
const progressPercent = computed(() => pathProgress.value.progressPercent)
const currentStageStatusLabel = computed(() => {
  if (props.currentStage === 'report') return props.workflow?.reportReady ? '可交付' : '终态输出'
  if (pathProgress.value.index < 0) return '待推进'
  return '进行中'
})
const finalStageHint = computed(() =>
  props.workflow?.reportReady
    ? '本轮选品判断已推进至报告输出，可导出或返回机会页复核。'
    : '报告页已就绪，完成复核后可导出正式结论。'
)
const progressText = computed(() => {
  const metricsHint = props.workflow
    ? `当前已识别 ${props.workflow.signalCount} 条信号、${props.workflow.insightCount} 个赛道、${props.workflow.opportunityCount} 个机会点。`
    : '等待启动本轮流程'
  if (isFinalStage.value) {
    return `${metricsHint} 当前处于第 ${totalSteps.value}/${totalSteps.value} 步，前序 ${completedBeforeCurrent.value} 步已完成。`.trim()
  }
  return `${metricsHint} 当前推进至第 ${activeStepCount.value}/${totalSteps.value} 步，前序 ${completedBeforeCurrent.value} 步已完成。`.trim()
})
</script>

<style scoped>
.workflow-summary {
  margin-bottom: 16px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.99), rgba(248, 251, 255, 0.98)),
    #ffffff;
}

.workflow-main {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.workflow-copy {
  min-width: 0;
}

.workflow-side {
  display: grid;
  grid-template-columns: repeat(2, minmax(180px, 1fr));
  gap: 12px;
  min-width: 420px;
}

.eyebrow {
  display: inline-block;
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
}

.workflow-main h2 {
  margin: 10px 0 0;
  color: var(--ink-strong);
}

.workflow-main p {
  margin: 10px 0 0;
  color: var(--muted);
  line-height: 1.7;
}

.workflow-copy::after {
  content: '';
  display: block;
  width: 72px;
  height: 3px;
  margin-top: 18px;
  border-radius: 999px;
  background: linear-gradient(90deg, rgba(37, 99, 235, 0.48), rgba(15, 118, 110, 0.28));
}

.workflow-progress,
.workflow-next {
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background: linear-gradient(180deg, rgba(247, 250, 255, 0.98), rgba(240, 246, 253, 0.98));
  box-shadow: var(--shadow-sm);
}

.workflow-progress span,
.workflow-next span,
.workflow-metrics span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.workflow-progress b,
.workflow-next b,
.workflow-metrics b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
}

.workflow-progress small,
.workflow-next small {
  display: block;
  margin-top: 8px;
  color: var(--muted);
  line-height: 1.6;
}

.workflow-next--done {
  border-color: rgba(22, 163, 74, 0.18);
  background: rgba(240, 253, 244, 0.82);
}

.workflow-ribbon {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.workflow-ribbon article {
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.workflow-ribbon span,
.workflow-ribbon small {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.workflow-ribbon b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 18px;
  line-height: 1.5;
}

.workflow-ribbon small {
  margin-top: 10px;
  line-height: 1.6;
}

.workflow-progress-track {
  height: 8px;
  margin-top: 12px;
  overflow: hidden;
  border-radius: 999px;
  background: rgba(226, 232, 240, 0.94);
}

.workflow-progress-fill {
  display: block;
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, rgba(37, 99, 235, 0.92), rgba(15, 118, 110, 0.82));
}

.workflow-metrics {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.workflow-metrics div {
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(248, 251, 255, 0.94)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

@media (max-width: 1280px) {
  .workflow-main {
    display: grid;
  }

  .workflow-side {
    min-width: 0;
  }
}

@media (max-width: 900px) {
  .workflow-main {
    display: grid;
  }

  .workflow-side {
    min-width: 0;
    grid-template-columns: 1fr;
  }

  .workflow-ribbon {
    grid-template-columns: 1fr;
  }

  .workflow-metrics {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
