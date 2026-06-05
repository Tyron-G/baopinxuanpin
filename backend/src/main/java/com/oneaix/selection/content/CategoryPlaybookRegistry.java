package com.oneaix.selection.content;

import com.oneaix.selection.entity.InsightCard;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/** 类目内容剧本注册表（按类目名 + cardId 解析）2026-06-05 */
@Component
public class CategoryPlaybookRegistry {
    private final CategoryPlaybook fallback;
    private final Map<Long, CategoryPlaybook> byCardId;
    private final Map<String, CategoryPlaybook> byCategoryName;

    public CategoryPlaybookRegistry() {
        this.fallback = CategoryPlaybook.cleaningRobot();
        this.byCardId = Map.of(
                1L, CategoryPlaybook.petSmart(),
                2L, CategoryPlaybook.portableCoffee(),
                3L, fallback
        );
        this.byCategoryName = new HashMap<>();
        byCategoryName.put("宠物智能用品", CategoryPlaybook.petSmart());
        byCategoryName.put("便携式咖啡器具", CategoryPlaybook.portableCoffee());
        byCategoryName.put("家用清洁机器人", CategoryPlaybook.cleaningRobot());
        for (CategoryUniverseCatalog.CategoryProfile profile : CategoryUniverseCatalog.profiles()) {
            byCategoryName.putIfAbsent(profile.categoryName(), CategoryPlaybook.fromProfile(profile));
        }
    }

    public CategoryPlaybook resolve(Long cardId) {
        if (cardId == null) {
            return fallback;
        }
        return byCardId.getOrDefault(cardId, fallback);
    }

    public CategoryPlaybook resolve(InsightCard card) {
        if (card == null) {
            return fallback;
        }
        CategoryPlaybook named = byCategoryName.get(card.getCategoryName());
        if (named != null) {
            return named;
        }
        return byCardId.getOrDefault(card.getId(), CategoryPlaybook.fromCard(card));
    }
}
