package com.oneaix.selection.service.score;

import com.oneaix.selection.enums.PlatformView;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 2026-06-04 平台视角加分策略 */
class PlatformScoreStrategyTest {

    @Test
    void shouldBoostPetOnDouyinInsightView() {
        assertEquals(6, PlatformScoreStrategy.insightCategoryBoost(PlatformView.DOUYIN, "宠物智能用品"));
    }

    @Test
    void shouldBoostScenarioOnDouyinOpportunityView() {
        int boost = PlatformScoreStrategy.opportunityScoreBoost(
                PlatformView.DOUYIN,
                "独自在家宠物焦虑",
                "便携",
                null
        );
        assertEquals(10, boost);
    }
}
