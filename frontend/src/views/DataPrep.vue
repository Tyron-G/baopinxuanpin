<template>
  <section>
    <PageHero
      eyebrow="数据准备"
      title="定义本轮选品边界"
      description="录入品牌约束、目标平台、预算和供应链资源，生成后续洞察分析的上下文。"
    >
      <template #actions>
        <el-button type="primary" :icon="TrendCharts" @click="goInsight">查看洞察</el-button>
      </template>
    </PageHero>

    <WorkflowSummary current-stage="data-prep" :workflow="workflow" />

    <el-alert
      v-if="!hasSavedBrand"
      class="prep-alert"
      type="info"
      show-icon
      :closable="false"
      title="建议先完成三步表单并点击「生成洞察」，系统会保存品牌上下文，后续页面将按您的预算与品类个性化推荐。"
    />

    <DataConnectorsPanel />

    <section class="panel pad industry-report-panel">
      <span class="eyebrow">行业报告（样例）</span>
      <h2>行业报告 · 社媒舆情 · 电商 · 搜索</h2>
      <p>与蝉妈妈/飞瓜样例 feed 交叉验证，驱动洞察页趋势与规模判断。</p>
      <ul class="industry-report-list">
        <li v-for="item in sampleIndustryReports" :key="item.title">
          <b>{{ item.title }}</b>
          <small>{{ item.source }} · {{ item.period }}</small>
          <span>{{ item.summary }}</span>
        </li>
      </ul>
    </section>

    <section class="panel pad prep-hero">
      <div class="prep-hero-copy">
        <span class="eyebrow">启动本轮选品</span>
        <h2>先把判断边界说清楚</h2>
        <p>这里不是简单录资料，而是在定义这轮机会筛选的业务边界。平台、预算、利润线和供应链会直接决定后面哪些赛道值得看、哪些可以先排除。</p>
      </div>
      <div class="prep-hero-metrics">
        <article>
          <span>目标平台</span>
          <b>{{ form.targetPlatforms.length }}</b>
          <small>{{ form.targetPlatforms.join(' / ') }}</small>
        </article>
        <article>
          <span>预算带</span>
          <b>{{ form.budgetRange }}</b>
          <small>决定冷启动试错空间</small>
        </article>
        <article>
          <span>利润下限</span>
          <b>{{ form.profitMin }}</b>
          <small>影响推荐顺序和进入门槛</small>
        </article>
      </div>
    </section>

    <section class="panel pad prep-intake">
      <div class="intake-head">
        <div>
          <span class="eyebrow">品牌建档</span>
          <h2>把品牌判断条件一次录清楚</h2>
          <p>先录品牌基础、再录选品目标，最后补排除边界。后续信号、洞察和机会页都会沿用这里的上下文。</p>
        </div>
        <div class="intake-summary">
          <article>
            <span>当前步骤</span>
            <b>{{ stepLabel }}</b>
            <small>{{ stepHint }}</small>
          </article>
          <article>
            <span>品牌状态</span>
            <b>{{ hasSavedBrand ? '已保存' : '待保存' }}</b>
            <small>{{ form.brandName || '等待录入品牌名称' }}</small>
          </article>
          <article>
            <span>建档完整度</span>
            <b>{{ completionScore }}%</b>
            <small>按平台、预算、利润、供应链和排除项估算</small>
          </article>
        </div>
      </div>
    </section>

    <div class="prep-shell">
      <aside class="prep-aside">
        <section class="panel pad prep-summary-card">
          <span class="eyebrow">配置摘要</span>
          <h2>品牌筛选约束</h2>
          <p>先锁定平台、预算和供应链边界，再进入趋势和竞争判断，避免后面的结论失焦。</p>
          <div class="summary-progress">
            <div class="summary-progress-head">
              <span>当前建档进度</span>
              <b>{{ completionScore }}%</b>
            </div>
            <div class="summary-progress-track">
              <span class="summary-progress-fill" :style="{ width: `${completionScore}%` }"></span>
            </div>
            <small>{{ readinessText }}</small>
          </div>
          <dl>
            <div>
              <dt>目标平台</dt>
              <dd>{{ form.targetPlatforms.join(' / ') }}</dd>
            </div>
            <div>
              <dt>预算带</dt>
              <dd>{{ form.budgetRange }}</dd>
            </div>
            <div>
              <dt>利润下限</dt>
              <dd>{{ form.profitMin }}</dd>
            </div>
            <div>
              <dt>备货周期</dt>
              <dd>{{ form.stockCycle }}</dd>
            </div>
          </dl>
        </section>

        <section class="panel pad prep-note-card">
          <span class="eyebrow">配置建议</span>
          <h2>先定边界，再看机会</h2>
          <ul>
            <li>平台先决定内容打法和转化承接路径。</li>
            <li>预算和利润线会直接影响候选赛道排序。</li>
            <li>供应链与备货周期会压住短期爆发类机会的可行性。</li>
          </ul>
        </section>

        <section class="panel pad prep-note-card prep-checklist-card">
          <span class="eyebrow">待补重点</span>
          <h2>还差哪些判断条件</h2>
          <ul>
            <li v-for="item in missingHighlights" :key="item">{{ item }}</li>
          </ul>
        </section>
      </aside>

      <div class="panel pad prep-form-card">
        <el-steps class="prep-steps" :active="activeStep" finish-status="success" simple>
          <el-step title="基础信息" />
          <el-step title="选品信息" />
          <el-step title="排除设置" />
        </el-steps>

        <div class="form-stage-banner">
          <div>
            <span class="eyebrow">当前录入区</span>
            <h2>{{ stepLabel }}</h2>
            <p>{{ stepHint }}</p>
          </div>
          <div class="stage-chip-row">
            <span class="stage-chip" :class="{ active: activeStep === 0 }">品牌基础</span>
            <span class="stage-chip" :class="{ active: activeStep === 1 }">选品目标</span>
            <span class="stage-chip" :class="{ active: activeStep === 2 }">排除边界</span>
          </div>
        </div>

        <el-form ref="formRef" class="prep-form" :model="form" :rules="rules" label-position="top">
          <section v-show="activeStep === 0" class="form-section">
            <header class="form-section-head">
              <div>
                <span class="section-kicker">品牌基础</span>
                <h3>先建立品牌语境</h3>
              </div>
              <p>行业和品牌名称会影响后续推荐文案、相似赛道比对和筛选解释。</p>
            </header>
            <div class="form-grid">
              <el-form-item label="品牌名称" prop="brandName">
                <el-input v-model="form.brandName" placeholder="如：壹沓生活" />
              </el-form-item>
              <el-form-item label="所属行业" prop="industry">
                <el-select v-model="form.industry" placeholder="选择行业">
                  <el-option label="生活消费品" value="生活消费品" />
                  <el-option label="宠物用品" value="宠物用品" />
                  <el-option label="小家电" value="小家电" />
                  <el-option label="户外运动" value="户外运动" />
                </el-select>
              </el-form-item>
            </div>
          </section>

          <section v-show="activeStep === 1" class="form-section">
            <header class="form-section-head">
              <div>
                <span class="section-kicker">选品边界</span>
                <h3>把投入和目标先说清楚</h3>
              </div>
              <p>平台、预算、利润和供应链决定系统是优先给你高增长机会，还是更稳妥的验证路径。</p>
            </header>

            <div class="form-grid">
              <el-form-item label="是否已有目标品类" prop="hasCategory">
                <el-segmented v-model="form.hasCategory" :options="categoryOptions" />
              </el-form-item>
              <el-form-item v-if="form.hasCategory" label="目标品类" prop="targetCategory">
                <el-input v-model="form.targetCategory" placeholder="如：宠物智能用品" />
              </el-form-item>
              <el-form-item v-else label="感兴趣方向" prop="interestDirection">
                <el-input v-model="form.interestDirection" placeholder="如：智能硬件、高复购耗材" />
              </el-form-item>
              <el-form-item label="已有产品（选填）" class="form-grid-full">
                <el-input
                  v-model="form.existingProducts"
                  type="textarea"
                  :rows="2"
                  placeholder="如：智能喂食器,宠物摄像头（逗号分隔，相关赛道将优先展示）"
                />
              </el-form-item>
            </div>

            <div class="choice-surface">
              <div class="choice-surface-head">
                <span class="section-kicker">平台选择</span>
                <b>先确定本轮主要验证阵地</b>
              </div>
              <el-form-item label="目标销售平台" prop="targetPlatforms">
                <el-checkbox-group v-model="form.targetPlatforms" class="platform-grid">
                  <el-checkbox label="天猫" value="天猫" />
                  <el-checkbox label="淘宝" value="淘宝" />
                  <el-checkbox label="抖音" value="抖音" />
                  <el-checkbox label="小红书" value="小红书" />
                  <el-checkbox label="亚马逊" value="亚马逊" />
                  <el-checkbox label="Shopee" value="Shopee" />
                  <el-checkbox label="TikTok Shop" value="TikTok Shop" />
                </el-checkbox-group>
              </el-form-item>
            </div>

            <div class="form-grid form-grid-wide">
              <div class="choice-surface">
                <div class="choice-surface-head">
                  <span class="section-kicker">投入带</span>
                  <b>预算和利润线</b>
                </div>
                <el-form-item label="资金预算">
                  <el-radio-group v-model="form.budgetRange">
                    <el-radio-button label="＜5万" value="＜5万" />
                    <el-radio-button label="5-20万" value="5-20万" />
                    <el-radio-button label="20-50万" value="20-50万" />
                    <el-radio-button label="50万以上" value="50万以上" />
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="期望利润下限">
                  <el-radio-group v-model="form.profitMin">
                    <el-radio-button label="＜15%" value="＜15%" />
                    <el-radio-button label="15-25%" value="15-25%" />
                    <el-radio-button label="＞25%" value="＞25%" />
                  </el-radio-group>
                </el-form-item>
              </div>

              <div class="choice-surface">
                <div class="choice-surface-head">
                  <span class="section-kicker">履约带</span>
                  <b>供应链和备货节奏</b>
                </div>
                <el-form-item label="供应链资源">
                  <el-input v-model="form.supplyChain" type="textarea" :rows="3" placeholder="如：华东小家电供应链，可接受小批量试产" />
                </el-form-item>
                <el-form-item label="备货周期">
                  <el-radio-group v-model="form.stockCycle">
                    <el-radio-button label="＜30天" value="＜30天" />
                    <el-radio-button label="30-60天" value="30-60天" />
                    <el-radio-button label="60天以上" value="60天以上" />
                  </el-radio-group>
                </el-form-item>
              </div>
            </div>
          </section>

          <section v-show="activeStep === 2" class="form-section">
            <header class="form-section-head">
              <div>
                <span class="section-kicker">排除边界</span>
                <h3>提前说明哪些不做</h3>
              </div>
              <p>把明确不碰的类目先排掉，后面看信号和机会时会少很多干扰判断。</p>
            </header>
            <div class="form-grid">
              <el-form-item label="排除品类">
                <el-select v-model="form.excludeCategories" multiple allow-create filterable placeholder="输入后回车添加">
                  <el-option label="食品" value="食品" />
                  <el-option label="医疗器械" value="医疗器械" />
                  <el-option label="高危化学品" value="高危化学品" />
                </el-select>
              </el-form-item>
            </div>
            <div class="exclusion-tip">
              <span>建议写明确禁止、强监管或明显不符合供应链条件的品类，避免把“暂时不优先”的对象也误删掉。</span>
            </div>
          </section>
        </el-form>

        <div class="form-actions">
          <el-button :disabled="activeStep === 0" @click="activeStep -= 1">上一步</el-button>
          <el-button v-if="activeStep < 2" type="primary" @click="activeStep += 1">下一步</el-button>
          <el-button v-else type="primary" :loading="submitting" @click="submit">生成洞察</el-button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { TrendCharts } from '@element-plus/icons-vue'
