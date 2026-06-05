package com.oneaix.selection.dto;

/** 2026-06-03 品牌适配明细 */
public record BrandFitDetail(
        String platformFit,
        String budgetFit,
        String profitFit,
        String supplyChainFit,
        String stockCycleFit,
        String overallFitLevel,
        String summary
) {
}
