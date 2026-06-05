<template>
  <section class="panel pad prd-analysis-panel">
    <span class="eyebrow">深度分析（PRD）</span>
    <h2>供需缺口模型 · 价格带分布 · 生命周期二阶导</h2>

    <div v-if="gapModel" class="analysis-block">
      <h3>供需缺口模型</h3>
      <p class="analysis-summary">{{ gapModel.summary }}</p>
      <div class="formula-grid">
        <article>
          <span>需求热度</span>
          <b>{{ gapModel.demandHeat }}</b>
        </article>
        <article>
          <span>供给充分度</span>
          <b>{{ gapModel.supplyAdequacy }}</b>
        </article>
        <article>
          <span>满意度缺口</span>
          <b>{{ gapModel.satisfactionGap }}</b>
        </article>
        <article>
          <span>缺口指数</span>
          <b>{{ gapModel.gapIndex }}</b>
          <small>需求热度 ÷ 供给充分度 × 满意度缺口</small>
        </article>
        <article>
          <span>价格真空带</span>
          <b>{{ gapModel.priceVacuumBand }}</b>
        </article>
      </div>
    </div>

    <div v-if="priceBands" class="analysis-block">
      <h3>价格带分布（{{ priceBands.platform }}）</h3>
      <p class="analysis-summary">{{ priceBands.summary }}</p>
      <el-table :data="priceBands.bands" size="small" stripe>
        <el-table-column prop="priceRange" label="价格带" width="120" />
        <el-table-column prop="skuCount" label="SKU 数" width="100" />
        <el-table-column prop="salesSharePercent" label="销量占比 %" width="110" />
        <el-table-column prop="gapHint" label="供需提示" min-width="120" />
      </el-table>
    </div>

    <div v-if="lifecycle" class="analysis-block">
      <h3>生命周期判断</h3>
      <p class="analysis-summary">{{ lifecycle.summary }}</p>
      <div class="lifecycle-tags">
        <el-tag type="success" effect="plain">{{ lifecycle.lifecycleStage }}</el-tag>
        <el-tag :type="lifecycle.growthAccelerating ? 'warning' : 'info'" effect="plain">
          {{ lifecycle.secondDerivativeLabel }}
        </el-tag>
        <el-tag effect="plain">12月同比 {{ lifecycle.latestGrowthRate }}%</el-tag>
        <el-tag effect="plain">加速度 {{ lifecycle.growthAcceleration }}</el-tag>
        <el-tag type="danger" effect="plain">{{ lifecycle.firstHitTimeline }}</el-tag>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import type { LifecycleInsight, PriceBandDistribution, SupplyDemandGapModel } from '@/types'

defineProps<{
  gapModel?: SupplyDemandGapModel
  priceBands?: PriceBandDistribution
  lifecycle?: LifecycleInsight
}>()
</script>

<style scoped>
.prd-analysis-panel h2 {
  margin: 6px 0 16px;
}

.analysis-block + .analysis-block {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid rgba(148, 163, 184, 0.2);
}

.analysis-block h3 {
  margin: 0 0 8px;
  font-size: 16px;
}

.analysis-summary {
  margin: 0 0 12px;
  color: var(--muted);
  line-height: 1.55;
}

.formula-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.formula-grid article {
  padding: 10px 12px;
  border-radius: 10px;
  background: rgba(248, 251, 255, 0.95);
}

.formula-grid span,
.formula-grid small {
  display: block;
  color: var(--muted);
}

.formula-grid b {
  display: block;
  margin-top: 4px;
}

.lifecycle-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
</style>