import { api } from '@/api'
import { getBrandId, setBrandId } from '@/composables/useBrandContext'
import { getApiErrorMessage } from '@/lib/apiError'
import type { BrandRequest, WorkflowProgress } from '@/types'
import PageHero from '@/components/common/PageHero.vue'
import WorkflowSummary from '@/components/common/WorkflowSummary.vue'
import DataConnectorsPanel from '@/components/common/DataConnectorsPanel.vue'

const router = useRouter()
const formRef = ref<FormInstance>()
const activeStep = ref(0)
const submitting = ref(false)
const hasSavedBrand = computed(() => sessionStorage.getItem('selection-brand-saved') === '1')
const workflow = ref<WorkflowProgress>()
const sampleIndustryReports = [
  {
    title: '2025 家居收纳消费趋势白皮书',
    source: '样例·行业报告',
    period: '2025 Q1',
    summary: '跨境与直播渠道增速高于大盘，轻小件收纳盒搜索同比 +34%。'
  },
  {
    title: '抖音个护美妆社媒舆情月报',
    source: '样例·社媒舆情',
    period: '2025-05',
    summary: '「成分透明」「敏感肌友好」讨论热度上升，差评集中在发货时效。'
  },
  {
    title: '天猫/淘宝类目搜索指数摘要',
    source: '样例·电商+搜索',
    period: '近 12 月',
    summary: '中高客单价格带供给偏紧，200-400 元带缺口比高于类目均值。'
  }
]
const stepLabel = computed(() => {
  if (activeStep.value === 0) return '基础信息录入'
  if (activeStep.value === 1) return '选品目标设定'
  return '排除边界确认'
})
const stepHint = computed(() => {
  if (activeStep.value === 0) return '先确认品牌名称和行业，让后续洞察有明确业务语境。'
  if (activeStep.value === 1) return '平台、预算、利润和供应链会直接决定推荐顺序。'
  return '把明确不做的品类提前排除，减少后续误判和干扰。'
})
const completionScore = computed(() => {
  let score = 20
  if (form.brandName.trim()) score += 15
  if (form.industry.trim()) score += 10
  if (form.targetPlatforms.length) score += 20
  if (form.budgetRange) score += 10
  if (form.profitMin) score += 10
  if (form.supplyChain.trim()) score += 10
  if (form.stockCycle) score += 10
  if (form.excludeCategories.length) score += 5
  return Math.min(score, 100)
})
const readinessText = computed(() => {
  if (completionScore.value >= 90) return '关键信息已基本齐全，可以直接进入洞察与机会筛选。'
  if (completionScore.value >= 70) return '主要边界已经成型，建议补齐供应链或排除项后再进入下一步。'
  return '品牌约束尚未完整，建议至少补齐平台、预算和利润边界。'
})
const missingHighlights = computed(() => {
  const items: string[] = []
  if (!form.targetPlatforms.length) items.push('还未选择目标平台，后续推荐会缺少验证语境。')
  if (!form.budgetRange) items.push('还未确定预算带，系统无法稳定判断试错空间。')
  if (!form.profitMin) items.push('还未设置利润下限，候选赛道排序会偏宽松。')
  if (!form.supplyChain.trim()) items.push('建议补充供应链资源，避免高增长但难履约的机会被误推到前面。')
  if (!form.stockCycle) items.push('建议明确备货周期，系统才能更准确判断入场窗口。')
  if (!form.excludeCategories.length) items.push('可以补几个明确不做的类目，减少后续噪音。')
  return items.length ? items : ['当前关键判断条件已齐，可进入下一步看洞察与候选赛道。']
})

