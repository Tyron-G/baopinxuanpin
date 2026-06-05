package com.oneaix.selection.dto;

/** 用户痛点优先级清单项 2026-06-04 */
public record PainPointItem(
        int rank,
        String topic,
        int crossCompetitorFrequency,
        String sentimentLevel,
        String summary
) {
}
