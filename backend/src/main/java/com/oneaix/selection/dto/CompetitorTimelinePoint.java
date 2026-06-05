package com.oneaix.selection.dto;

/** 2026-06-04 竞品表现时间轴节点 */
public record CompetitorTimelinePoint(
        String period,
        int heatIndex,
        int salesIndex,
        String note
) {
}
