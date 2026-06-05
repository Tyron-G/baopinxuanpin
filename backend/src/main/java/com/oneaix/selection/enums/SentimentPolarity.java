package com.oneaix.selection.enums;

/** 舆情情感极性 2026-06-03 */
public enum SentimentPolarity {
    POSITIVE("positive"),
    NEGATIVE("negative");

    private final String code;

    SentimentPolarity(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
