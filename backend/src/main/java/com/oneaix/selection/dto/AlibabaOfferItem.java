package com.oneaix.selection.dto;

/** 1688 报价样例条目 2026-06-04 */
public record AlibabaOfferItem(
        String offerId,
        String title,
        String unitPrice,
        String moq,
        String factoryName,
        String location,
        String creditLevel
) {
}
