<template>
  <nav class="flow-stepper panel pad" aria-label="产品路径">
    <div class="flow-head">
      <div class="flow-copy">
        <span class="eyebrow">产品路径</span>
        <b>{{ currentLabel }}</b>
        <small>围绕品牌约束、信号、洞察与机会逐步完成本轮选品判断。</small>
      </div>
      <div class="flow-head-side">
        <div class="flow-progress">
          <span>已完成进度</span>
          <b>{{ completedStepCount }}/{{ steps.length }}</b>
          <small>{{ progressText }}</small>
        </div>
        <div v-if="nextStageLabel" class="next-stage">
          <span>下一步</span>
          <b>{{ nextStageLabel }}</b>
          <small>{{ nextStageSummary }}</small>
        </div>
      </div>
    </div>
    <div class="flow-ribbon">
      <article>
        <span>当前阶段</span>
        <b>{{ currentLabel }}</b>
        <small>{{ currentStageSummary }}</small>
      </article>
      <article>
        <span>最佳机会</span>
        <b>#{{ props.bestCardId }}</b>
        <small>当前主链路默认回落到该机会与报告页</small>
      </article>
      <article>
        <span>当前判断</span>
        <b>{{ activeStepStatusLabel }}</b>
        <small>{{ progressText }}</small>
      </article>
    </div>
    <ol class="flow-steps">
      <li
        v-for="step in steps"
        :key="step.name"
        :class="step.status"
      >
        <RouterLink :to="step.to">
          <span class="step-index">{{ step.index }}</span>
          <span class="step-copy">
            <span class="step-title">{{ step.title }}</span>
            <small class="step-summary">{{ step.summary }}</small>
          </span>
        </RouterLink>
      </li>
    </ol>
  </nav>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { getBrandId } from '@/composables/useBrandContext'

const props = withDefaults(defineProps<{
  current: string
  bestCardId?: number
  workflow?: import('@/types').WorkflowProgress
}>(), {
  bestCardId: 1
})

const route = useRoute()
const brandId = computed(() => {
  const queryId = Number(route.query.brandId)
  return Number.isFinite(queryId) && queryId > 0 ? queryId : getBrandId()
})

const baseSteps = [
  { name: 'data-prep', title: '数据准备' },
  { name: 'radar', title: '信号雷达' },
  { name: 'insight', title: '洞察发现' },
  { name: 'opportunity', title: '爆品机会' },
  { name: 'report', title: '选品报告' }
]

const steps = computed(() => baseSteps.map((step, index) => {
  const workflowStage = props.workflow?.stages.find((item) => item.key === step.name)
  return {
    ...step,
    index: index + 1,
    status: workflowStage?.status ?? fallbackStatus(step.name),
    summary: workflowStage?.summary ?? '',
    to: step.name === 'data-prep'
      ? { path: '/data-prep', query: { brandId: brandId.value } }
      : step.name === 'radar'
        ? { path: '/radar', query: { brandId: brandId.value } }
        : step.name === 'insight'
          ? { path: '/insight', query: { brandId: brandId.value } }
          : step.name === 'opportunity'
            ? { path: `/opportunity/${props.bestCardId}`, query: { brandId: brandId.value } }
            : { path: `/report/${props.bestCardId}`, query: { brandId: brandId.value } }
  }
}))

const currentLabel = computed(() => steps.value.find((step) => step.name === props.current)?.title ?? '')
const currentStageSummary = computed(() =>
  steps.value.find((step) => step.name === props.current)?.summary ?? '当前阶段摘要待生成。'
)
const currentIndex = computed(() => baseSteps.findIndex((step) => step.name === props.current))
const nextStageName = computed(() => {
  if (currentIndex.value < 0 || currentIndex.value >= baseSteps.length - 1) return ''
  return baseSteps[currentIndex.value + 1]?.name ?? ''
})
const nextStageLabel = computed(() =>
  steps.value.find((step) => step.name === nextStageName.value)?.title ?? ''
)
const completedStepCount = computed(() => steps.value.filter((step) => step.status === 'done').length)
const activeStepStatusLabel = computed(() => {
  const stage = steps.value.find((step) => step.name === props.current)
  if (stage?.status === 'done') return '已完成'
  if (stage?.status === 'current') return '进行中'
  return '待推进'
})
const progressText = computed(() => {
  if (!props.workflow) {
    return '等待建立主流程'
  }
  return `当前位于${currentLabel.value}，已形成 ${props.workflow.signalCount} 条信号与 ${props.workflow.opportunityCount} 个机会点。`
})
const nextStageSummary = computed(() =>
  props.workflow?.stages.find((item) => item.key === nextStageName.value)?.summary ?? ''
)

