package com.oneaix.selection.service.insight;

import com.oneaix.selection.entity.CategoryTrend;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** 2026-06-05 分平台增速文案 */
class CategoryBriefBuilderPlatformGrowthTest {

    private final CategoryBriefBuilder builder = new CategoryBriefBuilder(new PotentialCategoryListBuilder());

    @Test
    void shouldExposeSinglePlatformGrowthWhenNotAllPlatform() {
        CategoryTrend douyin = trend("宠物智能用品", "抖音", "2025-12", 42);
        var top3 = builder.trendTop3(List.of(douyin), "抖音");
        assertTrue(top3.get(0).platformGrowthRates().contains("抖音"));
        assertTrue(top3.get(0).platformGrowthRates().contains("42"));
    }

    private CategoryTrend trend(String category, String platform, String month, int growth) {
        CategoryTrend row = new CategoryTrend();
        row.setCategoryName(category);
        row.setPlatform(platform);
        row.setTrendMonth(month);
        row.setGrowthRate(BigDecimal.valueOf(growth));
        row.setSearchVolume(8000);
        row.setRisingWords("词");
        return row;
    }
}
