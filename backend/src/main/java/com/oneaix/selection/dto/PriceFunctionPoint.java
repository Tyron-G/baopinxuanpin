package com.oneaix.selection.dto;

/** 竞争格局四象限散点：价格×功能 2026-06-05 */
public record PriceFunctionPoint(
        String label,
        double priceIndex,
        double functionIndex,
        String quadrant,
        String role
) {
}
