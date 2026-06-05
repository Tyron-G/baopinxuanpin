package com.oneaix.selection.dto;

/** 2026-06-03 爆品信号雷达条目 */
public record SignalItem(
        String id,
        String categoryName,
        String signalType,
        String strength,
        int score,
        int confidence,
        String platform,
        String metric,
        String summary,
        String discoveredAt,
        Long cardId,
        String recommendedAction,
        java.util.List<String> reasonTags,
        String decision,
        BrandFitDetail brandFitDetail,
        java.util.List<ReasonItem> reasons,
        java.util.List<RiskItem> risks,
        java.util.List<ConstraintMismatch> mismatches
) {
}
