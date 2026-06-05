package com.oneaix.selection.service.opportunity;

import com.oneaix.selection.content.CategoryPlaybookRegistry;
import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.entity.SupplyDemand;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** 2026-06-05 SupplyDemandGapModelBuilder */
class SupplyDemandGapModelBuilderTest {

    private final SupplyDemandGapModelBuilder builder =
            new SupplyDemandGapModelBuilder(new CategoryPlaybookRegistry());

    @Test
    void shouldComputeGapIndexFromPrdFactors() {
        InsightCard card = new InsightCard();
        card.setCategoryName("宠物智能用品");
        card.setPriceGap("100-150元");
        SupplyDemand supply = new SupplyDemand();
        supply.setCategoryName("宠物智能用品");
        supply.setPlatform("天猫");
        supply.setPriceRange("100-150元");
        supply.setSupplyCount(300);
        supply.setSearchVolume(20000);
        supply.setDemandSupplyRatio(BigDecimal.valueOf(72));

        var model = builder.build(
                card,
                "天猫",
                List.of(trend()),
                List.of(supply),
                List.of()
        );
        assertTrue(model.gapIndex() > 0);
        assertTrue(model.summary().contains("需求热度"));
    }

    private CategoryTrend trend() {
        CategoryTrend row = new CategoryTrend();
        row.setCategoryName("宠物智能用品");
        row.setPlatform("天猫");
        row.setTrendMonth("2025-12");
        row.setGrowthRate(BigDecimal.valueOf(30));
        row.setSearchVolume(22000);
        return row;
    }
}
