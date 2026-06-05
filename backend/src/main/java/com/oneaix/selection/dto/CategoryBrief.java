package com.oneaix.selection.dto;

/** 洞察 TOP3 推荐明细（对齐 PRD 推荐明细字段）2026-06-04 */
public record CategoryBrief(
        String categoryName,
        String metric,
        String description,
        Long monthlySearchVolume,
        String growthRate12m,
        Integer socialHeat,
        String risingWords,
        String platformGrowthRates,
        String categoryDescription,
        String tamSamSomSummary
) {
}
