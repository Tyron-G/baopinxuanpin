package com.oneaix.selection.enums;

/** 信号强度 2026-06-03 */
public enum SignalStrength {
    STRONG("强", 85),
    MEDIUM("中", 70),
    WEAK("弱", 0);

    private final String label;
    private final int minScore;

    SignalStrength(String label, int minScore) {
        this.label = label;
        this.minScore = minScore;
    }

    public String getLabel() {
        return label;
    }

    public static SignalStrength fromScore(int score) {
        if (score >= STRONG.minScore) {
            return STRONG;
        }
        if (score >= MEDIUM.minScore) {
            return MEDIUM;
        }
        return WEAK;
    }
}
