package com.oneaix.selection.enums;

/** 风险等级 2026-06-03 */
public enum RiskLevel {
    HIGH("high"),
    MEDIUM("medium"),
    LOW("low");

    private final String code;

    RiskLevel(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static RiskLevel fromCode(String code) {
        if (code == null) {
            return LOW;
        }
        for (RiskLevel level : values()) {
            if (level.code.equalsIgnoreCase(code)) {
                return level;
            }
        }
        return LOW;
    }

    public int sortRank() {
        return switch (this) {
            case HIGH -> 0;
            case MEDIUM -> 1;
            case LOW -> 2;
        };
    }

    public String suggestedAttention() {
        return switch (this) {
            case HIGH -> "优先解决预算、竞争壁垒或关键履约风险，再扩大验证投入。";
            case MEDIUM -> "保持小样验证节奏，同时补齐场景、素材或供应链稳定性验证。";
            case LOW -> "持续跟踪 CPC、转化率和售后反馈，防止低风险项在放量阶段放大。";
        };
    }
}
