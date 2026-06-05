import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/data-prep' },
    { path: '/data-prep', name: 'data-prep', component: () => import('@/views/DataPrep.vue') },
    { path: '/radar', name: 'radar', component: () => import('@/views/SignalRadar.vue') },
    { path: '/insight', name: 'insight', component: () => import('@/views/Insight.vue') },
    { path: '/ranking', name: 'ranking', component: () => import('@/views/OpportunityRanking.vue') },
    { path: '/competitor', name: 'competitor', component: () => import('@/views/CompetitorWatch.vue') },
    { path: '/mobile', name: 'mobile', component: () => import('@/views/MobileDashboard.vue') },
    { path: '/attribution', name: 'attribution', component: () => import('@/views/AttributionReport.vue') },
    { path: '/brand-model', name: 'brand-model', component: () => import('@/views/BrandModel.vue') },
    { path: '/test-run', name: 'test-run', component: () => import('@/views/TestRunOptimization.vue') },
    { path: '/open-api', name: 'open-api', component: () => import('@/views/OpenApiConsole.vue') },
    { path: '/supply-match', name: 'supply-match', component: () => import('@/views/SupplyMatch.vue') },
    { path: '/team', name: 'team', component: () => import('@/views/TeamWorkspace.vue') },
    { path: '/opportunity/:cardId', name: 'opportunity', component: () => import('@/views/Opportunity.vue'), props: true },
    { path: '/report/:cardId', name: 'report', component: () => import('@/views/ReportPreview.vue'), props: true }
  ]
})

export default router
