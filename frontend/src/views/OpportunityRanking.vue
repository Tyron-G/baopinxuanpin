<template>
  <section>
    <PageHero eyebrow="机会榜单" title="TOP50 潜力品机会榜" description="按机会分数排序，每个品标注推荐原因与卖点建议（MVP P0）。" />
    <section v-if="loading" class="panel pad"><el-skeleton :rows="8" animated /></section>
    <section v-else class="panel pad">
      <div class="rank-head">
        <span>共 {{ page?.total ?? 0 }} 项 · 当前展示 {{ items.length }} 项</span>
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
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '@/api'
import { getBrandId } from '@/composables/useBrandContext'
import type { OpportunityRankItem, OpportunityRankingPage } from '@/types'
import PageHero from '@/components/common/PageHero.vue'
import { getApiErrorMessage } from '@/lib/apiError'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const page = ref<OpportunityRankingPage>()
const items = ref<OpportunityRankItem[]>([])

async function load() {
  loading.value = true
  try {
    page.value = await api.getTop50Ranking(getBrandId())
    items.value = page.value.items
  } catch (error) {
    ElMessage.error(getApiErrorMessage(error))
  } finally {
    loading.value = false
  }
}

function openCard(cardId: number) {
  const platform = typeof route.query.platform === 'string' ? route.query.platform : undefined
  router.push({
    path: `/opportunity/${cardId}`,
    query: { brandId: getBrandId(), ...(platform ? { platform } : {}) }
  })
}

onMounted(load)
</script>

<style scoped>
.rank-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}
</style>
