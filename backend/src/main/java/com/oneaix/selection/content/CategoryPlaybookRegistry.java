package com.oneaix.selection.content;

import com.oneaix.selection.entity.InsightCard;
import org.springframework.stereotype.Component;

import java.util.Map;

/** 类目内容剧本注册表 2026-06-04 */
@Component
public class CategoryPlaybookRegistry {
    private final CategoryPlaybook fallback;
    private final Map<Long, CategoryPlaybook> byCardId;

    public CategoryPlaybookRegistry() {
        this.fallback = CategoryPlaybook.cleaningRobot();
        this.byCardId = Map.of(
                1L, CategoryPlaybook.petSmart(),
                2L, CategoryPlaybook.portableCoffee(),
                3L, fallback
        );
    }

    public CategoryPlaybook resolve(Long cardId) {
        if (cardId == null) {
            return fallback;
        }
        return byCardId.getOrDefault(cardId, fallback);
    }

    public CategoryPlaybook resolve(InsightCard card) {
        return card == null ? fallback : resolve(card.getId());
    }
}

