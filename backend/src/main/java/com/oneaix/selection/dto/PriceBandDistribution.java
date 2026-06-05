package com.oneaix.selection.dto;

import java.util.List;

/** 类目价格带分布（各价格段 SKU 数与销量占比）2026-06-05 */
public record PriceBandDistribution(
        String platform,
        List<PriceBandItem> bands,
        String bestVacuumBand,
        String summary
) {
}
