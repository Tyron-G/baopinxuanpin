package com.oneaix.selection.service.insight;

import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.enums.PlatformView;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 2026-06-04 CategoryBriefBuilder */
class CategoryBriefBuilderTest {

    private final CategoryBriefBuilder builder = new CategoryBriefBuilder(new PotentialCategoryListBuilder());

    @Test
    void shouldPreferAllPlatformTrendRows() {
        CategoryTrend all = trend("宠物智能用品", PlatformView.ALL.getLabel(), "2026-05", 30);
        CategoryTrend douyin = trend("宠物智能用品", PlatformView.DOUYIN.getLabel(), "2026-06", 99);
        var top3 = builder.trendTop3(List.of(douyin, all), "全平台");
        assertEquals(1, top3.size());
        assertEquals("宠物智能用品", top3.get(0).categoryName());
        assertEquals("月增速 30.0%", top3.get(0).metric());
    }

    private CategoryTrend trend(String category, String platform, String month, int growth) {
        CategoryTrend row = new CategoryTrend();
        row.setCategoryName(category);
        row.setPlatform(platform);
        row.setTrendMonth(month);
        row.setGrowthRate(BigDecimal.valueOf(growth));
        row.setSearchVolume(1000);
        row.setRisingWords("占位词");
        return row;
    }
}
