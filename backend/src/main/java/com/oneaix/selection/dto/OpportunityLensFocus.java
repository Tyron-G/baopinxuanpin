package com.oneaix.selection.dto;

public record OpportunityLensFocus(
        String lensKey,
        String lensLabel,
        String summary,
        String targetCrowd,
        String scenarioText,
        String differentiation,
        int opportunityScore,
        String opportunityLevel,
        String entryTiming,
        String lifecycleStage,
        String reason
) {
}
