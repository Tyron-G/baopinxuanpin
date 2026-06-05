package com.oneaix.selection.service.insight;

import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.enums.PlatformView;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 2026-06-05 CategoryBriefBuilder */
class CategoryBriefBuilderTest {

    private final TrendTwelveMonthGrowthCalculator growthCalculator = new TrendTwelveMonthGrowthCalculator();

    private final CategoryBriefBuilder builder = new CategoryBriefBuilder(
            new PotentialCategoryListBuilder(growthCalculator),
            growthCalculator
    );

    @Test
    void shouldPreferAllPlatformTrendRowsAndJanDecGrowth() {
        CategoryTrend jan = trend("宠物智能用品", PlatformView.ALL.getLabel(), "2025-01", 30, 1000);
        CategoryTrend dec = trend("宠物智能用品", PlatformView.ALL.getLabel(), "2025-12", 99, 1300);
        CategoryTrend douyin = trend("宠物智能用品", PlatformView.DOUYIN.getLabel(), "2025-12", 99, 5000);
        var top3 = builder.trendTop3(List.of(douyin, dec, jan), "全平台");
        assertEquals(1, top3.size());
        assertEquals("宠物智能用品", top3.get(0).categoryName());
        assertEquals("12月同比 30.0%", top3.get(0).metric());
        assertEquals("30.0%", top3.get(0).growthRate12m());
    }

    @Test
    void shouldRankTrendTop3ByJanDecGrowthNotLatestMonthRate() {
        CategoryTrend petJan = trend("宠物智能用品", PlatformView.ALL.getLabel(), "2025-01", 5, 1000);
        CategoryTrend petDec = trend("宠物智能用品", PlatformView.ALL.getLabel(), "2025-12", 99, 1500);
        CategoryTrend coffeeJan = trend("便携式咖啡器具", PlatformView.ALL.getLabel(), "2025-01", 5, 1000);
        CategoryTrend coffeeDec = trend("便携式咖啡器具", PlatformView.ALL.getLabel(), "2025-12", 10, 1100);
        var top3 = builder.trendTop3(List.of(coffeeDec, coffeeJan, petDec, petJan), "全平台");
        assertEquals("宠物智能用品", top3.get(0).categoryName());
        assertEquals("50.0%", top3.get(0).growthRate12m());
    }

    private CategoryTrend trend(String category, String platform, String month, int growth, int volume) {
        CategoryTrend row = new CategoryTrend();
        row.setCategoryName(category);
        row.setPlatform(platform);
        row.setTrendMonth(month);
        row.setGrowthRate(BigDecimal.valueOf(growth));
        row.setSearchVolume(volume);
        row.setRisingWords("占位词");
        return row;
    }
}
