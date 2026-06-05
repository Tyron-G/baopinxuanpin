package com.oneaix.selection.dto;

/** 进入壁垒评估：评论门槛/CPC/专利/供应链（PRD 样例口径）2026-06-05 */
public record EntryBarrierAssessment(
        String newProductListingCycle,
        String topCommentThreshold,
        String cpcBarrier,
        String patentBarrier,
        String supplyChainBarrier,
        String overallLevel,
        String summary
) {
}
