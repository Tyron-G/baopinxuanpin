package com.oneaix.selection.dto;

public record ReportActionSummary(
        int totalCount,
        int completedCount,
        int inProgressCount,
        int pendingCount,
        String focusActionTitle,
        String focusActionStatus,
        String latestUpdatedAt,
        String summary
) {
}
