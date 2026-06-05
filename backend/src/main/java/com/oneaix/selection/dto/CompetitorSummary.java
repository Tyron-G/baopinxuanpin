package com.oneaix.selection.dto;

import java.util.List;

/** 2026-06-04 竞品对比摘要 */
public record CompetitorSummary(
        int trackedShopCount,
        String coveredPlatforms,
        int totalHitProductCount,
        List<String> commonComplaintTopics,
        String strongestSignal,
        String summary
) {
}
