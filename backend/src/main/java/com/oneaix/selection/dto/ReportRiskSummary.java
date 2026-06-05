package com.oneaix.selection.dto;

public record ReportRiskSummary(
        int totalCount,
        String highestLevel,
        String primaryRiskTitle,
        String primaryRiskDescription,
        String suggestedAttention,
        String summary
) {
}
