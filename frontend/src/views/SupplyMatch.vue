<template>
  <section>
    <PageHero eyebrow="供应链匹配" title="为机会卡推荐 1688 工厂与 MOQ" description="基于机会页 1688 情报与类目剧本做匹配打分（P2）。" />
    <section class="panel pad">
      <div class="toolbar">
        <el-input-number v-model="cardId" :min="1" />
        <el-select v-model="platform" style="width: 140px">
          <el-option label="全平台" value="全平台" />
          <el-option label="天猫" value="天猫" />
          <el-option label="抖音" value="抖音" />
          <el-option label="小红书" value="小红书" />
        </el-select>
        <el-button type="primary" :loading="loading" @click="load">刷新匹配</el-button>
      </div>
      <el-table v-if="items.length" :data="items" stripe>
        <el-table-column prop="matchScore" label="匹配分" width="90" />
        <el-table-column prop="supplierName" label="工厂" min-width="160" />
        <el-table-column prop="productTitle" label="报价 SKU" min-width="180" />
        <el-table-column prop="unitPrice" label="单价" width="100" />
        <el-table-column prop="moq" label="MOQ" width="100" />
        <el-table-column prop="creditLevel" label="信用" width="80" />
        <el-table-column prop="matchReason" label="推荐理由" min-width="220" show-overflow-tooltip />
      </el-table>
      <el-empty v-else description="暂无匹配结果，请调整 cardId 后重试" />
    </section>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { api } from '@/api'
import { getBrandId } from '@/composables/useBrandContext'
import { DEFAULT_PLATFORM_VIEW } from '@/constants/brand'
import type { SupplyMatchItem } from '@/types'
import PageHero from '@/components/common/PageHero.vue'

const route = useRoute()
const loading = ref(false)
const cardId = ref(Number(route.query.cardId) || 1)
const platform = ref(typeof route.query.platform === 'string' ? route.query.platform : DEFAULT_PLATFORM_VIEW)
const items = ref<SupplyMatchItem[]>([])

async function load() {
  loading.value = true
  try {
    items.value = await api.getSupplyMatches(getBrandId(), cardId.value, platform.value)
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
</style>
