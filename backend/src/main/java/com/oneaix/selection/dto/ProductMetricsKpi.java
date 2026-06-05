package com.oneaix.selection.dto;

import java.util.List;

/** 产品背景文档运营 KPI 看板（样例）2026-06-04 */
public record ProductMetricsKpi(
        String phaseLabel,
        String asOfDate,
        boolean demoData,
        String summary,
        List<KpiMetricItem> metrics
) {
}