const categoryOptions = [
  { label: '已有品类', value: true },
  { label: '暂无品类', value: false }
]

const form = reactive<BrandRequest>({
  brandName: '壹沓生活',
  industry: '生活消费品',
  targetCategory: '宠物智能用品',
  hasCategory: true,
  interestDirection: '',
  targetPlatforms: ['天猫', '抖音', '小红书'],
  budgetRange: '20-50万',
  profitMin: '15-25%',
  supplyChain: '华东小家电供应链，可接受小批量试产',
  stockCycle: '30-60天',
  excludeCategories: ['食品'],
  existingProducts: '智能喂食器,宠物摄像头'
})

const rules: FormRules = {
  brandName: [{ required: true, message: '请输入品牌名称', trigger: 'blur' }],
  industry: [{ required: true, message: '请选择行业', trigger: 'change' }],
  targetPlatforms: [{ required: true, message: '请选择目标平台', trigger: 'change' }]
}

function goInsight() {
  router.push({ path: '/insight', query: { brandId: getBrandId() } })
}

async function loadWorkflow() {
  workflow.value = await api.getWorkflow(getBrandId())
}

async function submit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    const brand = await api.createBrand(form)
    setBrandId(brand.id)
    sessionStorage.setItem('selection-brand-saved', '1')
    workflow.value = await api.getWorkflow(brand.id)
    ElMessage.success(`已生成品牌上下文：${brand.brandName}`)
    router.push({ path: '/insight', query: { brandId: brand.id } })
  } catch (error) {
    ElMessage.error(getApiErrorMessage(error, '提交失败，请检查网络后重试'))
  } finally {
    submitting.value = false
  }
}

