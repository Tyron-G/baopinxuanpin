package com.oneaix.selection.service.opportunity;

import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.entity.CompetitionData;
import com.oneaix.selection.entity.Opportunity;
import com.oneaix.selection.entity.SupplyDemand;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** 2026-06-05 OpportunityScatterMetricsCalculator */
class OpportunityScatterMetricsCalculatorTest {

    private final OpportunityScatterMetricsCalculator calculator = new OpportunityScatterMetricsCalculator();

    @Test
    void shouldApplyNegativeCompetitionResistancePerPrd() {
        Opportunity point = new Opportunity();
        calculator.enrich(
                point,
                "宠物智能用品",
                "天猫",
                List.of(trend("2025-11", 5000, 20), trend("2025-12", 6500, 28)),
                List.of(competition(35)),
                List.of(supply(300, 20000))
        );
        assertTrue(point.getCompetitionResistance().doubleValue() < 0,
                "PRD 竞争阻力应为负值");
    }

    private CategoryTrend trend(String month, int socialHeat, int growth) {
        CategoryTrend row = new CategoryTrend();
        row.setCategoryName("宠物智能用品");
        row.setPlatform("天猫");
        row.setTrendMonth(month);
        row.setSocialHeat(socialHeat);
        row.setGrowthRate(BigDecimal.valueOf(growth));
        row.setSearchVolume(10000);
        return row;
    }

    private CompetitionData competition(double cr5) {
        CompetitionData row = new CompetitionData();
        row.setCategoryName("宠物智能用品");
        row.setPlatform("天猫");
        row.setCr5(BigDecimal.valueOf(cr5));
        row.setHomogeneityScore(BigDecimal.valueOf(40));
        return row;
    }

    private SupplyDemand supply(int supplyCount, int searchVolume) {
        SupplyDemand row = new SupplyDemand();
        row.setCategoryName("宠物智能用品");
        row.setPlatform("天猫");
        row.setSupplyCount(supplyCount);
        row.setSearchVolume(searchVolume);
        return row;
    }
}
