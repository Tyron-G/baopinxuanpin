package com.oneaix.selection.dto;

import java.util.List;

/** 2026-06-04 竞品表现时间轴 */
public record CompetitorTimeline(
        String shopName,
        String platform,
        String focusCategory,
        String trendLabel,
        String summary,
        List<CompetitorTimelinePoint> points
) {
}
