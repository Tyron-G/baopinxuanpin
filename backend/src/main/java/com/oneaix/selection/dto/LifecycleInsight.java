package com.oneaix.selection.dto;

/** 生命周期与搜索增速二阶导判断 2026-06-05 */
public record LifecycleInsight(
        String lifecycleStage,
        boolean growthAccelerating,
        String secondDerivativeLabel,
        double latestGrowthRate,
        double growthAcceleration,
        String summary
) {
}
