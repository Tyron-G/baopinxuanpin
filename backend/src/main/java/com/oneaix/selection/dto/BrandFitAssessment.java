package com.oneaix.selection.dto;

import com.oneaix.selection.enums.FitLevel;

/** 品牌适配结构化评估结果 2026-06-04 */
public record BrandFitAssessment(
        FitLevel platformLevel,
        String platformLabel,
        FitLevel profitLevel,
        String profitLabel,
        FitLevel supplyChainLevel,
        String supplyChainLabel,
        FitLevel stockCycleLevel,
        String stockCycleLabel
) {
    public int platformBoost() {
        return platformLevel.scoreBoost();
    }

    public int profitBoost() {
        return switch (profitLevel) {
            case LOW -> 1;
            case HIGH -> 3;
            case MEDIUM -> 2;
        };
    }

    public int supplyChainBoost() {
        return supplyChainLevel.scoreBoost();
    }
}
