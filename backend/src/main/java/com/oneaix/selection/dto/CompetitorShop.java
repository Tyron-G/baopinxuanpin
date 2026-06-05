package com.oneaix.selection.dto;

/** 2026-06-03 竞品监控店铺 */
public record CompetitorShop(
        String shopName,
        String platform,
        String focusCategory,
        String latestHit,
        String growthSignal,
        String addedAt,
        Long cardId,
        String sourceSignalId,
        String sourceSignalType,
        String recentLaunch,
        int hitProductCount,
        java.util.List<String> complaintTopics,
        java.util.List<String> opportunityTags
) {
}
