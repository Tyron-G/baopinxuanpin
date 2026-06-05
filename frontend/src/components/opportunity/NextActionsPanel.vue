<template>
  <section class="panel pad actions-panel">
    <div class="actions-head">
      <div>
        <span class="eyebrow">下一步动作</span>
        <h2>验证与推进清单</h2>
        <p>把当前推荐转成可执行动作，避免停留在“看完报告”阶段。</p>
      </div>
    </div>

    <div class="actions-grid">
      <article v-for="item in actions" :key="item.title" class="action-card">
        <header class="action-card-head">
          <div class="head-copy">
            <b>{{ item.title }}</b>
            <small>{{ item.expectedGoal }}</small>
          </div>
          <div class="tag-group">
            <el-tag size="small" :type="statusType(item.status)" effect="plain">{{ item.status }}</el-tag>
            <el-tag size="small" effect="plain">{{ item.priority }}</el-tag>
          </div>
        </header>

        <div class="meta-ribbon">
          <article>
            <span>负责人</span>
            <b>{{ item.ownerRole }}</b>
          </article>
          <article>
            <span>预计时效</span>
            <b>{{ item.eta }}</b>
          </article>
          <article>
            <span>最近更新</span>
            <b>{{ item.updatedAt }}</b>
          </article>
        </div>

        <div class="note-box">
          <span>执行说明</span>
          <small>{{ item.note }}</small>
        </div>
        <div class="status-field">
          <span>推进状态</span>
          <el-segmented
            class="status-segmented"
            :model-value="item.status"
            :options="statusOptions"
            @change="handleStatusChange(item.title, $event)"
          />
        </div>
        <footer class="action-card-foot">
          <span>当前状态会同步写回这轮机会与报告视图。</span>
        </footer>
      </article>
    </div>
  </section>
</template>

<script setup lang="ts">
import type { ReportAction } from '@/types'

defineProps<{
  actions: ReportAction[]
}>()

const emit = defineEmits<{
  'update-status': [{ title: string; status: string }]
}>()

const statusOptions = ['待执行', '待确认', '进行中', '已完成', '已放弃']

function statusType(status: string) {
  if (status.includes('进行')) return 'warning'
  if (status.includes('完成')) return 'success'
  if (status.includes('确认')) return 'info'
  return ''
}

function handleStatusChange(title: string, value: string | number | boolean) {
  emit('update-status', { title, status: String(value) })
}
</script>

<style scoped>
.actions-panel {
  margin-top: 16px;
}

.actions-head h2 {
  margin: 0;
  font-size: 18px;
}

.actions-head p {
  margin: 6px 0 0;
  color: var(--muted);
}

.eyebrow {
  display: inline-block;
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.action-card {
  display: grid;
  gap: 14px;
  padding: 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(247, 250, 255, 0.96)),
    #ffffff;
  box-shadow: var(--shadow-sm);
}

.action-card-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 14px;
}

.head-copy {
  min-width: 0;
}

.head-copy b,
.head-copy small {
  display: block;
}

.tag-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: flex-end;
  flex: 0 0 auto;
}

.head-copy b {
  color: var(--ink-strong);
  font-size: 18px;
  line-height: 1.45;
}

.head-copy small {
  margin-top: 8px;
  color: var(--muted);
  line-height: 1.7;
}

.meta-ribbon {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.meta-ribbon article {
  padding: 12px 14px;
  border: 1px solid var(--line);
  border-radius: 12px;
  background: linear-gradient(180deg, rgba(251, 252, 255, 0.98), rgba(246, 249, 253, 0.96));
}

.meta-ribbon span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.meta-ribbon b {
  display: block;
  margin-top: 6px;
  color: var(--ink-strong);
  line-height: 1.6;
  overflow-wrap: anywhere;
}

.note-box {
  padding: 12px;
  border: 1px solid var(--line);
  border-radius: 12px;
  background: rgba(248, 250, 252, 0.9);
}

.note-box span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.note-box small {
  display: block;
  margin-top: 6px;
  color: var(--ink);
  line-height: 1.6;
}

.status-field {
  display: grid;
  gap: 10px;
}

.status-field span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.status-field :deep(.el-segmented) {
  width: 100%;
  max-width: 100%;
}

.status-field :deep(.el-segmented__group) {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  width: 100%;
}

.status-field :deep(.el-segmented__item) {
  min-width: 0;
}

.status-field :deep(.el-segmented__item-label) {
  white-space: normal;
  line-height: 1.25;
  text-align: center;
  padding: 8px 4px;
}

.action-card-foot {
  color: var(--muted);
  font-size: 12px;
  line-height: 1.6;
  padding-top: 2px;
}

@media (max-width: 1100px) {
  .meta-ribbon {
    grid-template-columns: 1fr;
  }

  .action-card-head {
    display: grid;
  }

  .tag-group {
    justify-content: flex-start;
  }

  .status-field :deep(.el-segmented__group) {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .actions-grid {
    grid-template-columns: 1fr;
  }

  .status-field :deep(.el-segmented__group) {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
