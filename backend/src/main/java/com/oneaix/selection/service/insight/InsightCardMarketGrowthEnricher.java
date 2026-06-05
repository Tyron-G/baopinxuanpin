package com.oneaix.selection.service.insight;

import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.enums.PlatformView;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** 洞察卡片市场增速：与 TOP3 一致的 Jan vs Dec 12 月同比 2026-06-05 */
@Component
public class InsightCardMarketGrowthEnricher {

    private final TrendTwelveMonthGrowthCalculator twelveMonthGrowthCalculator;

    public InsightCardMarketGrowthEnricher(TrendTwelveMonthGrowthCalculator twelveMonthGrowthCalculator) {
        this.twelveMonthGrowthCalculator = twelveMonthGrowthCalculator;
    }

    public void apply(List<InsightCard> cards, List<CategoryTrend> trends, String platform) {
        if (cards == null || cards.isEmpty()) {
            return;
        }
        String platformLabel = normalizePlatform(platform);
        for (InsightCard card : cards) {
            enrichCard(card, trends, platformLabel);
        }
    }

    public void applyOne(InsightCard card, List<CategoryTrend> trends, String platform) {
        if (card == null) {
            return;
        }
        enrichCard(card, trends, normalizePlatform(platform));
    }

    private void enrichCard(InsightCard card, List<CategoryTrend> trends, String platformLabel) {
        twelveMonthGrowthCalculator.janToDecGrowth(trends, card.getCategoryName(), platformLabel)
                .ifPresent(growth -> card.setMarketGrowth(formatPercent(growth)));
    }

    private String normalizePlatform(String platform) {
        if (platform == null || platform.isBlank()) {
            return PlatformView.ALL.getLabel();
        }
        return PlatformView.normalize(platform).getLabel();
    }

    private String formatPercent(BigDecimal growth) {
        double value = growth.setScale(1, RoundingMode.HALF_UP).doubleValue();
        if (value > 0) {
            return "+" + growth.setScale(1, RoundingMode.HALF_UP).toPlainString() + "%";
        }
        return growth.setScale(1, RoundingMode.HALF_UP).toPlainString() + "%";
    }
}
