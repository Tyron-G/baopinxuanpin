package com.oneaix.selection.enums;

/** 品牌适配等级 2026-06-03 */
public enum FitLevel {
    HIGH("高匹配", 16),
    MEDIUM("中匹配", 12),
    LOW("低匹配", 0);

    private final String label;
    private final int minScore;

    FitLevel(String label, int minScore) {
        this.label = label;
        this.minScore = minScore;
    }

    public String getLabel() {
        return label;
    }

    public static FitLevel fromScore(int score) {
        if (score >= HIGH.minScore) {
            return HIGH;
        }
        if (score >= MEDIUM.minScore) {
            return MEDIUM;
        }
        return LOW;
    }

    /** 品牌适配子维度加分权重 2026-06-04 */
    public int scoreBoost() {
        return switch (this) {
            case HIGH -> 4;
            case MEDIUM -> 3;
            case LOW -> 2;
        };
    }
}
