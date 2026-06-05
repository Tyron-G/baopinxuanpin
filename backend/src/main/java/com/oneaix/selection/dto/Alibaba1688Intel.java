package com.oneaix.selection.dto;

import java.util.List;

/** 1688 供应链摸底（开放平台样例）2026-06-04 */
public record Alibaba1688Intel(
        String priceRange,
        String moq,
        String factoryCapacity,
        String summary,
        List<String> sampleSuppliers,
        String dataProvider,
        String syncedAt,
        String categoryKeyword,
        List<AlibabaOfferItem> offers
) {
}
