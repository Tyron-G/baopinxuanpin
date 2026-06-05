package com.oneaix.selection.dto;

/** 2026-06-03 统一评分拆解 */
public record ScoreBreakdown(
        int trendScore,
        int competitionScore,
        int supplyGapScore,
        int brandFitScore,
        int riskPenalty,
        int totalScore,
        int confidence
) {
}
