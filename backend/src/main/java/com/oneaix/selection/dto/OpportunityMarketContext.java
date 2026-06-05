package com.oneaix.selection.dto;

/** 机会页市场与平台上下文（CPC/物流/平台政策，内置样例）2026-06-04 */
public record OpportunityMarketContext(
        String cpcLevel,
        String cpcVsCategory,
        String logisticsCostHint,
        String weightVolumeRatio,
        String platformPolicySignal,
        String trafficBonusChannel,
        String summary
) {
}
