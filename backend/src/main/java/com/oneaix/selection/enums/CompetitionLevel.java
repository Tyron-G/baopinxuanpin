package com.oneaix.selection.enums;

/** 竞争难度等级 2026-06-03 */
public enum CompetitionLevel {
    LOW("低", 18),
    LOW_TO_MEDIUM("低到中", 17),
    MEDIUM("中", 13),
    HIGH("高", 8);

    private final String keyword;
    private final int scoreWeight;

    CompetitionLevel(String keyword, int scoreWeight) {
        this.keyword = keyword;
        this.scoreWeight = scoreWeight;
    }

    public String getKeyword() {
        return keyword;
    }

    public int scoreWeight() {
        return scoreWeight;
    }

    public static CompetitionLevel fromLabel(String label) {
        if (label == null || label.isBlank()) {
            return MEDIUM;
        }
        if (label.contains("低到中")) {
            return LOW_TO_MEDIUM;
        }
        if (label.contains("低")) {
            return LOW;
        }
        if (label.contains("高")) {
            return HIGH;
        }
        if (label.contains("中")) {
            return MEDIUM;
        }
        return MEDIUM;
    }

    public boolean isHigh() {
        return this == HIGH;
    }

    public boolean isMedium() {
        return this == MEDIUM;
    }
}
