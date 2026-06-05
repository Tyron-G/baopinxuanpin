package com.oneaix.selection.enums;

/** 报告推进动作状态 2026-06-04 */
public enum ActionStatus {
    IN_PROGRESS("进行", 1),
    PENDING("待", 2),
    COMPLETED("完成", 3),
    OTHER("", 4);

    private final String keyword;
    private final int rank;

    ActionStatus(String keyword, int rank) {
        this.keyword = keyword;
        this.rank = rank;
    }

    public int rank() {
        return rank;
    }

    public static ActionStatus fromLabel(String status) {
        if (status == null || status.isBlank()) {
            return OTHER;
        }
        for (ActionStatus value : values()) {
            if (!value.keyword.isBlank() && status.contains(value.keyword)) {
                return value;
            }
        }
        return OTHER;
    }

    public static boolean isCompleted(String status) {
        return fromLabel(status) == COMPLETED;
    }

    public static boolean isInProgress(String status) {
        return fromLabel(status) == IN_PROGRESS;
    }
}