onMounted(loadWorkflow)
watch(hasSavedBrand, loadWorkflow)
</script>

<style scoped>
.industry-report-panel {
  margin-bottom: 16px;
}

.industry-report-list {
  list-style: none;
  padding: 0;
  margin: 12px 0 0;
  display: grid;
  gap: 10px;
}

.industry-report-list li {
  padding: 12px;
  border-radius: 12px;
  background: rgba(248, 251, 255, 0.92);
}

.industry-report-list b,
.industry-report-list small,
.industry-report-list span {
  display: block;
}

.industry-report-list small {
  color: var(--muted);
  margin: 4px 0;
}

.industry-report-list span {
  color: var(--ink);
  line-height: 1.6;
}

.prep-alert {
  margin-bottom: 16px;
}

.prep-alert :deep(.el-alert) {
  border-radius: 14px;
}

.prep-shell {
  display: grid;
  grid-template-columns: 320px minmax(0, 1fr);
  gap: 16px;
}

.prep-intake {
  margin-bottom: 16px;
}

.intake-head {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(320px, 0.9fr);
  gap: 18px;
  align-items: start;
}

.intake-head h2 {
  margin: 10px 0 0;
  color: var(--ink-strong);
  font-size: 26px;
}

.intake-head p {
  margin: 10px 0 0;
  max-width: 760px;
  color: var(--muted);
  line-height: 1.7;
}

