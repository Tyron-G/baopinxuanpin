package com.oneaix.selection.dto;

/** 供应链匹配推荐项 2026-06-05 */
public record SupplyMatchItem(
        String supplierName,
        String region,
        String productTitle,
        String unitPrice,
        String moq,
        String creditLevel,
        int matchScore,
        String matchReason
) {
}
