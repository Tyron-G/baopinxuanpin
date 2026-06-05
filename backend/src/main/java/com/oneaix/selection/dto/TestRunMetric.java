package com.oneaix.selection.dto;

/** 测款周度指标项（样例）2026-06-05 */
public record TestRunMetric(
        String key,
        String label,
        String actualValue,
        String benchmarkValue,
        String status,
        String hint
) {
}
