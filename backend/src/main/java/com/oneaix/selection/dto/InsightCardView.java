package com.oneaix.selection.dto;

import com.oneaix.selection.entity.InsightCard;

import java.util.List;

/** 2026-06-03 带品牌约束匹配标签的洞察卡片视图 */
public record InsightCardView(
        InsightCard card,
        boolean pinned,
        boolean budgetCompatible,
        List<String> matchTags,
        String decision,
        ScoreBreakdown scoreBreakdown,
        List<ReasonItem> reasons,
        List<RiskItem> risks,
        BrandFitDetail brandFitDetail,
        List<ConstraintMismatch> mismatches,
        java.math.BigDecimal homogeneityScore
) {
}
