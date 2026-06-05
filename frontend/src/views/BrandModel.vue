<template>
  <section>
    <PageHero eyebrow="品牌专属模型" title="选品模型权重" description="迭代2：根据品牌约束训练专属权重，提升推荐准确率。">
      <template #actions>
        <el-button type="primary" :loading="training" @click="retrain">重新训练</el-button>
      </template>
    </PageHero>
    <section v-if="profile" class="panel pad model-grid">
      <article><span>版本</span><b>{{ profile.modelVersion }}</b></article>
      <article><span>趋势权重</span><b>{{ profile.trendWeight }}</b></article>
      <article><span>竞争权重</span><b>{{ profile.competitionWeight }}</b></article>
      <article><span>供需缺口</span><b>{{ profile.supplyGapWeight }}</b></article>
      <article><span>品牌契合</span><b>{{ profile.brandFitWeight }}</b></article>
      <article><span>风险惩罚</span><b>{{ profile.riskPenaltyWeight }}</b></article>
      <p class="full">{{ profile.trainingSummary }}</p>
      <el-alert type="success" :title="`预期提升：${profile.expectedAccuracyGain}`" show-icon :closable="false" />
    </section>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { api } from '@/api'
import { getBrandId } from '@/composables/useBrandContext'
import type { BrandSelectionModelProfile } from '@/types'
import PageHero from '@/components/common/PageHero.vue'
import { ElMessage } from 'element-plus'

const profile = ref<BrandSelectionModelProfile>()
const training = ref(false)

async function load() {
  profile.value = await api.getBrandModel(getBrandId())
}

async function retrain() {
  training.value = true
  try {
    profile.value = await api.retrainBrandModel(getBrandId())
    ElMessage.success('专属模型已更新')
  } finally {
    training.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.model-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}
.model-grid .full { grid-column: 1 / -1; line-height: 1.7; }
</style>
