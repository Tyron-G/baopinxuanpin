package com.oneaix.selection.dto;

/** PRD 供需缺口模型：需求热度 ÷ 供给充分度 × 用户满意度缺口 2026-06-05 */
public record SupplyDemandGapModel(
        double demandHeat,
        double supplyAdequacy,
        double satisfactionGap,
        double gapIndex,
        String priceVacuumBand,
        String summary
) {
}
