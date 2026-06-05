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
          <span>流程进度</span>
          <b>{{ activeStepCount }}/{{ steps.length }}</b>
          <small>{{ progressText }}</small>
        </div>
        <div v-if="nextStageLabel && !isFinalStep" class="next-stage">
          <span>下一步</span>
          <b>{{ nextStageLabel }}</b>
          <small>{{ nextStageSummary }}</small>
        </div>
        <div v-else-if="isFinalStep" class="next-stage next-stage--done">
          <span>流程状态</span>
          <b>已到终态</b>
          <small>本轮选品判断已推进至报告输出，可导出或返回机会页复核。</small>
        </div>
      </div>
    </div>
    <div class="flow-ribbon">
      <article>
        <span>当前阶段</span>
        <b>{{ currentLabel }}</b>
        <small>{{ currentStageSummary }}</small>
      </article>
      <article class="best-opportunity-card">
        <span>最佳机会</span>
        <RouterLink
          v-if="bestCardId"
          :to="bestOpportunityLink"
          class="best-opportunity-link"
         >
          <b>{{ bestCategoryLabel }}</b>
        </RouterLink>
        <b v-else>{{ bestCategoryLabel }}</b>
        <small>{{ bestOpportunityHint }}</small>
      </article>
      <article>
        <span>当前判断</span>
        <b>{{ activeStepStatusLabel }}</b>
        <small>{{ judgmentHint }}</small>
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
import { resolvePlatform } from '@/composables/usePlatformContext'
import {
  PRODUCT_PATH_STEPS,
  PRODUCT_PATH_TOTAL,
  computeProductPathProgress,
  resolveProductPathStage,
  routeBasedStepStatus
} from '@/constants/productPath'

const props = defineProps<{
  current: string
  bestCardId?: number | null
  bestCategoryName?: string
  bestScore?: number
  workflow?: import('@/types').WorkflowProgress
}>()

const route = useRoute()
const brandId = computed(() => {
  const queryId = Number(route.query.brandId)
  return Number.isFinite(queryId) && queryId > 0 ? queryId : getBrandId()
})

const routePlatform = computed(() => resolvePlatform(route.query.platform))

const bestCardId = computed(() => {
  if (props.bestCardId != null && props.bestCardId > 0) {
    return props.bestCardId
  }
  return null
})

const bestOpportunityLink = computed(() => ({
  path: `/opportunity/${bestCardId.value}`,
  query: { brandId: brandId.value, platform: routePlatform.value }
}))

const bestCategoryLabel = computed(() => {
  const name = props.bestCategoryName?.trim()
  if (name && name !== '-') return name
  if (bestCardId.value) return `候选 #${bestCardId.value}`
  return '待生成'
})

const bestOpportunityHint = computed(() => {
  const name = props.bestCategoryName?.trim()
  if (name && name !== '-' && props.bestScore) {
    return `机会分 ${props.bestScore} · 点击跳转机会详情`
  }
  if (name && name !== '-') {
    return '当前报告对应赛道 · 点击跳转机会详情'
  }
  if (bestCardId.value) {
    return `卡片 #${bestCardId.value} · 完成洞察后自动更新`
  }
  return '完成数据准备与洞察分析后显示最佳候选'
})

const baseSteps = PRODUCT_PATH_STEPS.map(({ key, title }) => ({ name: key, title }))

const pathProgress = computed(() => computeProductPathProgress(props.current))
const currentRouteIndex = computed(() => pathProgress.value.index)

function routeBasedStatus(stepName: string) {
  return routeBasedStepStatus(stepName, props.current)
}

const steps = computed(() => baseSteps.map((step, index) => {
  const stageDetail = resolveProductPathStage(step.name, props.workflow?.stages)
  const baseQuery = { brandId: brandId.value, platform: routePlatform.value }
  return {
    ...step,
    index: index + 1,
    status: routeBasedStatus(step.name),
    summary: stageDetail?.hint || stageDetail?.summary || '',
    to: step.name === 'data-prep'
      ? { path: '/data-prep', query: baseQuery }
      : step.name === 'radar'
        ? { path: '/radar', query: baseQuery }
        : step.name === 'insight'
          ? { path: '/insight', query: baseQuery }
          : step.name === 'ranking'
            ? { path: '/ranking', query: baseQuery }
            : step.name === 'opportunity'
            ? bestCardId.value
              ? { path: `/opportunity/${bestCardId.value}`, query: baseQuery }
              : { path: '/insight', query: baseQuery }
            : bestCardId.value
              ? { path: `/report/${bestCardId.value}`, query: baseQuery }
              : { path: '/ranking', query: baseQuery }
  }
}))

const isFinalStep = computed(() => props.current === 'report')
const currentLabel = computed(() => steps.value.find((step) => step.name === props.current)?.title ?? '')
const currentStageSummary = computed(() => {
  const summary = steps.value.find((step) => step.name === props.current)?.summary
  if (summary) return summary
  return resolveProductPathStage(props.current, props.workflow?.stages)?.summary ?? '当前阶段摘要待生成。'
})
const nextStageName = computed(() => pathProgress.value.nextStageKey)
const nextStageLabel = computed(() =>
  resolveProductPathStage(nextStageName.value, props.workflow?.stages)?.title ?? ''
)
const completedStepCount = computed(() => pathProgress.value.completedBeforeCurrent)
const activeStepCount = computed(() => pathProgress.value.activeStepCount)
const activeStepStatusLabel = computed(() => {
  if (props.current === 'report') return '终态输出'
  if (currentRouteIndex.value < 0) return '待推进'
  return '进行中'
})
const progressText = computed(() => {
  const total = PRODUCT_PATH_TOTAL
  const workflowHint = props.workflow
    ? `已形成 ${props.workflow.signalCount} 条信号、${props.workflow.opportunityCount} 个机会点。`
    : ''
  if (pathProgress.value.isFinalStage) {
    return `${workflowHint}当前处于第 ${total}/${total} 步「${currentLabel.value}」，前序 ${completedStepCount.value} 步已完成。`.trim()
  }
  return `${workflowHint}当前推进至第 ${activeStepCount.value}/${total} 步「${currentLabel.value}」，前序 ${completedStepCount.value} 步已完成。`.trim()
})
const judgmentHint = computed(() => {
  if (isFinalStep.value) {
    return props.workflow?.reportReady
      ? '洞察与机会数据已齐备，可导出正式报告。'
      : '报告页：支持 Markdown / PDF / Excel 导出。'
  }
  if (props.workflow?.reportReady && props.current === 'opportunity') {
    return '机会数据已齐备，可进入报告页导出正式结论。'
  }
  return currentStageSummary.value
})
const nextStageSummary = computed(() =>
  resolveProductPathStage(nextStageName.value, props.workflow?.stages)?.summary ?? ''
)

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

.best-opportunity-link {
  display: block;
  margin-top: 8px;
  text-decoration: none;
  color: inherit;
}

.best-opportunity-link:hover b {
  color: var(--accent);
}

.next-stage--done {
  border-color: rgba(22, 163, 74, 0.18);
  background: rgba(240, 253, 244, 0.82);
}

.flow-ribbon small {
  margin-top: 10px;
  line-height: 1.6;
}

.flow-steps {
  position: relative;
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
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

  .flow-steps {
    grid-template-columns: repeat(6, minmax(108px, 1fr));
    overflow-x: auto;
    padding-bottom: 4px;
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
