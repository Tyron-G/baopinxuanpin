package com.oneaix.selection.content;

import com.oneaix.selection.enums.PlatformView;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/** 2026-06-04 类目内容剧本注册表测试 */
class CategoryPlaybookRegistryTest {
    private final CategoryPlaybookRegistry registry = new CategoryPlaybookRegistry();

    @Test
    void shouldResolveKnownProfiles() {
        assertEquals(1L, registry.resolve(1L).cardId());
        assertEquals(2L, registry.resolve(2L).cardId());
        assertEquals(3L, registry.resolve(3L).cardId());
    }

    @Test
    void shouldBuildPlatformSpecificCompetitionSummary() {
        var card = new com.oneaix.selection.entity.InsightCard();
        card.setCompetitionPattern("分散竞争");
        card.setRecommendation("推荐立项");

        var report = registry.resolve(1L).buildCompetitionReport(card, PlatformView.DOUYIN);
        assertFalse(report.summary().contains("推荐立项"));
        assertEquals("浅蓝海 / 分散竞争", report.marketType());
    }
}

