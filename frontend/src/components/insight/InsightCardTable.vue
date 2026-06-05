<template>
  <div class="insight-card-table">
    <el-table
      :data="rows"
      stripe
      size="small"
      highlight-current-row
      :row-class-name="rowClassName"
      class="insight-table"
      @row-click="onRowClick"
    >
      <el-table-column label="品类" min-width="128" fixed>
        <template #default="{ row }">
          <div class="category-cell">
            <b>{{ row.card.categoryName }}</b>
            <el-tag v-if="row.pinned" size="small" type="warning" effect="plain">优先</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="card.marketSize" label="市场规模" min-width="108" />
      <el-table-column prop="card.marketGrowth" label="市场增速" min-width="96" />
      <el-table-column prop="card.competitionPattern" label="竞争格局" min-width="108" show-overflow-tooltip />
      <el-table-column label="同质化" width="88">
        <template #default="{ row }">{{ homogeneityLabel(row.homogeneityScore) }}</template>
      </el-table-column>
      <el-table-column prop="card.competitionLevel" label="竞争难度" width="96">
        <template #default="{ row }">
          <el-tag
            size="small"
            effect="plain"
            :type="competitionTagType(row.card.competitionLevel)"
          >
            {{ row.card.competitionLevel }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="card.priceGap" label="价格空白" min-width="120" show-overflow-tooltip />
      <el-table-column prop="card.estimatedStartupCost" label="启动资金" min-width="96" />
      <el-table-column label="决策标签" min-width="108">
        <template #default="{ row }">
          <span class="decision-text">{{ row.decision }}</span>
        </template>
      </el-table-column>
      <el-table-column label="匹配标签" min-width="160">
        <template #default="{ row }">
          <div class="tag-cell">
            <el-tag
              v-for="tag in row.matchTags.slice(0, 3)"
              :key="tag"
              size="small"
              effect="plain"
              :type="matchTagType(tag)"
            >
              {{ tag }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="综合分" width="80" align="center">
        <template #default="{ row }">{{ row.scoreBreakdown.totalScore }}</template>
      </el-table-column>
      <el-table-column label="操作" width="96" fixed="right" align="center">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click.stop="emit('select', row.card.id)">
            看机会
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <p class="table-hint">点击行或「看机会」进入爆品机会页；排序与卡片视图一致（优先赛道置顶）。</p>
  </div>
</template>

<script setup lang="ts">
import type { InsightCardView } from '@/types'

const props = defineProps<{
  rows: InsightCardView[]
  focusedCardId?: number | null
}>()

const emit = defineEmits<{ select: [id: number] }>()

function homogeneityLabel(score?: number | null) {
  if (score == null) return '—'
  return `${Number(score).toFixed(1)}`
}

function competitionTagType(level: string) {
  if (level.includes('高')) return 'danger'
  if (level.includes('中')) return 'warning'
  return 'success'
}

function matchTagType(tag: string) {
  if (tag.includes('风险') || tag.includes('放弃')) return 'danger'
  if (tag.includes('预算') || tag.includes('匹配')) return 'success'
  return 'info'
}

function rowClassName({ row }: { row: InsightCardView }) {
  if (props.focusedCardId && row.card.id === props.focusedCardId) return 'row-focused'
  if (row.pinned) return 'row-pinned'
  return ''
}

function onRowClick(row: InsightCardView) {
  emit('select', row.card.id)
}
</script>

<style scoped>
.insight-card-table {
  width: 100%;
}

.insight-table {
  width: 100%;
}

.category-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.category-cell b {
  font-size: 13px;
}

.tag-cell {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.decision-text {
  font-size: 12px;
  color: var(--text, #0f172a);
}

.table-hint {
  margin: 10px 0 0;
  font-size: 12px;
  color: var(--muted, #64748b);
}

:deep(.row-focused td) {
  background: rgba(37, 99, 235, 0.08) !important;
}

:deep(.row-pinned td:first-child) {
  box-shadow: inset 3px 0 0 rgba(245, 158, 11, 0.85);
}

:deep(.el-table__row) {
  cursor: pointer;
}
</style>
