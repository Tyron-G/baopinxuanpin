package com.oneaix.selection.dto;

/** 产品核心承诺量化（应用内推算）2026-06-05 */
public record CorePromiseMetrics(
        int earlySignalLeadDays,
        int recommendHitRatePercent,
        int decisionSuccessProxyPercent,
        String narrative
) {
}