.intake-summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.intake-summary article {
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.intake-summary span,
.intake-summary small {
  display: block;
}

.intake-summary span {
  color: var(--muted);
  font-size: 12px;
}

.intake-summary b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 18px;
  line-height: 1.5;
}

.intake-summary small {
  margin-top: 10px;
  color: var(--muted);
  line-height: 1.6;
}

.prep-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(320px, 0.95fr);
  gap: 18px;
  margin-bottom: 16px;
  background:
    linear-gradient(135deg, rgba(239, 246, 255, 0.92), rgba(240, 253, 250, 0.8)),
    #ffffff;
}

.prep-hero-copy h2 {
  margin: 10px 0 0;
  font-size: 32px;
  color: var(--ink-strong);
}

.prep-hero-copy p {
  margin: 12px 0 0;
  max-width: 760px;
  color: var(--muted);
  line-height: 1.8;
}

.prep-hero-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.prep-hero-metrics article {
  padding: 16px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.76);
  box-shadow: var(--shadow-sm);
}

.prep-hero-metrics span,
.prep-hero-metrics small {
  display: block;
}

.prep-hero-metrics span {
  color: var(--muted);
  font-size: 12px;
}

.prep-hero-metrics b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 22px;
}

.prep-hero-metrics small {
  margin-top: 10px;
  color: var(--muted);
  line-height: 1.6;
}

.prep-aside {
  display: grid;
  gap: 16px;
  align-self: start;
}

.prep-summary-card {
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 250, 255, 0.98)),
    #ffffff;
}

.summary-progress {
  margin-top: 18px;
  padding: 14px 14px 16px;
  border: 1px solid rgba(148, 163, 184, 0.14);
  border-radius: 14px;
  background: rgba(248, 251, 255, 0.86);
}

.summary-progress-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.summary-progress-head span,
.summary-progress small {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.summary-progress-head b {
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 18px;
}

.summary-progress-track {
  height: 8px;
  margin-top: 12px;
  overflow: hidden;
  border-radius: 999px;
  background: rgba(226, 232, 240, 0.9);
}

.summary-progress-fill {
  display: block;
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, rgba(37, 99, 235, 0.92), rgba(15, 118, 110, 0.82));
}

.summary-progress small {
  margin-top: 10px;
  line-height: 1.6;
}

.prep-note-card {
  background:
    linear-gradient(180deg, rgba(239, 246, 255, 0.92), rgba(240, 253, 250, 0.82)),
    #ffffff;
}

.prep-note-card h2 {
  margin: 10px 0 0;
}

.prep-note-card ul {
  margin: 18px 0 0;
  padding-left: 18px;
  color: var(--ink);
  line-height: 1.8;
}

.prep-checklist-card {
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(250, 252, 255, 0.98)),
    #ffffff;
}

.prep-form-card {
  background: var(--panel-strong);
}

