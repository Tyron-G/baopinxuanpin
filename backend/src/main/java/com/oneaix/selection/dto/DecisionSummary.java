package com.oneaix.selection.dto;

import java.util.List;

/** 2026-06-03 决策摘要 */
public record DecisionSummary(
        String decision,
        int confidence,
        String headline,
        List<ReasonItem> reasons,
        List<RiskItem> risks,
        ScoreBreakdown scoreBreakdown
) {
}
