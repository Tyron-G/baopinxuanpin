package com.oneaix.selection.dto;

/** 机会页类目竞争指标条：在售 SKU / 同质化等 2026-06-05 */
public record CategoryMarketMetrics(
        String platform,
        int totalSkuCount,
        double top10SalesRatio,
        double homogeneityScore,
        long totalSearchVolume,
        String summary
) {
}
