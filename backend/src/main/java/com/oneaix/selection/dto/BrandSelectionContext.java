package com.oneaix.selection.dto;

import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.entity.InsightCard;

import com.oneaix.selection.util.CategoryNameMatcher;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/** 单次请求内品牌选品上下文快照 2026-06-04 */
public record BrandSelectionContext(
        BrandInfo brand,
        List<InsightCard> catalog,
        Set<String> visibleCategoryNames,
        List<InsightCardView> cards
) {
    public Optional<InsightCardView> findCard(Long cardId) {
        if (cardId == null) {
            return Optional.empty();
        }
        return cards.stream()
                .filter(view -> cardId.equals(view.card().getId()))
                .findFirst();
    }

    public Optional<InsightCardView> findByCategory(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            return Optional.empty();
        }
        return cards.stream()
                .filter(view -> CategoryNameMatcher.matches(view.card().getCategoryName(), categoryName))
                .findFirst();
    }
}
