package com.oneaix.selection.service.insight;

import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.service.CompetitorService;
import com.oneaix.selection.util.CategoryNameMatcher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 竞品差评主题聚合（跨店铺频次统计）2026-06-05 */
@Component
public class CompetitorComplaintAggregator {

    private final CompetitorService competitorService;

    public CompetitorComplaintAggregator(CompetitorService competitorService) {
        this.competitorService = competitorService;
    }

    public List<ComplaintTopicStat> aggregate(Long brandId, List<InsightCardView> rankedCards) {
        if (brandId == null || rankedCards == null || rankedCards.isEmpty()) {
            return List.of();
        }
        Map<String, ComplaintTopicStat> stats = new LinkedHashMap<>();
        for (CompetitorShop shop : competitorService.list(brandId)) {
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
                    List<String> shops = new ArrayList<>(existing.shopNames());
                    if (!shops.contains(shop.shopName())) {
                        shops.add(shop.shopName());
                    }
                    return new ComplaintTopicStat(category, topic, existing.frequency() + 1, shops);
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
