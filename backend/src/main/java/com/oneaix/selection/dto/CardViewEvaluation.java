package com.oneaix.selection.dto;

/** 单卡评估快照：一次 assess + 评分拆解 2026-06-04 */
public record CardViewEvaluation(
        BrandFitAssessment assessment,
        ScoreBreakdown scoreBreakdown
) {
}
