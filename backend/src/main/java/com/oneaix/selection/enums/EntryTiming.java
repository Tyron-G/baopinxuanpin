package com.oneaix.selection.enums;

/** 入场时机标签 2026-06-04 */
public enum EntryTiming {
    BEST("最佳", 40),
    PRIORITY("优先", 30),
    FEASIBLE("可", 20),
    VALIDATE("验证", 15),
    OTHER("", 10);

    private final String keyword;
    private final int priority;

    EntryTiming(String keyword, int priority) {
        this.keyword = keyword;
        this.priority = priority;
    }

    public int priority() {
        return priority;
    }

    public static int priorityOf(String value) {
        if (value == null || value.isBlank()) {
            return OTHER.priority;
        }
        for (EntryTiming timing : values()) {
            if (!timing.keyword.isBlank() && value.contains(timing.keyword)) {
                return timing.priority;
            }
        }
        return OTHER.priority;
    }
}
