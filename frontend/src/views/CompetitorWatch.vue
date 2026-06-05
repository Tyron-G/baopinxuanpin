<template>
  <section>
    <PageHero
      eyebrow="竞品监控"
      title="把值得长期盯的对标对象稳定放进工作台"
      description="手动添加或承接信号来源的竞品对象，持续跟踪它们的上新节奏、爆品样本和差评痛点。"
    >
      <template #actions>
        <el-button :loading="discovering" @click="discover">自动发现竞品</el-button>
        <el-button type="primary" :icon="Plus" @click="dialogVisible = true">添加竞品</el-button>
      </template>
    </PageHero>

    <div v-if="hasContext" class="panel pad context-banner">
      <div>
        <span class="eyebrow">来源上下文</span>
        <h2>{{ contextTitle }}</h2>
      </div>
      <p>{{ contextDescription }}</p>
      <div class="context-actions">
        <el-button v-if="contextCardId" type="primary" @click="backToOpportunity">返回机会分析</el-button>
        <el-button @click="backToInsight">返回洞察</el-button>
      </div>
    </div>

    <section class="panel pad competitor-hero">
      <div class="competitor-hero-main">
        <div class="competitor-copy">
          <span class="eyebrow">当前竞品池</span>
          <h2>{{ heroTitle }}</h2>
          <p>{{ heroDescription }}</p>
          <small>{{ filterSummary }}</small>
        </div>
        <div class="competitor-context">
          <article class="hero-context-card">
            <span>当前主平台</span>
            <b>{{ mainPlatformLabel }}</b>
            <small>{{ platformCount }} 个平台仍在跟踪范围内</small>
          </article>
          <article class="hero-context-card">
            <span>最强样本</span>
            <b>{{ leadShop?.shopName ?? '暂无对象' }}</b>
            <small>{{ leadShop ? `${leadShop.hitProductCount} 个爆品样本` : '等待竞品加入' }}</small>
          </article>
          <article class="hero-context-card">
            <span>信号承接对象</span>
            <b>{{ signalBackedCount }}</b>
            <small>来自信号雷达的高优先级对象</small>
          </article>
          <article class="hero-context-card">
            <span>手动补充对象</span>
            <b>{{ manualCount }}</b>
            <small>用于补齐同赛道的观察样本</small>
          </article>
        </div>
      </div>
    </section>

    <div class="metric-strip">
      <div class="metric"><span>追踪店铺</span><b>{{ filteredShops.length }}</b></div>
      <div class="metric"><span>覆盖平台</span><b>{{ platformCount }}</b></div>
      <div class="metric"><span>爆品总数</span><b>{{ totalHits }}</b></div>
      <div class="metric"><span>高优先级来源</span><b>{{ signalBackedCount }}</b></div>
    </div>

    <div class="panel pad toolbar">
      <div class="toolbar-head">
        <div>
          <span class="eyebrow">筛选控制台</span>
          <h2>先把当前观察范围锁清楚</h2>
        </div>
        <p>平台、类目、来源和排序会直接决定下面看到的是“广泛扫一遍”，还是“围绕某个机会链路精查对象”。</p>
      </div>
      <div class="toolbar-grid">
        <div class="toolbar-field">
          <span>平台</span>
          <el-select v-model="platformFilter" placeholder="全部平台">
            <el-option label="全部平台" value="全部" />
            <el-option v-for="item in platformOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </div>
        <div class="toolbar-field">
          <span>类目</span>
          <el-select v-model="categoryFilter" placeholder="全部类目">
            <el-option label="全部类目" value="全部" />
            <el-option v-for="item in categoryOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </div>
        <div class="toolbar-field">
          <span>来源信号</span>
          <el-select v-model="sourceFilter" placeholder="全部来源">
            <el-option label="全部来源" value="全部" />
            <el-option label="手动添加" value="手动添加" />
            <el-option v-for="item in sourceOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </div>
        <div class="toolbar-field">
          <span>排序方式</span>
          <el-select v-model="sortBy" placeholder="选择排序">
            <el-option label="爆品数优先" value="hit-desc" />
            <el-option label="最近添加" value="time-desc" />
            <el-option label="店铺名称" value="name-asc" />
          </el-select>
        </div>
      </div>
    </div>

    <div class="shop-grid">
      <article v-for="shop in filteredShops" :key="`${shop.shopName}-${shop.addedAt}`" class="shop-card panel pad">
        <header>
          <div class="shop-head-main">
            <div>
              <span class="eyebrow">{{ shop.platform }}</span>
              <h3>{{ shop.shopName }}</h3>
              <small>{{ shop.focusCategory }}</small>
            </div>
            <div class="shop-inline-meta">
              <span>添加于 {{ shop.addedAt }}</span>
              <span>{{ shop.sourceSignalType ? '信号承接' : '手动补充' }}</span>
            </div>
          </div>
          <div class="shop-status">
            <span>状态</span>
            <el-tag type="warning" effect="plain">追踪中</el-tag>
          </div>
        </header>
        <div class="shop-decision-banner">
          <div>
            <span>当前优先原因</span>
            <b>{{ shop.sourceSignalType ? '信号承接对象' : '手动补齐样本' }}</b>
          </div>
          <small>{{ shop.growthSignal }}</small>
        </div>
        <dl>
          <div><dt>最新爆品/上新</dt><dd>{{ shop.latestHit }}</dd></div>
          <div><dt>增长信号</dt><dd>{{ shop.growthSignal }}</dd></div>
        </dl>
        <div class="metric-row">
          <div class="metric-box"><span>最近上新</span><b>{{ shop.recentLaunch }}</b></div>
          <div class="metric-box"><span>爆品数</span><b>{{ shop.hitProductCount }}</b></div>
        </div>
        <div v-if="shop.opportunityTags.length" class="tag-row">
          <el-tag
            v-for="tag in shop.opportunityTags"
            :key="`${shop.shopName}-${tag}`"
            size="small"
            effect="plain"
            :type="tagType(tag)"
          >
            {{ tag }}
          </el-tag>
        </div>
        <div v-if="shop.complaintTopics.length" class="complaint-box">
          <span class="eyebrow">差评主题</span>
          <div class="complaint-tags">
            <el-tag v-for="topic in shop.complaintTopics" :key="topic" type="danger" effect="plain" size="small">
              {{ topic }}
            </el-tag>
          </div>
        </div>
        <div class="shop-actions">
          <div class="action-label">
            <span>后续动作</span>
            <small>{{ shop.cardId ? '已可进入机会分析' : '等待机会分析关联' }}</small>
          </div>
          <div class="action-buttons">
            <el-button v-if="shop.cardId" type="primary" link @click="openOpportunity(shop.cardId)">进入机会分析 →</el-button>
            <el-button v-if="shop.focusCategory" link @click="openInsight(shop.focusCategory)">回到洞察</el-button>
          </div>
        </div>
      </article>
    </div>

    <CompetitorTimelinePanel
      :items="timelines"
      title="最近 4 周平台表现"
      description="把当前筛选下的竞品按平台与时间串起来，判断它们是在持续走强，还是只是短期异动。"
    />

    <div v-if="!filteredShops.length && !timelines.length" class="panel pad empty-panel">
      <el-result icon="info" title="当前筛选下暂无竞品" sub-title="可以放宽平台、类目或来源信号条件后再看。">
        <template #extra>
          <el-button @click="resetFilters">重置筛选</el-button>
        </template>
      </el-result>
    </div>

    <el-dialog v-model="dialogVisible" title="添加竞品店铺" width="480px">
      <el-form label-position="top">
        <el-form-item label="店铺名称">
          <el-input v-model="form.shopName" placeholder="如：某某旗舰店" />
        </el-form-item>
        <el-form-item label="平台">
          <el-select v-model="form.platform" placeholder="选择平台">
            <el-option label="天猫" value="天猫" />
            <el-option label="抖音" value="抖音" />
            <el-option label="小红书" value="小红书" />
            <el-option label="淘宝" value="淘宝" />
          </el-select>
        </el-form-item>
        <el-form-item label="关注品类">
          <el-input v-model="form.focusCategory" placeholder="如：宠物智能用品" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitShop">开始监控</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { api } from '@/api'
