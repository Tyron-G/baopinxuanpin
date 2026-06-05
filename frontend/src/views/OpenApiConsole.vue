<template>
  <section>
    <PageHero
      eyebrow="开放 API"
      title="迭代2 数据接口控制台"
      description="使用 X-Api-Key 调用 /api/open/v1 下的只读接口，便于联调与嵌入外部系统。"
    />

    <section class="panel pad config-panel">
      <div class="config-row">
        <label>API Key</label>
        <el-input v-model="apiKey" placeholder="selection-open-demo" />
        <el-button @click="saveKey">保存到会话</el-button>
      </div>
      <div class="config-row">
        <label>brandId</label>
        <el-input-number v-model="brandId" :min="1" />
        <label>cardId</label>
        <el-input-number v-model="cardId" :min="1" />
        <label>platform</label>
        <el-select v-model="platform" style="width: 140px">
          <el-option label="全平台" value="全平台" />
          <el-option label="天猫" value="天猫" />
          <el-option label="抖音" value="抖音" />
        </el-select>
      </div>
      <p class="hint">默认密钥见 backend `application.yml` → `selection.open-api.key`</p>
    </section>

    <section class="panel pad">
      <span class="eyebrow">可用接口</span>
      <div class="endpoint-grid">
        <article v-for="item in endpoints" :key="item.path" class="endpoint-card">
          <div class="endpoint-head">
            <el-tag size="small" type="success">GET</el-tag>
            <code>/api/open/v1{{ item.path }}</code>
          </div>
          <p>{{ item.description }}</p>
          <el-button type="primary" link :loading="loadingPath === item.path" @click="invoke(item)">试调用</el-button>
        </article>
      </div>
    </section>

    <section v-if="responseText" class="panel pad response-panel">
      <div class="response-head">
        <span class="eyebrow">最近响应</span>
        <el-button link @click="responseText = ''">清空</el-button>
      </div>
      <pre>{{ responseText }}</pre>
    </section>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { api } from '@/api'
import { getBrandId } from '@/composables/useBrandContext'
import { getApiErrorMessage } from '@/lib/apiError'
import PageHero from '@/components/common/PageHero.vue'
import { ElMessage } from 'element-plus'

const STORAGE_KEY = 'selection-open-api-key'
const apiKey = ref('selection-open-demo')
const brandId = ref(getBrandId())
const cardId = ref(1)
const platform = ref('全平台')
const loadingPath = ref('')
const responseText = ref('')

const endpoints = [
  { path: '/radar/signals', description: '今日信号列表', needsCard: false },
  { path: '/ranking/top50', description: '机会榜单 TOP50', needsCard: false },
  { path: '/insight/cards', description: '洞察卡片视图', needsCard: false },
  { path: '/insight/summary', description: '洞察管理摘要', needsCard: false },
  { path: '/opportunity/{cardId}', description: '爆品机会详情', needsCard: true },
  { path: '/report/{cardId}', description: '选品报告 Markdown 结构', needsCard: true }
]

function saveKey() {
  sessionStorage.setItem(STORAGE_KEY, apiKey.value.trim())
  ElMessage.success('API Key 已保存到当前会话')
}

async function invoke(item: { path: string; needsCard: boolean }) {
  loadingPath.value = item.path
  try {
    const path = item.needsCard ? item.path.replace('{cardId}', String(cardId.value)) : item.path
    const params: Record<string, string | number> = {
      brandId: brandId.value,
      platform: platform.value
    }
    const data = await api.callOpenApi<unknown>(path, apiKey.value.trim(), params)
    responseText.value = JSON.stringify(data, null, 2)
  } catch (error) {
    ElMessage.error(getApiErrorMessage(error, '调用失败'))
  } finally {
    loadingPath.value = ''
  }
}

onMounted(() => {
  const saved = sessionStorage.getItem(STORAGE_KEY)
  if (saved) {
    apiKey.value = saved
  }
})
</script>

<style scoped>
.config-panel {
  margin-bottom: 16px;
}

.config-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  margin-bottom: 12px;
}

.config-row label {
  color: var(--muted);
  font-size: 12px;
}

.hint {
  margin: 0;
  color: var(--muted);
  font-size: 13px;
}

.endpoint-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 12px;
}

.endpoint-card {
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.88);
}

.endpoint-head {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}

.endpoint-card p {
  margin: 10px 0;
  color: var(--muted);
  line-height: 1.6;
}

.response-panel pre {
  margin: 12px 0 0;
  padding: 14px;
  border-radius: 12px;
  background: #0f172a;
  color: #e2e8f0;
  overflow: auto;
  max-height: 420px;
  font-size: 12px;
  line-height: 1.6;
}

.response-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

@media (max-width: 900px) {
  .endpoint-grid {
    grid-template-columns: 1fr;
  }
}
</style>
