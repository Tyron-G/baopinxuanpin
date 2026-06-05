package com.oneaix.selection.dto;

import java.util.List;

/** 2026-06-03 工作流进度 */
public record WorkflowProgress(
        Long brandId,
        String brandName,
        String currentStage,
        String recommendedNextStage,
        List<WorkflowStage> stages,
        int signalCount,
        int insightCount,
        int opportunityCount,
        boolean reportReady
) {
}