import { getBrandId, setBrandId } from '@/composables/useBrandContext'
import { getApiErrorMessage } from '@/lib/apiError'
import CompetitorTimelinePanel from '@/components/common/CompetitorTimelinePanel.vue'
import PageHero from '@/components/common/PageHero.vue'
import type { CompetitorShop, CompetitorTimeline } from '@/types'

const route = useRoute()
const router = useRouter()

const shops = ref<CompetitorShop[]>([])
const timelines = ref<CompetitorTimeline[]>([])
const dialogVisible = ref(false)
const saving = ref(false)
const discovering = ref(false)

const platformFilter = ref('全部')
const categoryFilter = ref('全部')
const sourceFilter = ref('全部')
const sortBy = ref('hit-desc')

const form = reactive({
  shopName: '',
  platform: '天猫',
  focusCategory: '',
  cardId: null as number | null,
  sourceSignalId: '',
  sourceSignalType: ''
})

const brandId = computed(() => {
  const queryId = Number(route.query.brandId)
  if (Number.isFinite(queryId) && queryId > 0) {
    setBrandId(queryId)
    return queryId
  }
  return getBrandId()
})

const insightPlatform = computed(() => {
  const value = route.query.insightPlatform
  return typeof value === 'string' && value.trim() ? value : ''
})

