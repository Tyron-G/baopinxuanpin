package com.oneaix.selection.dto;

import java.util.List;

/** 选品归因报告（迭代2）2026-06-04 */
public record SelectionAttributionReport(
        String brandName,
        String summary,
        List<AttributionInsight> successFactors,
        List<AttributionInsight> failureFactors,
        List<String> nextQuarterOpportunities
) {
}
