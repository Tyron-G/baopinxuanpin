<template>
  <section>
    <PageHero eyebrow="机会榜单" title="TOP50 潜力品机会榜" description="按机会分数排序，每个品标注推荐原因与卖点建议（MVP P0）。" />
    <WorkflowSummary current-stage="ranking" :workflow="workflow" />
    <section v-if="loading" class="panel pad"><el-skeleton :rows="8" animated /></section>
    <section v-else class="panel pad">
      <div class="rank-toolbar">
        <div>
          <span class="eyebrow">平台视角</span>
          <p class="toolbar-hint">榜单机会分与 12 月同比按所选平台口径计算，与洞察页一致。</p>
        </div>
        <el-segmented v-model="activePlatform" :options="platformOptions" />
      </div>
      <div class="rank-head">
        <span>共 {{ page?.total ?? 0 }} 项 · 当前展示 {{ items.length }} 项 · {{ activePlatformLabel }}</span>
        <el-button type="primary" @click="load">刷新榜单</el-button>
      </div>
      <el-table :data="items" stripe>
        <el-table-column prop="rank" label="#" width="60" />
        <el-table-column prop="productTitle" label="潜力品" min-width="160" />
        <el-table-column prop="categoryName" label="类目" width="140" />
        <el-table-column prop="opportunityScore" label="机会分" width="90" />
        <el-table-column prop="decision" label="决策" width="110" />
        <el-table-column prop="recommendationReason" label="为什么推荐" min-width="200" show-overflow-tooltip />
        <el-table-column label="卖点建议" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.sellingPoint?.sellingPoint ?? '—' }}
          </template>
        </el-table-column>
        <el-table-column label="价格带" width="110" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.sellingPoint?.suggestedPriceBand ?? '—' }}
          </template>
        </el-table-column>
        <el-table-column label="差异化方向" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.sellingPoint?.differentiationDirection ?? '—' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button v-if="row.cardId" link type="primary" @click="openCard(row.cardId)">进入机会</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '@/api'
import { getBrandId } from '@/composables/useBrandContext'
import { DEFAULT_PLATFORM_VIEW } from '@/constants/brand'
import type { CategoryTrend, OpportunityRankItem, OpportunityRankingPage, WorkflowProgress } from '@/types'
import PageHero from '@/components/common/PageHero.vue'
import WorkflowSummary from '@/components/common/WorkflowSummary.vue'
import { getApiErrorMessage } from '@/lib/apiError'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const workflow = ref<WorkflowProgress>()
const page = ref<OpportunityRankingPage>()
const items = ref<OpportunityRankItem[]>([])
const trends = ref<CategoryTrend[]>([])
const activePlatform = ref(DEFAULT_PLATFORM_VIEW)
const platformReady = ref(false)

const platformOptions = computed(() => [...new Set(trends.value.map((item) => item.platform))])
const activePlatformLabel = computed(() =>
  activePlatform.value === DEFAULT_PLATFORM_VIEW ? '全平台聚合视角' : `${activePlatform.value} 平台视角`
)

async function loadTrends() {
  trends.value = await api.getTrends(getBrandId())
}

async function load() {
  loading.value = true
  try {
    const brandId = getBrandId()
    const [rankingPage, workflowData] = await Promise.all([
      api.getTop50Ranking(brandId, 1, 50, activePlatform.value),
      api.getWorkflow(brandId, activePlatform.value)
    ])
    page.value = rankingPage
    items.value = rankingPage.items
    workflow.value = workflowData
  } catch (error) {
    ElMessage.error(getApiErrorMessage(error))
  } finally {
    loading.value = false
  }
}

function openCard(cardId: number) {
  router.push({
    path: `/opportunity/${cardId}`,
    query: { brandId: getBrandId(), platform: activePlatform.value }
  })
}

watch(platformOptions, (options) => {
  if (options.length && !options.includes(activePlatform.value)) {
    activePlatform.value = DEFAULT_PLATFORM_VIEW
  }
})

watch(
  () => route.query.platform,
  (value) => {
    if (typeof value === 'string' && value.trim()) {
      activePlatform.value = value
      return
    }
    activePlatform.value = DEFAULT_PLATFORM_VIEW
  },
  { immediate: true }
)

watch(activePlatform, async (platform) => {
  if (!platformReady.value) return
  router.replace({ query: { ...route.query, brandId: getBrandId(), platform } })
  await load()
})

onMounted(async () => {
  await loadTrends()
  platformReady.value = true
  await load()
})
</script>

<style scoped>
.rank-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.toolbar-hint {
  margin: 4px 0 0;
  color: var(--text-muted, #64748b);
  font-size: 13px;
}

.rank-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}
</style>
