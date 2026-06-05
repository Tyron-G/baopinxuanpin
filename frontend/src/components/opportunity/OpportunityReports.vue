<template>
  <div class="panel pad reports">
    <div class="reports-head">
      <div>
        <h2>深度决策报告</h2>
        <p>竞争格局、利润测算与供应链可行性，支撑「值不值得押注」判断。</p>
      </div>
    </div>

    <div class="reports-ribbon">
      <article>
        <span>当前市场型</span>
        <b>{{ competitionReport.marketType }}</b>
        <small>{{ competitionReport.entryWindow }}</small>
      </article>
      <article>
        <span>预估净利</span>
        <b>{{ profitAnalysis.netMargin }}</b>
        <small>目标售价 {{ profitAnalysis.targetPrice }}</small>
      </article>
      <article>
        <span>供应链门槛</span>
        <b>{{ supplyChainFeasibility.moq }}</b>
        <small>{{ supplyChainFeasibility.riskHint }}</small>
      </article>
      <article>
        <span>首发平台</span>
        <b>{{ platformPlaybook.firstLaunchPlatform }}</b>
        <small>{{ platformPlaybook.validationPlatform }} 用于验证</small>
      </article>
    </div>

    <div v-if="constraintMatch.matched.length || constraintMatch.warnings.length" class="constraint-box">
      <span class="eyebrow">品牌约束匹配</span>
      <div class="tag-row">
        <el-tag v-for="item in constraintMatch.matched" :key="item" type="success" effect="plain">{{ item }}</el-tag>
        <el-tag v-for="item in constraintMatch.warnings" :key="item" type="warning" effect="plain">{{ item }}</el-tag>
      </div>
      <div class="fit-summary">
        <p>{{ constraintMatch.brandFitDetail.summary }}</p>
        <ul v-if="constraintMatch.mismatches.length">
          <li v-for="item in constraintMatch.mismatches" :key="`${item.type}-${item.message}`">{{ item.message }}</li>
        </ul>
      </div>
    </div>

    <div class="report-grid">
      <article class="report-card">
        <span class="eyebrow">竞争格局报告</span>
        <h3>{{ competitionReport.marketType }}</h3>
        <dl>
          <div v-if="competitionReport.cr3"><dt>CR3</dt><dd>{{ competitionReport.cr3 }}</dd></div>
          <div><dt>CR5</dt><dd>{{ competitionReport.cr5 }}</dd></div>
          <div><dt>进入窗口</dt><dd>{{ competitionReport.entryWindow }}</dd></div>
        </dl>
        <p>{{ competitionReport.summary }}</p>
      </article>

      <article class="report-card">
        <span class="eyebrow">利润空间测算</span>
        <h3>目标售价 {{ profitAnalysis.targetPrice }}</h3>
        <dl>
          <div><dt>采购成本</dt><dd>{{ profitAnalysis.unitCost }}</dd></div>
          <div><dt>平台佣金</dt><dd>{{ profitAnalysis.platformFee }}</dd></div>
          <div><dt>广告占比</dt><dd>{{ profitAnalysis.adCost }}</dd></div>
          <div><dt>预估净利</dt><dd>{{ profitAnalysis.netMargin }}</dd></div>
        </dl>
        <p>{{ profitAnalysis.summary }}</p>
      </article>

      <article class="report-card">
        <span class="eyebrow">供应链可行性</span>
        <h3>MOQ {{ supplyChainFeasibility.moq }}</h3>
        <dl>
          <div><dt>备货周期</dt><dd>{{ supplyChainFeasibility.leadTime }}</dd></div>
          <div><dt>产能</dt><dd>{{ supplyChainFeasibility.factoryCapacity }}</dd></div>
          <div><dt>风险</dt><dd>{{ supplyChainFeasibility.riskHint }}</dd></div>
        </dl>
        <p>{{ supplyChainFeasibility.conclusion }}</p>
      </article>

      <PlatformPlaybookPanel
        variant="card"
        :first-launch-platform="platformPlaybook.firstLaunchPlatform"
        :validation-platform="platformPlaybook.validationPlatform"
        :conversion-platform="platformPlaybook.conversionPlatform"
        :summary="platformPlaybook.summary"
        :execution-hints="platformPlaybook.executionHints"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import PlatformPlaybookPanel from '@/components/common/PlatformPlaybookPanel.vue'
import type { CompetitionReport, ConstraintMatch, PlatformPlaybook, ProfitAnalysis, SupplyChainFeasibility } from '@/types'

defineProps<{
  constraintMatch: ConstraintMatch
  competitionReport: CompetitionReport
  profitAnalysis: ProfitAnalysis
  supplyChainFeasibility: SupplyChainFeasibility
  platformPlaybook: PlatformPlaybook
}>()
</script>

<style scoped>
.reports {
  margin-top: 16px;
}

.reports-head h2 {
  margin: 0;
  font-size: 18px;
}

.reports-head p {
  margin: 6px 0 0;
  color: var(--muted);
}

.reports-ribbon {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.reports-ribbon article {
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.reports-ribbon span,
.reports-ribbon small {
  display: block;
}

.reports-ribbon span {
  color: var(--muted);
  font-size: 12px;
}

.reports-ribbon b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 18px;
  line-height: 1.5;
}

.reports-ribbon small {
  margin-top: 10px;
  color: var(--muted);
  line-height: 1.6;
}

.eyebrow {
  display: inline-block;
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
}

.constraint-box {
  margin-top: 16px;
  padding: 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.76);
}

.fit-summary {
  margin-top: 12px;
  color: var(--muted);
}

.fit-summary p {
  margin: 0;
  line-height: 1.7;
}

.fit-summary ul {
  margin: 10px 0 0;
  padding-left: 18px;
  line-height: 1.7;
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.report-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-top: 16px;
}

.report-card {
  padding: 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.report-card h3 {
  margin: 10px 0 0;
  font-size: 16px;
  color: var(--ink-strong);
}

.report-card dl {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
  margin: 14px 0;
}

.report-card dt {
  color: var(--muted);
  font-size: 12px;
}

.report-card dd {
  margin: 4px 0 0;
  color: var(--ink-strong);
  font-family: "IBM Plex Mono", monospace;
  font-size: 13px;
}

.report-card p {
  margin: 0;
  line-height: 1.7;
  color: var(--muted);
}

@media (max-width: 1100px) {
  .reports-ribbon,
  .report-grid {
    grid-template-columns: 1fr;
  }
}
</style>
