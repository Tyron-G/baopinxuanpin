package com.oneaix.selection.enums;

/** 期望利润下限 2026-06-03 */
public enum ProfitMin {
    UNDER_15("＜15%"),
    RANGE_15_25("15-25%"),
    ABOVE_25("＞25%");

    private final String label;

    ProfitMin(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ProfitMin fromLabel(String label) {
        if (label == null || label.isBlank()) {
            return null;
        }
        for (ProfitMin profit : values()) {
            if (profit.label.equals(label)) {
                return profit;
            }
        }
        return null;
    }

    public boolean requiresRecommendDecision() {
        return this == ABOVE_25;
    }

    public boolean toleratesWatchDecision() {
        return this == RANGE_15_25;
    }
}
