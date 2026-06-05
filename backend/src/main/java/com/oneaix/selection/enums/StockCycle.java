package com.oneaix.selection.enums;

/** 备货周期 2026-06-03 */
public enum StockCycle {
    UNDER_30("＜30天"),
    RANGE_30_60("30-60天"),
    ABOVE_60("60天以上");

    private final String label;

    StockCycle(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static StockCycle fromLabel(String label) {
        if (label == null || label.isBlank()) {
            return null;
        }
        for (StockCycle cycle : values()) {
            if (cycle.label.equals(label) || label.contains(cycle.label.replace("天", ""))) {
                return cycle;
            }
        }
        if (label.contains("60")) {
            return ABOVE_60;
        }
        if (label.contains("30")) {
            return UNDER_30;
        }
        return null;
    }

    public boolean isLongCycle() {
        return this == ABOVE_60;
    }

    public boolean isShortCycle() {
        return this == UNDER_30;
    }
}
