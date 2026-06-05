package com.oneaix.selection.service.insight;

import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.enums.PlatformView;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 2026-06-05 TrendTwelveMonthGrowthCalculator */
class TrendTwelveMonthGrowthCalculatorTest {

    private final TrendTwelveMonthGrowthCalculator calculator = new TrendTwelveMonthGrowthCalculator();

    @Test
    void shouldComputeJanToDecGrowth() {
        List<CategoryTrend> trends = List.of(
                volume("宠物智能用品", "2025-01", 1000),
                volume("宠物智能用品", "2025-12", 1300)
        );
        var growth = calculator.janToDecGrowth(trends, "宠物智能用品", PlatformView.ALL.getLabel());
        assertTrue(growth.isPresent());
        assertEquals(0, growth.get().compareTo(BigDecimal.valueOf(30.0)));
    }

    @Test
    void shouldParseFirstHitMonthsFromSignal() {
        assertEquals(14, calculator.parseFirstHitMonths("首个类目爆款出现约 14 个月前", 99));
        assertEquals(99, calculator.parseFirstHitMonths(null, 99));
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
