import type { WorkflowStage } from '@/types'

/** 6 步产品路径定义，与 WorkflowStepper / WorkflowSummary 共用（2026-06-05） */
export interface ProductPathStep {
  key: string
  title: string
  summary: string
  nextAction: string
  hint: string
}

export const PRODUCT_PATH_STEPS: ProductPathStep[] = [
  {
    key: 'data-prep',
    title: '数据准备',
    summary: '品牌约束、平台、预算和供应链边界已录入。',
    nextAction: '生成品牌上下文',
    hint: '录入品牌约束与目标平台'
  },
  {
    key: 'radar',
    title: '信号雷达',
    summary: '系统聚焦今日最值得跟进的搜索、社媒和差评信号。',
    nextAction: '确认高优先级信号',
    hint: '扫描今日强信号'
  },
  {
    key: 'insight',
    title: '洞察发现',
    summary: '系统从趋势、竞争和供需三个维度筛出可进入赛道。',
    nextAction: '选择优先深挖的赛道',
    hint: '三维度筛选候选赛道'
  },
  {
    key: 'ranking',
    title: '机会榜单',
    summary: '查看 TOP50 机会榜单',
    nextAction: '锁定优先验证的候选',
    hint: '查看 TOP50 机会榜单'
  },
  {
    key: 'opportunity',
    title: '爆品机会',
    summary: '系统输出机会点、风险、利润和供应链可行性结论。',
    nextAction: '确认立项、观望或放弃',
    hint: '深度验证机会点'
  },
  {
    key: 'report',
    title: '选品报告',
    summary: '系统已具备沉淀报告所需信息，可对外输出本轮结论。',
    nextAction: '导出报告并沉淀判断',
    hint: '导出 Markdown / PDF / Excel'
  }
]

export const PRODUCT_PATH_TOTAL = PRODUCT_PATH_STEPS.length

export function findProductPathIndex(stageKey: string): number {
  return PRODUCT_PATH_STEPS.findIndex((step) => step.key === stageKey)
}

export function resolveProductPathStage(stageKey: string, apiStages?: WorkflowStage[]) {
  const fallback = PRODUCT_PATH_STEPS.find((item) => item.key === stageKey)
  const fromApi = apiStages?.find((item) => item.key === stageKey)
  if (!fromApi && !fallback) return undefined
  return {
    key: stageKey,
    title: fromApi?.title ?? fallback?.title ?? stageKey,
    summary: fromApi?.summary ?? fallback?.summary ?? '',
    nextAction: fromApi?.nextAction ?? fallback?.nextAction ?? '',
    hint: fallback?.hint ?? fromApi?.summary ?? ''
  }
}

export function computeProductPathProgress(currentStage: string) {
  const index = findProductPathIndex(currentStage)
  const totalSteps = PRODUCT_PATH_TOTAL
  const activeStepCount = index < 0 ? 0 : index + 1
  const completedBeforeCurrent = Math.max(0, index)
  const isFinalStage = currentStage === 'report'
  const progressPercent = totalSteps <= 0 ? 0 : Math.round((activeStepCount / totalSteps) * 100)
  const nextStageKey = index < 0 || isFinalStage ? '' : (PRODUCT_PATH_STEPS[index + 1]?.key ?? '')
  return {
    index,
    totalSteps,
    activeStepCount,
    completedBeforeCurrent,
    isFinalStage,
    progressPercent,
    nextStageKey
  }
}

export function routeBasedStepStatus(stepKey: string, currentStage: string) {
  const stepIndex = findProductPathIndex(stepKey)
  const activeIndex = findProductPathIndex(currentStage)
  if (activeIndex < 0 || stepIndex < 0) return 'pending' as const
  if (stepIndex < activeIndex) return 'done' as const
  if (stepIndex === activeIndex) return 'current' as const
  return 'pending' as const
}
