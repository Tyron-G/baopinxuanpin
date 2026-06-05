package com.oneaix.selection.enums;

/** 洞察卡片决策类型 2026-06-04 */
public enum DecisionType {
    RECOMMEND("推荐立项"),
    WATCH("建议观望"),
    ABANDON("建议放弃");

    private final String label;

    DecisionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getHeadline() {
        return switch (this) {
            case RECOMMEND -> "建议优先立项验证";
            case WATCH -> "建议保持观望，先做小样验证";
            case ABANDON -> "建议暂缓投入，优先排除";
        };
    }

    public static DecisionType fromRecommendation(String recommendation) {
        if (recommendation == null || recommendation.isBlank()) {
            return WATCH;
        }
        if (recommendation.contains("推荐")) {
            return RECOMMEND;
        }
        if (recommendation.contains("放弃")) {
            return ABANDON;
        }
        if (recommendation.contains("观望")) {
            return WATCH;
        }
        return WATCH;
    }

    public boolean matchesRecommendation(String recommendation) {
        return fromRecommendation(recommendation) == this;
    }

    public int riskPenaltyWeight() {
        return switch (this) {
            case RECOMMEND -> 0;
            case WATCH -> 1;
            case ABANDON -> 3;
        };
    }

    public int confidenceBase() {
        return switch (this) {
            case RECOMMEND -> 82;
            case WATCH -> 68;
            case ABANDON -> 58;
        };
    }
}
