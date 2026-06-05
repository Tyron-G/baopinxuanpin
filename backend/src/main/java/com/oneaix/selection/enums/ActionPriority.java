package com.oneaix.selection.enums;

/** 报告动作优先级 2026-06-04 */
public enum ActionPriority {
    P0("P0", 0),
    P1("P1", 1),
    P2("P2", 2),
    OTHER("", 3);

    private final String label;
    private final int rank;

    ActionPriority(String label, int rank) {
        this.label = label;
        this.rank = rank;
    }

    public static int rankOf(String priority) {
        if (priority == null || priority.isBlank()) {
            return OTHER.rank;
        }
        for (ActionPriority value : values()) {
            if (value.label.equals(priority)) {
                return value.rank;
            }
        }
        return OTHER.rank;
    }
}
