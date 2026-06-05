package com.oneaix.selection.service.competitor;

import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.enums.PlatformView;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 2026-06-04 */
class CompetitorTimelineRuleEngineTest {

    private final CompetitorTimelineRuleEngine ruleEngine = new CompetitorTimelineRuleEngine();

    @Test
    void shouldGenerateFourBasePointsForTmallShop() {
        CompetitorShop shop = sampleShop("测试店", PlatformView.TMALL.getLabel());
        var points = ruleEngine.generateBasePoints(shop);
        assertEquals(4, points.size());
        assertEquals("第1周", points.get(0).period());
        assertEquals(48, points.get(0).heatIndex());
        assertEquals(64, points.get(0).salesIndex());
    }

    @Test
    void shouldApplyViewBoostOnTopOfBasePoints() {
        CompetitorShop shop = sampleShop("测试店", PlatformView.TMALL.getLabel());
        var boosted = ruleEngine.applyViewBoost(ruleEngine.generateBasePoints(shop), PlatformView.TMALL);
        assertEquals(51, boosted.get(0).heatIndex());
        assertEquals(70, boosted.get(0).salesIndex());
    }

    private CompetitorShop sampleShop(String name, String platform) {
        return new CompetitorShop(
                name,
                platform,
                "宠物智能用品",
                "摘要",
                "增长",
                "2026-06-04 10:00",
                1L,
                "sig-001",
                "搜索飙升",
                "上新",
                2,
                java.util.List.of("痛点"),
                java.util.List.of("标签")
        );
    }
}
