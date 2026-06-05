<template>
  <div class="panel pad scene-panel">
    <div class="scene-head">
      <div>
        <h2>人群场景图</h2>
        <p>把目标人群、使用场景、核心痛点和产品价值压缩到一块分析板里。</p>
      </div>
      <div class="scene-summary">
        <span>覆盖 {{ scenes.length }} 类人群</span>
        <b>{{ summaryLabel }}</b>
      </div>
    </div>

    <div v-if="!scenes.length" class="scene-empty">
      <el-empty description="暂未生成可分析的人群场景" />
    </div>

    <div v-else class="scene-board">
      <article
        v-for="(scene, index) in scenes"
        :key="`${scene.crowd}-${scene.scene}`"
        class="scene-card"
        :class="toneClass(index)"
      >
        <div class="scene-card__top">
          <span class="scene-index">{{ formatIndex(index) }}</span>
          <div class="scene-orbit" aria-hidden="true">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>

        <div class="scene-card__body">
          <div class="scene-role">
            <div class="scene-avatar">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div>
              <small>目标人群</small>
              <strong>{{ scene.crowd }}</strong>
            </div>
          </div>

          <div class="scene-flow" aria-hidden="true">
            <span></span>
            <el-icon><ArrowRight /></el-icon>
            <span></span>
          </div>

          <div class="scene-chip">
            <small>核心场景</small>
            <b>{{ scene.scene }}</b>
          </div>
        </div>

        <div class="scene-focus">
          <section class="focus-block pain">
            <header>
              <el-icon><WarningFilled /></el-icon>
              <span>痛点信号</span>
            </header>
            <p>{{ scene.painPoint }}</p>
          </section>

          <section class="focus-block value">
            <header>
              <el-icon><Opportunity /></el-icon>
              <span>价值方向</span>
            </header>
            <p>{{ scene.desiredValue }}</p>
          </section>
        </div>
      </article>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ArrowRight, Opportunity, UserFilled, WarningFilled } from '@element-plus/icons-vue'
import { computed } from 'vue'
import type { CrowdScene } from '@/types'

const props = defineProps<{ scenes: CrowdScene[] }>()

const summaryLabel = computed(() => {
  if (!props.scenes.length) return '暂无场景'
  if (props.scenes.length === 1) return '单点突破'
  if (props.scenes.length === 2) return '双场景验证'
  return '多场景覆盖'
})

function toneClass(index: number) {
  return index % 2 === 0 ? 'tone-green' : 'tone-blue'
}

function formatIndex(index: number) {
  return String(index + 1).padStart(2, '0')
}
</script>

<style scoped>
.scene-panel {
  overflow: hidden;
}

.scene-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 18px;
}

.scene-head h2 {
  margin: 0;
  font-size: 18px;
}

.scene-head p {
  margin: 8px 0 0;
  color: var(--muted);
  line-height: 1.6;
  max-width: 460px;
}

.scene-summary {
  min-width: 152px;
  padding: 12px 14px;
  border-radius: 12px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  background:
    linear-gradient(180deg, rgba(252, 253, 255, 0.98), rgba(246, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.scene-summary span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.scene-summary b {
  display: block;
  margin-top: 6px;
  font-size: 18px;
}

.scene-board {
  display: grid;
  gap: 14px;
}

.scene-empty {
  min-height: 220px;
  display: grid;
  place-items: center;
  border: 1px dashed var(--line);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.56);
}

.scene-card {
  position: relative;
  overflow: hidden;
  border-radius: 14px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.99), rgba(248, 251, 255, 0.96)),
    #ffffff;
  padding: 16px;
  box-shadow: var(--shadow-sm);
}

.scene-card::after {
  content: "";
  position: absolute;
  inset: 0 auto 0 0;
  width: 3px;
  border-radius: 0;
  opacity: 1;
}

.scene-card.tone-green::after {
  background: linear-gradient(180deg, rgba(31, 143, 116, 0.82), rgba(31, 143, 116, 0.22));
}

.scene-card.tone-blue::after {
  background: linear-gradient(180deg, rgba(47, 128, 237, 0.82), rgba(47, 128, 237, 0.22));
}

.scene-card__top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.scene-index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 36px;
  height: 24px;
  padding: 0 8px;
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.18);
  background: rgba(255, 255, 255, 0.92);
  font-size: 12px;
  font-weight: 700;
  color: var(--muted);
}

.scene-orbit {
  display: inline-flex;
  gap: 6px;
}

.scene-orbit span {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: rgba(23, 33, 31, 0.14);
}

.scene-orbit span:nth-child(1) {
  background: rgba(31, 143, 116, 0.62);
}

.scene-orbit span:nth-child(2) {
  background: rgba(47, 128, 237, 0.52);
}

.scene-orbit span:nth-child(3) {
  background: rgba(242, 165, 65, 0.58);
}

.scene-card__body {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) auto minmax(0, 1fr);
  gap: 14px;
  align-items: center;
}

.scene-role {
  display: flex;
  gap: 12px;
  align-items: center;
}

.scene-avatar {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: linear-gradient(135deg, #1f8f74, #2563eb);
  box-shadow: inset 0 -10px 16px rgba(255, 255, 255, 0.08);
}

.scene-role small,
.scene-chip small {
  display: block;
  color: var(--muted);
  font-size: 12px;
  margin-bottom: 4px;
}

.scene-role strong,
.scene-chip b {
  display: block;
  font-size: 16px;
  line-height: 1.4;
}

.scene-flow {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--muted);
}

.scene-flow span {
  width: 26px;
  height: 1px;
  background: rgba(23, 33, 31, 0.18);
}

.scene-chip {
  border-radius: 12px;
  border: 1px solid rgba(148, 163, 184, 0.18);
  background:
    linear-gradient(180deg, rgba(252, 253, 255, 0.98), rgba(246, 250, 255, 0.94)),
    #ffffff;
  padding: 12px;
}

.scene-focus {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.focus-block {
  position: relative;
  z-index: 1;
  min-height: 112px;
  padding: 14px;
  border-radius: 12px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 251, 255, 0.94)),
    #ffffff;
}

.focus-block header {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 700;
}

.focus-block p {
  margin: 10px 0 0;
  color: #31413c;
  line-height: 1.7;
}

.focus-block.pain header {
  color: var(--coral);
}

.focus-block.value header {
  color: var(--green);
}

@media (max-width: 900px) {
  .scene-head {
    display: grid;
  }

  .scene-summary {
    min-width: 0;
  }

  .scene-card__body,
  .scene-focus {
    grid-template-columns: 1fr;
  }

  .scene-flow {
    justify-content: flex-start;
  }
}
</style>
