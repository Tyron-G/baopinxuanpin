package com.oneaix.selection.dto;

/** 卖点/价格带/差异化建议（迭代1）2026-06-04 */
public record SellingPointSuggestion(
        String suggestedPriceBand,
        String sellingPoint,
        String differentiationDirection
) {
}
