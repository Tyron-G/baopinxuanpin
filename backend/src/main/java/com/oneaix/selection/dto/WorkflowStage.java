package com.oneaix.selection.dto;

/** 2026-06-03 工作流阶段 */
public record WorkflowStage(
        String key,
        String title,
        String status,
        String summary,
        String nextAction
) {
}
