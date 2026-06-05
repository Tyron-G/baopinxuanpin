package com.oneaix.selection.enums;

/** 爆品信号类型 2026-06-03 */
public enum SignalType {
    SEARCH_SURGE("搜索飙升"),
    SOCIAL_ANOMALY("社媒异常"),
    CONTENT_SEEDING("内容种草"),
    PAIN_POINT("差评痛点"),
    RANK_CHANGE("榜单异动"),
    OPPORTUNITY_SCORE("机会评分");

    private final String label;

    SignalType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
