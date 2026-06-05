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
          <b>{{ completedStageCount }}/{{ workflow.stages.length }}</b>
          <small>{{ progressText }}</small>
        </div>
        <div class="workflow-next">
          <span>下一步</span>
          <b>{{ nextStageDetail?.title ?? currentStageDetail.title }}</b>
          <small>{{ currentStageDetail.nextAction }}</small>
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
        <span>下一步焦点</span>
        <b>{{ nextStageDetail?.title ?? currentStageDetail.title }}</b>
        <small>{{ nextStageDetail?.summary ?? currentStageDetail.summary }}</small>
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

const props = defineProps<{
  workflow?: WorkflowProgress
  currentStage: string
}>()

const workflow = computed(() => props.workflow)
const fallbackStages = [
  { key: 'data-prep', title: '数据准备', status: 'pending', summary: '品牌约束、平台、预算和供应链边界已录入。', nextAction: '生成品牌上下文' },
  { key: 'radar', title: '信号雷达', status: 'pending', summary: '系统聚焦今日最值得跟进的搜索、社媒和差评信号。', nextAction: '确认高优先级信号' },
  { key: 'insight', title: '洞察发现', status: 'pending', summary: '系统从趋势、竞争和供需三个维度筛出可进入赛道。', nextAction: '选择优先深挖的赛道' },
  { key: 'opportunity', title: '爆品机会', status: 'pending', summary: '系统输出机会点、风险、利润和供应链可行性结论。', nextAction: '确认立项、观望或放弃' },
  { key: 'report', title: '选品报告', status: 'pending', summary: '系统已具备沉淀报告所需信息，可对外输出本轮结论。', nextAction: '导出报告并沉淀判断' }
]

const currentStageDetail = computed(() =>
  props.workflow?.stages.find((item) => item.key === props.currentStage)
  ?? fallbackStages.find((item) => item.key === props.currentStage)
)

const currentStageIndex = computed(() => fallbackStages.findIndex((item) => item.key === props.currentStage))
const nextStageKey = computed(() => {
  if (currentStageIndex.value < 0 || currentStageIndex.value >= fallbackStages.length - 1) {
    return props.currentStage
  }
  return fallbackStages[currentStageIndex.value + 1]?.key ?? props.currentStage
})
const nextStageDetail = computed(() => {
  return props.workflow?.stages.find((item) => item.key === nextStageKey.value)
    ?? fallbackStages.find((item) => item.key === nextStageKey.value)
})
const completedStageCount = computed(() => props.workflow?.stages.filter((item) => item.status === 'done').length ?? 0)
const progressPercent = computed(() => {
  const total = props.workflow?.stages.length ?? 5
  const currentProgress = currentStageIndex.value >= 0 ? currentStageIndex.value + 1 : 1
  return Math.max(Math.round((completedStageCount.value / total) * 100), Math.round((currentProgress / total) * 100))
})
const currentStageStatusLabel = computed(() => {
  const status = currentStageDetail.value?.status
  if (status === 'done') return '已完成'
  if (status === 'current') return '进行中'
  if (props.currentStage === 'report') return '可交付'
  return '待推进'
})
const progressText = computed(() => {
  if (!props.workflow) return '等待启动本轮流程'
  return `当前已识别 ${props.workflow.signalCount} 条信号、${props.workflow.insightCount} 个赛道、${props.workflow.opportunityCount} 个机会点。`
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
