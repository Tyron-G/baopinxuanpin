package com.oneaix.selection.dto;

/** 团队协作任务分派 2026-06-05 */
public record TeamAssignmentItem(
        Long id,
        Long cardId,
        String actionTitle,
        String assigneeName,
        String status,
        String approvalStatus,
        String approverName,
        String note
) {
}
