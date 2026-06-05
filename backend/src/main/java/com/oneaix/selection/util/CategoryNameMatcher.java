package com.oneaix.selection.util;

import com.oneaix.selection.dto.InsightCardView;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** 品类名称模糊匹配（排除/关联/竞品聚焦共用）2026-06-04 */
public final class CategoryNameMatcher {

    private CategoryNameMatcher() {
    }

    public static boolean matches(String left, String right) {
        if (isBlank(left) || isBlank(right)) {
            return false;
        }
        String a = normalize(left);
        String b = normalize(right);
        return a.equals(b) || a.contains(b) || b.contains(a);
    }

    public static Optional<Long> resolveCardId(String focusCategory, List<InsightCardView> cards, Long preferredCardId) {
        if (preferredCardId != null && preferredCardId > 0) {
            return Optional.of(preferredCardId);
        }
        return resolveCardView(null, focusCategory, cards).map(view -> view.card().getId());
    }

    public static Optional<InsightCardView> resolveCardView(Long cardId, String focusCategory, List<InsightCardView> cards) {
        if (cards == null || cards.isEmpty()) {
            return Optional.empty();
        }
        if (cardId != null && cardId > 0) {
            for (InsightCardView view : cards) {
                if (cardId.equals(view.card().getId())) {
                    return Optional.of(view);
                }
            }
        }
        if (isBlank(focusCategory)) {
            return Optional.empty();
        }
        for (InsightCardView view : cards) {
            if (matches(view.card().getCategoryName(), focusCategory)) {
                return Optional.of(view);
            }
        }
        return Optional.empty();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
