package com.oneaix.selection.dto;

import java.util.List;

/** 测款优化诊断（背景场景：上架一周数据）2026-06-05 */
public record TestRunDiagnosis(
        Long brandId,
        Long cardId,
        String productTitle,
        String categoryName,
        String platform,
        String weekLabel,
        String verdict,
        int confidence,
        String summary,
        List<TestRunMetric> metrics,
        List<String> scaleUpActions,
        List<String> stopSignals,
        boolean demoData
) {
}
