package com.oneaix.selection.service.ranking;

import com.oneaix.selection.content.CategoryUniverseCatalog;
import com.oneaix.selection.content.RankingPadCatalog;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.OpportunityRankItem;
import com.oneaix.selection.dto.OpportunityRankingPage;
import com.oneaix.selection.dto.SellingPointSuggestion;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.service.opportunity.OpportunityIntelBuilder;
import com.oneaix.selection.util.CategoryNameMatcher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** 机会榜单 TOP50 编排（恒 50 条）2026-06-04 */
@Service
public class OpportunityRankingService {

    private static final int TARGET_SIZE = 50;

    private final OpportunityIntelBuilder opportunityIntelBuilder;

    public OpportunityRankingService(OpportunityIntelBuilder opportunityIntelBuilder) {
        this.opportunityIntelBuilder = opportunityIntelBuilder;
    }

    public OpportunityRankingPage top50(List<InsightCardView> cards, int page, int pageSize) {
        List<OpportunityRankItem> items = padToFifty(buildItems(cards));
        assignRanks(items);
        int total = items.size();
        int from = Math.max(0, (page - 1) * pageSize);
        if (from >= total) {
            return new OpportunityRankingPage(total, page, pageSize, List.of());
        }
        int to = Math.min(from + pageSize, total);
        return new OpportunityRankingPage(total, page, pageSize, items.subList(from, to));
    }

    private List<OpportunityRankItem> buildItems(List<InsightCardView> cards) {
        List<OpportunityRankItem> items = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (InsightCardView view : cards) {
            InsightCard card = view.card();
            CategoryUniverseCatalog.CategoryProfile profile = findProfile(card.getCategoryName());
            List<String> variants = profile != null
                    ? profile.productVariants()
                    : List.of(card.getCategoryName());

            for (String variant : variants) {
                String key = card.getCategoryName() + "|" + variant;
                if (!seen.add(key)) {
                    continue;
                }
                items.add(new OpportunityRankItem(
                        0,
                        card.getId(),
                        variant,
                        card.getCategoryName(),
                        view.scoreBreakdown().totalScore(),
                        summarizeReason(card.getRecommendation()),
                        view.decision(),
                        firstSellingPoint(card)
                ));
            }
        }

        return items.stream()
                .sorted(Comparator.comparingInt(OpportunityRankItem::opportunityScore).reversed())
                .toList();
    }

    private List<OpportunityRankItem> padToFifty(List<OpportunityRankItem> items) {
        if (items.size() >= TARGET_SIZE) {
            return new ArrayList<>(items.subList(0, TARGET_SIZE));
        }
        List<OpportunityRankItem> padded = new ArrayList<>(items);
        List<RankingPadCatalog.PadItem> pads = RankingPadCatalog.padItems();
        int padIndex = 0;
        while (padded.size() < TARGET_SIZE) {
            RankingPadCatalog.PadItem pad = pads.get(padIndex % pads.size());
            padded.add(new OpportunityRankItem(
                    0,
                    null,
                    pad.productTitle(),
                    pad.categoryName(),
                    pad.score(),
                    pad.reason(),
                    "建议观望",
                    new SellingPointSuggestion(
                            "99-149 元",
                            pad.productTitle() + " 场景差异化",
                            "先用内容验证再扩 SKU"
                    )
            ));
            padIndex++;
        }
        return padded;
    }

    private void assignRanks(List<OpportunityRankItem> items) {
        for (int i = 0; i < items.size(); i++) {
            OpportunityRankItem item = items.get(i);
            items.set(i, new OpportunityRankItem(
                    i + 1,
                    item.cardId(),
                    item.productTitle(),
                    item.categoryName(),
                    item.opportunityScore(),
                    item.recommendationReason(),
                    item.decision(),
                    item.sellingPoint()
            ));
        }
    }

    private CategoryUniverseCatalog.CategoryProfile findProfile(String categoryName) {
        return CategoryUniverseCatalog.profiles().stream()
                .filter(profile -> CategoryNameMatcher.matches(profile.categoryName(), categoryName))
                .findFirst()
                .orElse(null);
    }

    private SellingPointSuggestion firstSellingPoint(InsightCard card) {
        List<SellingPointSuggestion> suggestions = opportunityIntelBuilder.buildSellingPoints(card);
        return suggestions.isEmpty()
                ? new SellingPointSuggestion("99-149 元", "场景化卖点", "先做小样验证")
                : suggestions.get(0);
    }

    private String summarizeReason(String recommendation) {
        if (recommendation == null || recommendation.isBlank()) {
            return "内置样例：待补充推荐原因";
        }
        return recommendation.length() > 80 ? recommendation.substring(0, 80) + "…" : recommendation;
    }
}
