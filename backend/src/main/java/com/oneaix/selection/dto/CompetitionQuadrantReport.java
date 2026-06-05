package com.oneaix.selection.dto;

import java.util.List;

/** 价格×功能四象限报告 2026-06-05 */
public record CompetitionQuadrantReport(
        List<PriceFunctionPoint> points,
        String blankZone,
        String summary
) {
}