const insightCategory = computed(() => {
  const value = route.query.category
  return typeof value === 'string' && value.trim() ? value : ''
})

const contextCardId = computed(() => {
  const value = Number(route.query.cardId)
  return Number.isFinite(value) && value > 0 ? value : null
})

const hasContext = computed(() => !!(insightPlatform.value || insightCategory.value || contextCardId.value))

const contextTitle = computed(() => {
  const category = insightCategory.value || '当前筛选类目'
  const platform = insightPlatform.value || '全平台'
  return `${category} · ${platform} 视角`
})

const contextDescription = computed(() =>
  contextCardId.value
    ? '当前竞品列表延续自机会分析链路，你可以先看跟踪对象，再稳定返回原分析页面继续判断。'
    : '当前竞品列表带有明确的来源上下文，建议结合平台视角继续判断竞品是在持续走强，还是短期异动。'
)

const platformOptions = computed(() => [...new Set(shops.value.map((item) => item.platform))])
const categoryOptions = computed(() => [...new Set(shops.value.map((item) => item.focusCategory))])
const sourceOptions = computed(() =>
  [...new Set(shops.value.map((item) => item.sourceSignalType).filter((item): item is string => !!item))]
)

const filteredShops = computed(() => {
  let items = [...shops.value]
  if (platformFilter.value !== '全部') {
    items = items.filter((item) => item.platform === platformFilter.value)
  }
  if (categoryFilter.value !== '全部') {
    items = items.filter((item) => item.focusCategory === categoryFilter.value)
  }
  if (sourceFilter.value !== '全部') {
    items = sourceFilter.value === '手动添加'
      ? items.filter((item) => !item.sourceSignalType)
      : items.filter((item) => item.sourceSignalType === sourceFilter.value)
  }
  if (sortBy.value === 'name-asc') {
    items.sort((left, right) => left.shopName.localeCompare(right.shopName, 'zh-CN'))
  } else if (sortBy.value === 'time-desc') {
    items.sort((left, right) => right.addedAt.localeCompare(left.addedAt))
  } else {
    items.sort((left, right) => right.hitProductCount - left.hitProductCount || right.addedAt.localeCompare(left.addedAt))
  }
  return items
})

const platformCount = computed(() => new Set(filteredShops.value.map((item) => item.platform)).size)
const totalHits = computed(() => filteredShops.value.reduce((sum, item) => sum + item.hitProductCount, 0))
const signalBackedCount = computed(() => filteredShops.value.filter((item) => !!item.sourceSignalType).length)
const manualCount = computed(() => filteredShops.value.length - signalBackedCount.value)
const leadShop = computed(() => filteredShops.value[0])
const mainPlatformLabel = computed(() => leadShop.value?.platform ?? (platformFilter.value === '全部' ? '全平台' : platformFilter.value))
const sortLabel = computed(() => (sortBy.value === 'time-desc' ? '最近添加' : sortBy.value === 'name-asc' ? '店铺名称' : '爆品数优先'))

const heroTitle = computed(() =>
  leadShop.value
    ? `${leadShop.value.focusCategory} 当前优先看 ${leadShop.value.shopName}`
    : categoryFilter.value !== '全部'
      ? `${categoryFilter.value} 当前暂无可见竞品`
      : '先把同赛道的关键竞品稳住'
)

