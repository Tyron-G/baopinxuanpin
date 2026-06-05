package com.oneaix.selection.dto;

public record ProfitAnalysis(
        String targetPrice,
        String unitCost,
        String platformFee,
        String adCost,
        String netMargin,
        String summary
) {
}
