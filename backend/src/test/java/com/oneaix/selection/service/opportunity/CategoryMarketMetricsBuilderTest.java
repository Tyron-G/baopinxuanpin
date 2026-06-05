package com.oneaix.selection.service.opportunity;

import com.oneaix.selection.entity.CompetitionData;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 2026-06-05 CategoryMarketMetricsBuilder */
class CategoryMarketMetricsBuilderTest {

    private final CategoryMarketMetricsBuilder builder = new CategoryMarketMetricsBuilder();

    @Test
    void shouldExposeSkuAndHomogeneity() {
        CompetitionData row = new CompetitionData();
        row.setCategoryName("宠物智能用品");
        row.setPlatform("天猫");
        row.setTotalSkuCount(1380);
        row.setTop10SalesRatio(BigDecimal.valueOf(32.5));
        row.setHomogeneityScore(BigDecimal.valueOf(42));
        row.setTotalSearchVolume(61200);

        var metrics = builder.build("宠物智能用品", "天猫", List.of(row));
        assertEquals(1380, metrics.totalSkuCount());
        assertEquals(42.0, metrics.homogeneityScore());
    }
}
