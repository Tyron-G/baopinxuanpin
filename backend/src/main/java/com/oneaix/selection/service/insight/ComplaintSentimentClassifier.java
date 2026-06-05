package com.oneaix.selection.service.insight;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/** 差评主题情感极性（关键词规则，非外部 NLP）2026-06-05 */
@Component
public class ComplaintSentimentClassifier {

    private static final List<String> HIGH_NEGATIVE = List.of(
            "卡粮", "漏电", "起火", "过敏", "断裂", "失灵", "无法", "损坏", "投诉"
    );
    private static final List<String> MEDIUM_NEGATIVE = List.of(
            "噪音", "漏", "慢", "不准", "不稳", "异味", "磨损", "售后", "退货"
    );

    /**
     * @return 高 / 中 / 低（负向情绪强度，供 PRD 痛点清单展示）
     */
    public String polarity(String topic, int crossCompetitorFrequency) {
        if (topic == null || topic.isBlank()) {
            return "低";
        }
        String normalized = topic.toLowerCase(Locale.ROOT);
        for (String keyword : HIGH_NEGATIVE) {
            if (normalized.contains(keyword)) {
                return "高";
            }
        }
        for (String keyword : MEDIUM_NEGATIVE) {
            if (normalized.contains(keyword)) {
                return crossCompetitorFrequency >= 2 ? "高" : "中";
            }
        }
        if (crossCompetitorFrequency >= 3) {
            return "高";
        }
        if (crossCompetitorFrequency >= 2) {
            return "中";
        }
        return "低";
    }
}
