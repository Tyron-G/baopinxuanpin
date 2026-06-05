package com.oneaix.selection.service.insight;

import com.oneaix.selection.entity.CategoryTrend;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** 2026-06-05 分平台 12 月同比文案 */
class CategoryBriefBuilderPlatformGrowthTest {

    private final TrendTwelveMonthGrowthCalculator growthCalculator = new TrendTwelveMonthGrowthCalculator();

    private final CategoryBriefBuilder builder = new CategoryBriefBuilder(
            new PotentialCategoryListBuilder(growthCalculator),
            growthCalculator
    );

    @Test
    void shouldExposeSinglePlatformJanDecGrowthWhenNotAllPlatform() {
        CategoryTrend jan = trend("宠物智能用品", "抖音", "2025-01", 10, 1000);
        CategoryTrend dec = trend("宠物智能用品", "抖音", "2025-12", 42, 1420);
        var top3 = builder.trendTop3(List.of(jan, dec), "抖音");
        assertTrue(top3.get(0).platformGrowthRates().contains("抖音"));
        assertTrue(top3.get(0).platformGrowthRates().contains("12月同比"));
        assertTrue(top3.get(0).platformGrowthRates().contains("42.0%"));
    }

    private CategoryTrend trend(String category, String platform, String month, int growth, int volume) {
        CategoryTrend row = new CategoryTrend();
        row.setCategoryName(category);
        row.setPlatform(platform);
        row.setTrendMonth(month);
        row.setGrowthRate(BigDecimal.valueOf(growth));
        row.setSearchVolume(volume);
        row.setRisingWords("词");
        return row;
    }
}
