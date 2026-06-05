<template>
  <section v-if="overview" class="panel pad connectors-panel">
    <span class="eyebrow">第三方数据源（样例已接入）</span>
    <h2>蝉妈妈 · 飞瓜 · 1688 · 专利检索</h2>
    <p class="connectors-note">
      市场数据源：<b>{{ overview.marketDataSource }}</b> · 最近同步 {{ overview.lastMarketSyncAt }}（{{ overview.lastMarketSyncStatus }}）
    </p>
    <div class="sync-row">
      <el-button type="primary" :loading="syncing" @click="syncMarket">拉取外部市场 API（需配置后端）</el-button>
    </div>

    <div class="connector-status-grid">
      <article v-for="item in overview.connectors" :key="item.provider">
        <div class="connector-title">
          <b>{{ item.provider }}</b>
          <el-tag size="small" type="success" effect="plain">{{ item.status }}</el-tag>
        </div>
        <small>{{ item.providerType }} · {{ item.lastSyncedAt }}</small>
        <p>{{ item.sampleHighlight }}</p>
        <span class="coverage">{{ item.coverage }}</span>
      </article>
    </div>

    <div class="feed-columns">
      <div>
        <h3>蝉妈妈 · 直播/电商样例</h3>
        <ul>
          <li v-for="feed in overview.chanmamaFeeds" :key="feed.productTitle">
            <b>{{ feed.productTitle }}</b>
            <small>{{ feed.categoryName }} · {{ feed.salesGrowth7d }} · {{ feed.liveRank }} · {{ feed.gmvBand }}</small>
          </li>
        </ul>
      </div>
      <div>
        <h3>飞瓜 · 短视频样例</h3>
        <ul>
          <li v-for="feed in overview.feiguaFeeds" :key="feed.videoTitle">
            <b>{{ feed.videoTitle }}</b>
            <small>{{ feed.categoryName }} · {{ feed.playGrowth7d }} · {{ feed.influencerTier }} · {{ feed.hotTopic }}</small>
          </li>
        </ul>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { api } from '@/api'
import { getBrandId } from '@/composables/useBrandContext'
import type { DataConnectorsOverview } from '@/types'
import { ElMessage } from 'element-plus'

const overview = ref<DataConnectorsOverview>()
const syncing = ref(false)

async function refresh() {
  overview.value = await api.getDataConnectors()
}

async function syncMarket() {
  syncing.value = true
  try {
    const result = await api.syncMarketData(getBrandId())
    ElMessage[result.success ? 'success' : 'warning'](result.message)
    await refresh()
  } finally {
    syncing.value = false
  }
}

onMounted(refresh)
</script>

<style scoped>
.connectors-panel {
  margin-bottom: 16px;
}

.connectors-note {
  margin: 8px 0 0;
  color: var(--muted);
  line-height: 1.7;
}

.sync-row {
  margin: 12px 0 4px;
}

.connector-status-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.connector-status-grid article {
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background: var(--panel-bg);
  box-shadow: var(--shadow-sm);
}

.connector-title {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  align-items: center;
}

.connector-title b {
  color: var(--ink-strong);
}

.connector-status-grid small,
.connector-status-grid p {
  display: block;
  margin-top: 8px;
  color: var(--muted);
  line-height: 1.6;
}

.coverage {
  display: inline-block;
  margin-top: 10px;
  color: var(--accent);
  font-size: 12px;
}

.feed-columns {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.feed-columns h3 {
  margin: 0 0 10px;
  color: var(--ink-strong);
  font-size: 15px;
}

.feed-columns ul {
  margin: 0;
  padding: 0;
  list-style: none;
}

.feed-columns li {
  padding: 10px 0;
  border-bottom: 1px solid var(--line);
}

.feed-columns li:last-child {
  border-bottom: none;
}

.feed-columns b {
  display: block;
  color: var(--ink-strong);
}

.feed-columns small {
  display: block;
  margin-top: 6px;
  color: var(--muted);
  line-height: 1.6;
}

@media (max-width: 1100px) {
  .connector-status-grid,
  .feed-columns {
    grid-template-columns: 1fr;
  }
}
</style>
