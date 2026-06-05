package com.oneaix.selection.enums;

/** 资金预算带 2026-06-03 */
public enum BudgetRange {
    UNDER_5("＜5万", 5),
    RANGE_5_20("5-20万", 20),
    RANGE_20_50("20-50万", 50),
    ABOVE_50("50万以上", Integer.MAX_VALUE);

    private final String label;
    private final int maxWan;

    BudgetRange(String label, int maxWan) {
        this.label = label;
        this.maxWan = maxWan;
    }

    public String getLabel() {
        return label;
    }

    public int maxWan() {
        return maxWan;
    }

    public static BudgetRange fromLabel(String label) {
        if (label == null || label.isBlank()) {
            return null;
        }
        if ("<5万".equals(label)) {
            return UNDER_5;
        }
        for (BudgetRange range : values()) {
            if (range.label.equals(label)) {
                return range;
            }
        }
        return null;
    }

    public static int maxWanFromLabel(String label) {
        BudgetRange range = fromLabel(label);
        return range == null ? -1 : range.maxWan();
    }
}
