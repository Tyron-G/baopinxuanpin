package com.oneaix.selection.dto;

/** 品牌专属选品模型配置（迭代2）2026-06-04 */
public record BrandSelectionModelProfile(
        Long brandId,
        String brandName,
        double trendWeight,
        double competitionWeight,
        double supplyGapWeight,
        double brandFitWeight,
        double riskPenaltyWeight,
        String modelVersion,
        String trainingSummary,
        String expectedAccuracyGain,
        java.util.List<String> trainingPipelineSteps
) {
}
