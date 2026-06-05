package com.oneaix.selection.service.insight;

import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.service.CompetitorService;
import com.oneaix.selection.service.competitor.BuiltinCompetitorCatalog;
import com.oneaix.selection.util.CategoryNameMatcher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 竞品差评主题聚合（跟踪店 + 内置种子，Playbook 不再参与）2026-06-05 */
@Component
public class CompetitorComplaintAggregator {

    private final CompetitorService competitorService;

    public CompetitorComplaintAggregator(CompetitorService competitorService) {
        this.competitorService = competitorService;
    }

    public List<ComplaintTopicStat> aggregate(Long brandId, List<InsightCardView> rankedCards) {
        if (rankedCards == null || rankedCards.isEmpty()) {
            return List.of();
        }
        Set<String> visibleCategories = new LinkedHashSet<>();
        for (InsightCardView view : rankedCards) {
            visibleCategories.add(view.card().getCategoryName());
        }
        List<CompetitorShop> tracked = competitorService.list(brandId);
        Set<String> trackedCategories = new LinkedHashSet<>();
        for (CompetitorShop shop : tracked) {
            if (shop.focusCategory() != null && !shop.focusCategory().isBlank()) {
                trackedCategories.add(shop.focusCategory());
            }
        }
        List<CompetitorShop> shops = new ArrayList<>(tracked);
        for (CompetitorShop shop : BuiltinCompetitorCatalog.shops()) {
            if (!visibleCategories.stream().anyMatch(category -> CategoryNameMatcher.matches(category, shop.focusCategory()))) {
                continue;
            }
            boolean categoryHasTracked = trackedCategories.stream()
                    .anyMatch(trackedCategory -> CategoryNameMatcher.matches(trackedCategory, shop.focusCategory()));
            if (!categoryHasTracked) {
                shops.add(shop);
            }
        }

        Map<String, ComplaintTopicStat> stats = new LinkedHashMap<>();
        for (CompetitorShop shop : shops) {
            String category = shop.focusCategory();
            if (category == null || category.isBlank()) {
                continue;
            }
            boolean matched = rankedCards.stream().anyMatch(view ->
                    CategoryNameMatcher.matches(category, view.card().getCategoryName())
                    || (shop.cardId() != null && shop.cardId().equals(view.card().getId())));
            if (!matched) {
                continue;
            }
            for (String topic : shop.complaintTopics()) {
                if (topic == null || topic.isBlank()) {
                    continue;
                }
                String key = category + "::" + topic;
                stats.compute(key, (k, existing) -> {
                    if (existing == null) {
                        return new ComplaintTopicStat(category, topic, 1, List.of(shop.shopName()));
                    }
                    List<String> shopNames = new ArrayList<>(existing.shopNames());
                    if (!shopNames.contains(shop.shopName())) {
                        shopNames.add(shop.shopName());
                    }
                    return new ComplaintTopicStat(category, topic, existing.frequency() + 1, shopNames);
                });
            }
        }
        return stats.values().stream()
                .sorted(Comparator.comparingInt(ComplaintTopicStat::frequency).reversed())
                .toList();
    }

    public record ComplaintTopicStat(
            String categoryName,
            String topic,
            int frequency,
            List<String> shopNames
    ) {
    }
}
