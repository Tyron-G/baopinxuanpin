<template>
  <section class="panel pad watchlist-panel">
    <div class="watchlist-head">
      <div>
        <span class="eyebrow">关注列表</span>
        <h3>基础看板 · 重点跟踪类目</h3>
        <p>将当前判断中的类目加入关注，便于日常监控与复盘。</p>
      </div>
      <el-button type="primary" size="small" :disabled="!canAdd" @click="addCurrent">加入关注</el-button>
    </div>
    <el-empty v-if="!items.length" description="暂无关注项，可从洞察卡片或此处添加" />
    <ul v-else class="watchlist-list">
      <li v-for="item in items" :key="item.id">
        <div>
          <b>{{ item.categoryName }}</b>
          <small>{{ item.note || '已加入关注' }} · {{ item.createdAt }}</small>
        </div>
        <el-button link type="danger" @click="remove(item.id)">移除</el-button>
      </li>
    </ul>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { api } from '@/api'
import { getBrandId } from '@/composables/useBrandContext'
import { getApiErrorMessage } from '@/lib/apiError'
import type { WatchlistItem } from '@/types'
import { ElMessage } from 'element-plus'

const props = defineProps<{
  categoryName?: string
  cardId?: number | null
}>()

const items = ref<WatchlistItem[]>([])
const brandId = computed(() => getBrandId())
const canAdd = computed(() => Boolean(props.categoryName?.trim()))

async function load() {
  items.value = await api.getWatchlist(brandId.value)
}

async function addCurrent() {
  if (!props.categoryName) return
  try {
    await api.addWatchlist(brandId.value, props.categoryName, props.cardId ?? undefined, '洞察页加入')
    ElMessage.success('已加入关注列表')
    await load()
  } catch (error) {
    ElMessage.error(getApiErrorMessage(error))
  }
}

async function remove(id: number) {
  try {
    await api.removeWatchlist(brandId.value, id)
    await load()
  } catch (error) {
    ElMessage.error(getApiErrorMessage(error))
  }
}

onMounted(load)
watch(() => brandId.value, load)
</script>

<style scoped>
.watchlist-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.watchlist-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  gap: 10px;
}

.watchlist-list li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  border-radius: 10px;
  background: rgba(248, 251, 255, 0.9);
}

.watchlist-list small {
  display: block;
  color: var(--muted);
  margin-top: 4px;
}
</style>
