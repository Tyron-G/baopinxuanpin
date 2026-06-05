package com.oneaix.selection.dto;

/** 价格带分布项：SKU 数与销量占比 2026-06-05 */
public record PriceBandItem(
        String priceRange,
        int skuCount,
        double salesSharePercent,
        String gapHint
) {
}
