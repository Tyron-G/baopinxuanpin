package com.oneaix.selection.dto;

import java.util.List;

/** 2026-06-04 机会页与报告共享的策略叙述层 */
public record OpportunityNarrative(
        CompetitorSummary competitorSummary,
        PlatformPlaybook platformPlaybook,
        List<CompetitorShop> competitorComparison,
        List<String> differentiationAdvice,
        List<CompetitorFocusReason> competitorFocusReasons,
        List<OpportunityLensFocus> opportunityLensFocuses
) {
}
