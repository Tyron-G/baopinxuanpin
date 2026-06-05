package com.oneaix.selection.service.insight;

import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.entity.CompetitionData;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.repository.market.MarketDataRepository;
import com.oneaix.selection.util.PlatformMarketFilter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 为洞察卡片附加同质化评分（竞争数据）2026-06-05 */
@Component
public class InsightCardHomogeneityEnricher {

    private final MarketDataRepository marketDataRepository;

    public InsightCardHomogeneityEnricher(MarketDataRepository marketDataRepository) {
        this.marketDataRepository = marketDataRepository;
    }

    public List<InsightCardView> enrich(List<InsightCardView> views, Set<String> visibleCategories, String platform) {
        if (views == null || views.isEmpty()) {
            return views;
        }
        String platformLabel = platform == null || platform.isBlank()
                ? PlatformView.ALL.getLabel()
                : PlatformView.normalize(platform).getLabel();
        List<CompetitionData> competition = PlatformMarketFilter.byPlatform(
                marketDataRepository.findCompetitionByCategories(visibleCategories, platformLabel),
                platformLabel,
                CompetitionData::getPlatform
        );
        Map<String, BigDecimal> homogeneityByCategory = new LinkedHashMap<>();
        for (CompetitionData row : competition) {
            homogeneityByCategory.putIfAbsent(row.getCategoryName(), row.getHomogeneityScore());
        }
        return views.stream()
                .map(view -> withHomogeneity(view, homogeneityByCategory.get(view.card().getCategoryName())))
                .toList();
    }

    private InsightCardView withHomogeneity(InsightCardView view, BigDecimal score) {
        if (score == null) {
            return view;
        }
        return new InsightCardView(
                view.card(),
                view.pinned(),
                view.budgetCompatible(),
                view.matchTags(),
                view.decision(),
                view.scoreBreakdown(),
                view.reasons(),
                view.risks(),
                view.brandFitDetail(),
                view.mismatches(),
                score
        );
    }
}
