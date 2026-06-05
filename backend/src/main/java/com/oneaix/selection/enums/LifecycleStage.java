package com.oneaix.selection.enums;

/** 机会点生命周期阶段 2026-06-04 */
public enum LifecycleStage {
    GROWTH("成长期", 4),
    INTRO("导入期", 3),
    MATURE("成熟期", 2),
    OTHER("", 1);

    private final String keyword;
    private final int priority;

    LifecycleStage(String keyword, int priority) {
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
        for (LifecycleStage stage : values()) {
            if (!stage.keyword.isBlank() && value.contains(stage.keyword)) {
                return stage.priority;
            }
        }
        return OTHER.priority;
    }
}
