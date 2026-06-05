<template>
  <section>
    <PageHero
      eyebrow="测款优化"
      title="上架一周，该不该加投"
      description="背景场景：新品测款第 7 天数据复盘，输出加投 / 观望 / 停投建议与优化动作（内置样例）。"
    >
      <template #actions>
        <el-button type="primary" @click="reload">刷新诊断</el-button>
        <el-button @click="openOpportunity">回到机会页</el-button>
      </template>
    </PageHero>

    <section class="panel pad filter-bar">
      <el-select v-model="cardId" style="width: 120px" @change="reload">
        <el-option label="卡片 #1" :value="1" />
        <el-option label="卡片 #2" :value="2" />
        <el-option label="卡片 #3" :value="3" />
      </el-select>
      <el-select v-model="platform" style="width: 140px" @change="reload">
        <el-option label="全平台" value="全平台" />
        <el-option label="天猫" value="天猫" />
        <el-option label="抖音" value="抖音" />
        <el-option label="小红书" value="小红书" />
      </el-select>
      <el-tag v-if="diagnosis?.demoData" type="info" effect="plain">演示数据</el-tag>
    </section>

    <section v-if="loading" class="panel pad"><el-skeleton :rows="8" animated /></section>

    <template v-else-if="diagnosis">
      <section class="panel pad hero">
        <div>
          <span class="eyebrow">{{ diagnosis.weekLabel }} · {{ diagnosis.platform }}</span>
          <h2>{{ diagnosis.productTitle }}</h2>
          <p>{{ diagnosis.summary }}</p>
        </div>
        <div class="verdict-card" :class="verdictClass">
          <span>测款结论</span>
          <b>{{ diagnosis.verdict }}</b>
          <small>置信度 {{ diagnosis.confidence }}%</small>
        </div>
      </section>

      <div class="metric-grid">
        <article v-for="item in diagnosis.metrics" :key="item.key" class="panel pad metric-card" :class="`metric-card--${item.status}`">
          <span>{{ item.label }}</span>
          <b>{{ item.actualValue }}</b>
          <small>目标 {{ item.benchmarkValue }}</small>
          <p>{{ item.hint }}</p>
        </article>
      </div>

      <div class="action-grid">
        <section class="panel pad">
          <span class="eyebrow">优化建议</span>
          <h3>改什么 / 加什么</h3>
          <ul>
            <li v-for="item in diagnosis.scaleUpActions" :key="item">{{ item }}</li>
          </ul>
        </section>
        <section class="panel pad">
          <span class="eyebrow">停投信号</span>
          <h3>出现以下情况应停</h3>
          <ul>
            <li v-for="item in diagnosis.stopSignals" :key="item">{{ item }}</li>
          </ul>
        </section>
      </div>
    </template>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '@/api'
import { getBrandId } from '@/composables/useBrandContext'
import { getApiErrorMessage } from '@/lib/apiError'
import PageHero from '@/components/common/PageHero.vue'
import type { TestRunDiagnosis } from '@/types'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(true)
const diagnosis = ref<TestRunDiagnosis>()
const cardId = ref(1)
const platform = ref('全平台')

const verdictClass = computed(() => {
  const verdict = diagnosis.value?.verdict ?? ''
  if (verdict.includes('加投')) return 'verdict-scale'
  if (verdict.includes('停投')) return 'verdict-stop'
  return 'verdict-watch'
})

async function reload() {
  loading.value = true
  try {
    diagnosis.value = await api.getTestRunDiagnosis(cardId.value, getBrandId(), platform.value)
  } catch (error) {
    ElMessage.error(getApiErrorMessage(error))
  } finally {
    loading.value = false
  }
}

function openOpportunity() {
  router.push({ path: `/opportunity/${cardId.value}`, query: { brandId: getBrandId(), platform: platform.value } })
}

onMounted(reload)
</script>

<style scoped>
.filter-bar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
}

.hero {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 16px;
}

.hero h2 {
  margin: 8px 0 0;
  color: var(--ink-strong);
}

.hero p {
  margin: 10px 0 0;
  color: var(--muted);
  line-height: 1.7;
  max-width: 720px;
}

.verdict-card {
  min-width: 200px;
  padding: 16px 18px;
  border-radius: 14px;
  border: 1px solid var(--line);
  box-shadow: var(--shadow-sm);
}

.verdict-card span,
.verdict-card small {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.verdict-card b {
  display: block;
  margin-top: 8px;
  font-family: 'IBM Plex Mono', monospace;
  font-size: 22px;
}

.verdict-scale {
  background: linear-gradient(180deg, rgba(240, 253, 244, 0.96), rgba(255, 255, 255, 0.98));
  border-color: rgba(22, 163, 74, 0.22);
}

.verdict-watch {
  background: linear-gradient(180deg, rgba(255, 251, 235, 0.96), rgba(255, 255, 255, 0.98));
  border-color: rgba(217, 119, 6, 0.22);
}

.verdict-stop {
  background: linear-gradient(180deg, rgba(254, 242, 242, 0.96), rgba(255, 255, 255, 0.98));
  border-color: rgba(220, 38, 38, 0.22);
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.metric-card span,
.metric-card small,
.metric-card p {
  display: block;
}

.metric-card span {
  color: var(--muted);
  font-size: 12px;
}

.metric-card b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: 'IBM Plex Mono', monospace;
  font-size: 20px;
}

.metric-card small {
  margin-top: 6px;
  color: var(--muted);
}

.metric-card p {
  margin: 10px 0 0;
  color: var(--muted);
  line-height: 1.6;
  font-size: 13px;
}

.metric-card--good {
  border-color: rgba(22, 163, 74, 0.18);
}

.metric-card--warn {
  border-color: rgba(217, 119, 6, 0.18);
}

.metric-card--bad {
  border-color: rgba(220, 38, 38, 0.18);
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.action-grid h3 {
  margin: 8px 0 12px;
  color: var(--ink-strong);
  font-size: 16px;
}

.action-grid ul {
  margin: 0;
  padding-left: 18px;
  line-height: 1.8;
  color: var(--muted);
}

@media (max-width: 900px) {
  .hero,
  .metric-grid,
  .action-grid {
    grid-template-columns: 1fr;
  }

  .hero {
    display: grid;
  }
}
</style>