.form-section {
  padding: 18px;
  border: 1px solid rgba(148, 163, 184, 0.14);
  border-radius: 16px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(249, 251, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.form-section + .form-section {
  margin-top: 16px;
}

.form-section-head {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  align-items: flex-start;
  margin-bottom: 18px;
}

.form-section-head h3 {
  margin: 8px 0 0;
  color: var(--ink-strong);
  font-size: 20px;
}

.form-section-head p {
  max-width: 420px;
  margin: 0;
  color: var(--muted);
  line-height: 1.7;
  text-align: right;
}

.section-kicker {
  display: block;
  color: var(--muted-soft);
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.choice-surface {
  padding: 16px;
  border: 1px solid rgba(148, 163, 184, 0.14);
  border-radius: 14px;
  background: rgba(248, 251, 255, 0.84);
}

.choice-surface-head {
  display: grid;
  gap: 6px;
  margin-bottom: 14px;
}

.choice-surface-head b {
  color: var(--ink-strong);
  font-size: 16px;
}

.form-stage-banner {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background: rgba(252, 253, 255, 0.96);
}

.form-stage-banner h2 {
  margin: 8px 0 0;
  color: var(--ink-strong);
  font-size: 22px;
}

.form-stage-banner p {
  margin: 10px 0 0;
  color: var(--muted);
  line-height: 1.7;
}

.stage-chip-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: flex-end;
}

.stage-chip {
  display: inline-flex;
  align-items: center;
  min-height: 34px;
  padding: 0 12px;
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  background: rgba(255, 255, 255, 0.82);
  color: var(--muted);
  font-size: 12px;
  font-weight: 600;
}

.stage-chip.active {
  border-color: rgba(37, 99, 235, 0.18);
  background: rgba(37, 99, 235, 0.08);
  color: var(--accent);
}

.eyebrow {
  display: inline-block;
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
}

.prep-aside h2 {
  margin: 10px 0 0;
  font-size: 28px;
}

.prep-aside p {
  line-height: 1.7;
  color: var(--muted);
}

.prep-aside dl {
  display: grid;
  gap: 12px;
  margin: 22px 0 0;
}

.prep-aside dl div {
  padding: 14px 14px 16px;
  border: 1px solid rgba(148, 163, 184, 0.14);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.74);
}

.prep-aside dt {
  color: var(--muted);
  font-size: 12px;
}

.prep-aside dd {
  margin: 6px 0 0;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 15px;
}

.prep-steps {
  margin-bottom: 18px;
}

.prep-form {
  margin-top: 22px;
}

.prep-form-card :deep(.el-input__wrapper),
.prep-form-card :deep(.el-select__wrapper),
.prep-form-card :deep(.el-textarea__inner) {
  border-radius: 12px;
}

.prep-form-card :deep(.el-segmented),
.prep-form-card :deep(.el-radio-group) {
  gap: 8px;
}

.prep-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.prep-form :deep(.el-radio-button__inner) {
  min-width: 92px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px 18px;
}

.form-grid-wide {
  gap: 16px;
}

.platform-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px 14px;
}

.exclusion-tip {
  margin-top: 16px;
  padding: 12px 14px;
  border-radius: 12px;
  background: rgba(252, 253, 255, 0.96);
  border: 1px solid rgba(148, 163, 184, 0.14);
}

.exclusion-tip span {
  display: block;
  color: var(--muted);
  line-height: 1.7;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  border-top: 1px solid var(--line);
  padding-top: 18px;
  margin-top: 8px;
}

@media (max-width: 1024px) {
  .prep-hero,
  .prep-intake .intake-head,
  .prep-shell {
    grid-template-columns: 1fr;
  }

  .prep-hero-metrics {
    grid-template-columns: 1fr;
  }

  .intake-summary {
    grid-template-columns: 1fr;
  }

  .form-stage-banner,
  .form-section-head {
    display: grid;
  }

  .form-section-head p {
    max-width: none;
    text-align: left;
  }

  .stage-chip-row {
    justify-content: flex-start;
  }
}

@media (max-width: 760px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .platform-grid {
    grid-template-columns: 1fr;
  }

  .form-stage-banner {
    display: grid;
  }

  .form-section-head {
    display: grid;
  }

  .form-section-head p {
    text-align: left;
  }
}
</style>