function stepIndex(name: string) {
  return baseSteps.findIndex((step) => step.name === name)
}

function fallbackStatus(name: string) {
  const targetIndex = stepIndex(name)
  const currentIndex = stepIndex(props.current)
  if (targetIndex < currentIndex) return 'done'
  if (targetIndex === currentIndex) return 'current'
  return 'pending'
}
</script>

<style scoped>
.flow-stepper {
  margin-bottom: 18px;
  padding: 18px 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.99), rgba(246, 249, 254, 0.98));
}

.flow-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.flow-copy {
  min-width: 0;
}

.eyebrow {
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
}

.flow-head b {
  display: block;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 20px;
  margin-top: 6px;
}

.flow-head small {
  display: block;
  margin-top: 8px;
  color: var(--muted);
  line-height: 1.6;
}

.flow-head-side {
  display: grid;
  grid-template-columns: repeat(2, minmax(180px, 1fr));
  gap: 12px;
  min-width: 420px;
}

.flow-progress,
.next-stage {
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.82);
  box-shadow: var(--shadow-sm);
}

.flow-progress span,
.next-stage span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.flow-progress b,
.next-stage b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
}

.flow-progress b {
  font-size: 18px;
}

.next-stage b {
  font-size: 20px;
}

.flow-progress small,
.next-stage small {
  display: block;
  margin-top: 8px;
  color: var(--muted);
  line-height: 1.6;
}

.flow-ribbon {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.flow-ribbon article {
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.flow-ribbon span,
.flow-ribbon small {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.flow-ribbon b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 18px;
  line-height: 1.5;
}

.flow-ribbon small {
  margin-top: 10px;
  line-height: 1.6;
}

.flow-steps {
  position: relative;
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 8px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.flow-steps::before {
  content: '';
  position: absolute;
  left: 24px;
  right: 24px;
  top: 28px;
  height: 1px;
  background: linear-gradient(90deg, rgba(37, 99, 235, 0.12), rgba(148, 163, 184, 0.18));
}

.flow-steps li {
  position: relative;
  z-index: 1;
}

.flow-steps a {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 14px;
  border-radius: 14px;
  border: 1px solid var(--line);
  text-decoration: none;
  color: var(--muted);
  background: rgba(255, 255, 255, 0.88);
  transition: border-color 0.2s ease, background 0.2s ease, color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

.flow-steps li.current a {
  border-color: rgba(37, 99, 235, 0.28);
  background: linear-gradient(180deg, rgba(239, 246, 255, 0.98), rgba(243, 248, 255, 0.98));
  color: var(--ink-strong);
  box-shadow: 0 12px 24px rgba(37, 99, 235, 0.08);
}

.flow-steps li.done a {
  border-color: rgba(22, 163, 74, 0.18);
  color: #1f6f43;
  background: rgba(240, 253, 244, 0.94);
}

.flow-steps li.pending a:hover {
  transform: translateY(-1px);
  border-color: rgba(37, 99, 235, 0.14);
  box-shadow: var(--shadow-sm);
}

.step-index {
  flex: 0 0 28px;
  width: 28px;
  height: 28px;
  border-radius: 999px;
  display: grid;
  place-items: center;
  font-family: "IBM Plex Mono", monospace;
  font-size: 12px;
  background: rgba(37, 99, 235, 0.08);
  border: 1px solid rgba(37, 99, 235, 0.12);
}

.flow-steps li.done .step-index {
  background: rgba(22, 163, 74, 0.12);
  border-color: rgba(22, 163, 74, 0.18);
}

.flow-steps li.current .step-index {
  background: rgba(37, 99, 235, 0.14);
  border-color: rgba(37, 99, 235, 0.18);
}

.step-copy {
  display: grid;
  gap: 4px;
}

.step-title {
  font-size: 13px;
  font-weight: 700;
}

.step-summary {
  color: var(--muted);
  font-size: 12px;
  line-height: 1.5;
}

@media (max-width: 1280px) {
  .flow-head {
    display: grid;
  }

  .flow-head-side {
    min-width: 0;
  }
}

@media (max-width: 1100px) {
  .flow-head {
    display: grid;
  }

  .flow-head-side {
    min-width: 0;
    grid-template-columns: 1fr;
  }

  .flow-steps {
    grid-template-columns: 1fr 1fr;
  }

  .flow-steps::before {
    display: none;
  }

  .flow-ribbon {
    grid-template-columns: 1fr;
  }
}
</style>