const heroDescription = computed(() =>
  leadShop.value
    ? `${leadShop.value.growthSignal}。建议先看它最近上新和差评主题，再判断当前机会点到底是长期趋势还是短期异动。`
    : '当前筛选范围内还没有稳定对象，建议放宽平台、类目或来源信号后继续查看。'
)

const filterSummary = computed(
  () =>
    `平台 ${platformFilter.value === '全部' ? '全平台' : platformFilter.value} · 类目 ${categoryFilter.value === '全部' ? '全部类目' : categoryFilter.value} · 来源 ${sourceFilter.value === '全部' ? '全部来源' : sourceFilter.value} · 排序 ${sortLabel.value}`
)

function tagType(label: string) {
  if (label.includes('放弃') || label.includes('风险')) return 'danger'
  if (label.includes('观望') || label.includes('搜索')) return 'warning'
  if (label.includes('立项') || label.includes('匹配') || label.includes('目标')) return 'success'
  return 'info'
}

function applyQueryFilters() {
  platformFilter.value = typeof route.query.platform === 'string' ? route.query.platform : '全部'
  categoryFilter.value = typeof route.query.category === 'string' ? route.query.category : '全部'
  sourceFilter.value = typeof route.query.source === 'string' ? route.query.source : '全部'
}

async function loadTimelines() {
  timelines.value = await api.getCompetitorTimelines(
    brandId.value,
    categoryFilter.value === '全部' ? '' : categoryFilter.value,
    platformFilter.value === '全部' ? '全平台' : platformFilter.value
  )
}

async function load() {
  applyQueryFilters()
  shops.value = await api.getCompetitors(brandId.value)
  await loadTimelines()
}

async function discover() {
  discovering.value = true
  try {
    const created = await api.discoverCompetitors(brandId.value)
    ElMessage.success(`已自动添加 ${created.length} 个对标竞品`)
    await load()
  } catch (error) {
    ElMessage.error(getApiErrorMessage(error, '自动发现失败'))
  } finally {
    discovering.value = false
  }
}

async function submitShop() {
  if (!form.shopName.trim()) {
    ElMessage.warning('请输入店铺名称')
    return
  }
  saving.value = true
  try {
    await api.addCompetitor(brandId.value, form)
    ElMessage.success('竞品已加入监控')
    dialogVisible.value = false
    form.shopName = ''
    form.focusCategory = ''
    form.cardId = null
    form.sourceSignalId = ''
    form.sourceSignalType = ''
    await load()
  } catch (error) {
    ElMessage.error(getApiErrorMessage(error, '添加失败，请稍后重试'))
  } finally {
    saving.value = false
  }
}

function openOpportunity(cardId: number) {
  const platform =
    (typeof route.query.insightPlatform === 'string' && route.query.insightPlatform.trim()
      ? route.query.insightPlatform
      : platformFilter.value !== '全部'
        ? platformFilter.value
        : '全平台')
  router.push({ path: `/opportunity/${cardId}`, query: { brandId: brandId.value, platform } })
}

function openInsight(category: string) {
  const platform =
    (typeof route.query.insightPlatform === 'string' && route.query.insightPlatform.trim()
      ? route.query.insightPlatform
      : platformFilter.value !== '全部'
        ? platformFilter.value
        : '全平台')
  router.push({ path: '/insight', query: { brandId: brandId.value, category, platform } })
}

function backToOpportunity() {
  if (!contextCardId.value) return
  router.push({
    path: `/opportunity/${contextCardId.value}`,
    query: { brandId: brandId.value, platform: insightPlatform.value || '全平台' }
  })
}

function backToInsight() {
  router.push({
    path: '/insight',
    query: {
      brandId: brandId.value,
      ...(insightCategory.value ? { category: insightCategory.value } : {}),
      platform: insightPlatform.value || '全平台'
    }
  })
}

function resetFilters() {
  platformFilter.value = '全部'
  categoryFilter.value = '全部'
  sourceFilter.value = '全部'
  sortBy.value = 'hit-desc'
}

onMounted(load)
watch(() => route.query, load)
watch(brandId, load)
watch([platformFilter, categoryFilter], loadTimelines)
</script>

<style scoped>
.context-banner {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1.1fr) auto;
  gap: 16px;
  align-items: center;
  margin-bottom: 16px;
}

