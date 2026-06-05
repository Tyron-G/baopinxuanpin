package com.oneaix.selection.service.opportunity;

import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.service.insight.TrendTwelveMonthGrowthCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 2026-06-05 LifecycleInsightBuilder */
class LifecycleInsightBuilderTest {

    private final LifecycleInsightBuilder builder = new LifecycleInsightBuilder(new TrendTwelveMonthGrowthCalculator());

    @Test
    void shouldDetectPositiveSecondDerivative() {
        var insight = builder.build("宠物智能用品", "天猫", List.of(
                trend("2025-10", 18, 9000),
                trend("2025-11", 24, 9500),
                trend("2025-12", 32, 10000)
        ), List.of());
        assertTrue(insight.growthAccelerating());
        assertTrue(insight.secondDerivativeLabel().contains("二阶导为正"));
    }

    @Test
    void shouldExposeFirstHitTimelineFromCompetitors() {
        var competitor = new CompetitorShop(
                "小佩宠物旗舰店",
                PlatformView.TMALL.getLabel(),
                "宠物智能用品",
                "hit",
                "首个类目爆款出现约 11 个月前",
                "2026-06-05",
                1L,
                "id",
                "type",
                "launch",
                3,
                List.of("卡粮"),
                List.of()
        );
        var insight = builder.build("宠物智能用品", "天猫", List.of(
                trend("2025-01", 20, 8000),
                trend("2025-11", 24, 9000),
                trend("2025-12", 28, 10000)
        ), List.of(competitor));
        assertEquals(11, insight.firstHitMonthsAgo());
        assertTrue(insight.firstHitTimeline().contains("11 个月"));
    }

    private CategoryTrend trend(String month, int growth, int volume) {
        CategoryTrend row = new CategoryTrend();
        row.setCategoryName("宠物智能用品");
        row.setPlatform("天猫");
        row.setTrendMonth(month);
        row.setGrowthRate(BigDecimal.valueOf(growth));
        row.setSearchVolume(volume);
        return row;
    }
}
