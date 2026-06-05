package com.oneaix.selection.dto;

/** 2026-06-03 侧边栏与首页 KPI 摘要 */
public record DashboardSummary(
        int monitoredCategories,
        int activeSignals,
        int bestScore,
        String topCategory,
        Long bestCardId,
        String brandName,
        int watchlistCount,
        int opportunityRankingTotal
) {
}