.context-banner h2 {
  margin: 8px 0 0;
  color: var(--ink-strong);
  font-size: 18px;
}

.context-banner p {
  margin: 0;
  color: var(--muted);
  line-height: 1.7;
}

.context-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.competitor-hero {
  margin-bottom: 16px;
  background: linear-gradient(135deg, rgba(239, 246, 255, 0.92), rgba(240, 253, 250, 0.8)), #fff;
}

.competitor-hero-main {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(320px, 0.9fr);
  gap: 18px;
}

.competitor-copy h2 {
  margin: 10px 0 0;
  color: var(--ink-strong);
  font-size: 30px;
  line-height: 1.3;
}

.competitor-copy p,
.competitor-copy small {
  display: block;
  color: var(--muted);
  line-height: 1.7;
}

.competitor-copy p {
  margin: 12px 0 0;
  max-width: 760px;
}

.competitor-copy small {
  margin-top: 10px;
}

.competitor-context {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.hero-context-card {
  padding: 14px 16px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: var(--shadow-sm);
}

.hero-context-card span,
.hero-context-card small {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.hero-context-card b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: 'IBM Plex Mono', monospace;
  font-size: 18px;
}

.toolbar {
  margin-bottom: 16px;
}

.toolbar-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 16px;
}

.toolbar-head h2 {
  margin: 8px 0 0;
  color: var(--ink-strong);
  font-size: 20px;
}

.toolbar-head p {
  margin: 0;
  max-width: 560px;
  color: var(--muted);
  line-height: 1.7;
  text-align: right;
}

.toolbar-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.toolbar-field span {
  display: block;
  margin-bottom: 8px;
  color: var(--muted);
  font-size: 12px;
}

.shop-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.shop-card header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.shop-head-main {
  display: grid;
  gap: 12px;
}

.shop-inline-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.shop-inline-meta span {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: rgba(248, 250, 252, 0.96);
  border: 1px solid rgba(148, 163, 184, 0.14);
  color: var(--muted);
  font-family: 'IBM Plex Mono', monospace;
  font-size: 12px;
}

.shop-decision-banner {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin: 14px 0 0;
  padding: 14px 16px;
  border: 1px solid rgba(37, 99, 235, 0.14);
  border-radius: 14px;
  background: linear-gradient(180deg, rgba(239, 246, 255, 0.84), rgba(247, 250, 255, 0.94));
}

.shop-decision-banner span,
.shop-decision-banner small,
.shop-decision-banner b {
  display: block;
}

.shop-decision-banner span {
  color: var(--muted);
  font-size: 12px;
}

.shop-decision-banner b {
  margin-top: 8px;
  color: var(--ink-strong);
  font-family: 'IBM Plex Mono', monospace;
}

dl {
  display: grid;
  gap: 12px;
  margin: 16px 0;
}

dt {
  color: var(--muted);
  font-size: 12px;
}

dd {
  margin: 6px 0 0;
  color: var(--ink-strong);
  line-height: 1.7;
}

.metric-row {
  display: grid;
  grid-template-columns: 1.5fr 0.5fr;
  gap: 12px;
  margin-bottom: 12px;
}

.metric-box,
.complaint-box {
  padding: 14px 16px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 251, 255, 0.96));
}

.metric-box span {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.metric-box b {
  display: block;
  margin-top: 8px;
  color: var(--ink-strong);
}

.tag-row,
.complaint-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-row {
  margin-bottom: 12px;
}

.complaint-box {
  margin-bottom: 12px;
}

.complaint-tags {
  margin-top: 10px;
}

.shop-actions {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--line);
}

.action-label span,
.action-label small {
  display: block;
  color: var(--muted);
  font-size: 12px;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: flex-end;
}

.empty-panel {
  margin-top: 16px;
  min-height: 240px;
  display: grid;
  align-items: center;
}

@media (max-width: 1100px) {
  .context-banner,
  .competitor-hero-main,
  .toolbar-head,
  .competitor-context,
  .shop-grid,
  .toolbar-grid,
  .metric-row {
    grid-template-columns: 1fr;
  }

  .toolbar-head p {
    max-width: none;
    text-align: left;
  }

  .context-actions,
  .action-buttons {
    justify-content: flex-start;
  }
}
</style>
