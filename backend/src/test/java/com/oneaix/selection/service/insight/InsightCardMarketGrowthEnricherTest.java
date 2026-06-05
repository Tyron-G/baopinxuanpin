package com.oneaix.selection.service.insight;

import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.enums.PlatformView;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 2026-06-05 InsightCardMarketGrowthEnricher */
class InsightCardMarketGrowthEnricherTest {

    private final InsightCardMarketGrowthEnricher enricher =
            new InsightCardMarketGrowthEnricher(new TrendTwelveMonthGrowthCalculator());

    @Test
    void shouldOverwriteMarketGrowthWithJanDecRate() {
        InsightCard card = new InsightCard();
        card.setCategoryName("宠物智能用品");
        card.setMarketGrowth("+99.9%");
        List<CategoryTrend> trends = List.of(
                volume("宠物智能用品", "2025-01", 1000),
                volume("宠物智能用品", "2025-12", 1300)
        );
        enricher.applyOne(card, trends, PlatformView.ALL.getLabel());
        assertEquals("+30.0%", card.getMarketGrowth());
    }

    private CategoryTrend volume(String category, String month, int volume) {
        CategoryTrend row = new CategoryTrend();
        row.setCategoryName(category);
        row.setPlatform(PlatformView.ALL.getLabel());
        row.setTrendMonth(month);
        row.setSearchVolume(volume);
        row.setGrowthRate(BigDecimal.ZERO);
        return row;
    }
}
