package com.oneaix.selection.service.opportunity;

import com.oneaix.selection.entity.CategoryTrend;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** 2026-06-05 LifecycleInsightBuilder */
class LifecycleInsightBuilderTest {

    private final LifecycleInsightBuilder builder = new LifecycleInsightBuilder();

    @Test
    void shouldDetectPositiveSecondDerivative() {
        var insight = builder.build("宠物智能用品", "天猫", List.of(
                trend("2025-10", 18),
                trend("2025-11", 24),
                trend("2025-12", 32)
        ));
        assertTrue(insight.growthAccelerating());
        assertTrue(insight.secondDerivativeLabel().contains("二阶导为正"));
    }

    private CategoryTrend trend(String month, int growth) {
        CategoryTrend row = new CategoryTrend();
        row.setCategoryName("宠物智能用品");
        row.setPlatform("天猫");
        row.setTrendMonth(month);
        row.setGrowthRate(BigDecimal.valueOf(growth));
        row.setSearchVolume(10000);
        return row;
    }
}
