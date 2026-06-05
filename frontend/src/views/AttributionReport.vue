<template>
  <section>
    <PageHero eyebrow="选品归因" title="为什么爆 / 为什么死" description="迭代2：基于历史卡片决策沉淀归因与下季度机会清单。" />
    <section v-if="loading" class="panel pad"><el-skeleton :rows="6" animated /></section>
    <section v-else-if="report" class="panel pad">
      <p>{{ report.summary }}</p>
      <h3>成功因子</h3>
      <ul>
        <li v-for="item in report.successFactors" :key="item.title">
          <b>{{ item.title }}</b> — {{ item.description }}（{{ item.evidence }}）
        </li>
      </ul>
      <h3>失败因子</h3>
      <ul>
        <li v-for="item in report.failureFactors" :key="item.title">
          <b>{{ item.title }}</b> — {{ item.description }}
        </li>
      </ul>
      <h3>下季度机会清单</h3>
      <el-tag v-for="name in report.nextQuarterOpportunities" :key="name" class="tag">{{ name }}</el-tag>
    </section>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { api } from '@/api'
import { getBrandId } from '@/composables/useBrandContext'
import type { SelectionAttributionReport } from '@/types'
import PageHero from '@/components/common/PageHero.vue'

const loading = ref(true)
const report = ref<SelectionAttributionReport>()

onMounted(async () => {
  report.value = await api.getAttributionReport(getBrandId())
  loading.value = false
})
</script>

<style scoped>
.tag { margin: 4px 6px 0 0; }
ul { padding-left: 18px; line-height: 1.7; }
</style>
