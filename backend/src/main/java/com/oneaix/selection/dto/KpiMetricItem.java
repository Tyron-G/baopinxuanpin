package com.oneaix.selection.dto;

/** 单条运营 KPI（样例）2026-06-04 */
public record KpiMetricItem(
        String key,
        String label,
        String actualValue,
        String targetValue,
        String trend,
        String status
) {
}
