package com.oneaix.selection.dto;

import com.oneaix.selection.entity.BrandInfo;

import java.util.List;

public record InsightSummary(
        BrandInfo brand,
        String trendConclusion,
        List<CategoryBrief> trendTop3,
        String competitionConclusion,
        List<CategoryBrief> competitionTop3,
        String supplyConclusion,
        List<CategoryBrief> supplyTop3,
        String trendJudgment,
        List<PainPointItem> painPointItems,
        List<String> painPoints,
        String crowdProfile,
        List<InsightCardView> skippedCards,
        List<String> blockingReasons,
        List<String> recommendedAdjustments,
        List<String> filteredCategories,
        List<PotentialCategoryItem> potentialCategories,
        MarketScaleBrief marketScaleBrief
) {
}
