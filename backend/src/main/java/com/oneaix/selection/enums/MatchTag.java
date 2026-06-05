package com.oneaix.selection.enums;

/** 洞察卡片匹配标签 2026-06-03 */
public enum MatchTag {
    TARGET_CATEGORY("目标品类"),
    BUDGET_MATCH("预算匹配"),
    BUDGET_RISK("超预算风险"),
    RECOMMEND("建议立项"),
    WATCH("建议观望"),
    ABANDON("建议放弃");

    private final String label;

    MatchTag(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static MatchTag fromDecision(DecisionType decision) {
        return switch (decision) {
            case RECOMMEND -> RECOMMEND;
            case WATCH -> WATCH;
            case ABANDON -> ABANDON;
        };
    }
}
