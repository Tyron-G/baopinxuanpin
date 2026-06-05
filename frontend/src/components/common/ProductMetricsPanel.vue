<template>
  <section class="panel pad metrics-panel">
    <div class="metrics-head">
      <div>
        <span class="eyebrow">{{ metrics?.demoData ? '运营 KPI（样例）' : '运营 KPI（应用内推算）' }}</span>
        <h3>{{ metrics?.phaseLabel ?? '产品验证指标' }}</h3>
        <p>{{ metrics?.summary ?? '加载中…' }}</p>
      </div>
      <el-tag v-if="metrics?.demoData" size="small" type="info" effect="plain">演示数据</el-tag>
    </div>
    <small v-if="metrics" class="metrics-date">截至 {{ metrics.asOfDate }}</small>
    <div v-if="metrics" class="metrics-grid">
      <article v-for="item in metrics.metrics" :key="item.key" :class="`metric-card metric-card--${item.status}`">
        <span>{{ item.label }}</span>
        <b>{{ item.actualValue }}</b>
        <small>目标 {{ item.targetValue }}</small>
      </article>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { api } from '@/api'
import { getBrandId } from '@/composables/useBrandContext'
import type { ProductMetricsKpi } from '@/types'

const props = defineProps<{ brandId?: number }>()
const metrics = ref<ProductMetricsKpi>()

async function load() {
  metrics.value = await api.getProductMetrics(props.brandId ?? getBrandId())
}

onMounted(load)
watch(() => props.brandId, load, { immediate: false })
</script>

<style scoped>
.metrics-panel {
  margin-bottom: 16px;
}

.metrics-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.metrics-head p {
  margin: 8px 0 0;
  color: var(--muted);
  line-height: 1.6;
}

.metrics-date {
  display: block;
  margin: 8px 0 12px;
  color: var(--muted);
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.metric-card {
  padding: 10px 12px;
  border-radius: 12px;
  background: rgba(248, 251, 255, 0.92);
  border: 1px solid rgba(148, 163, 184, 0.14);
}

.metric-card span,
.metric-card small {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.metric-card b {
  display: block;
  margin: 6px 0;
  font-family: "IBM Plex Mono", monospace;
}

.metric-card--met {
  border-color: rgba(34, 197, 94, 0.35);
}

@media (max-width: 1100px) {
  .metrics-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
