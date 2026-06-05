package com.oneaix.selection.dto;

/** 机会榜单 TOP50 单项 2026-06-04 */
public record OpportunityRankItem(
        int rank,
        Long cardId,
        String productTitle,
        String categoryName,
        int opportunityScore,
        String recommendationReason,
        String decision,
        SellingPointSuggestion sellingPoint
) {
}
