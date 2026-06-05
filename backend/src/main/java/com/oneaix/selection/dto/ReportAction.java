package com.oneaix.selection.dto;

/** 2026-06-03 报告下一步动作 */
public record ReportAction(
        String title,
        String ownerRole,
        String expectedGoal,
        String priority,
        String eta,
        String status,
        String updatedAt,
        String note
) {
}
