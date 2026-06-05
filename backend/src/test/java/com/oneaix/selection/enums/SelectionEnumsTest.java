package com.oneaix.selection.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 2026-06-03 核心枚举行为校验 */
class SelectionEnumsTest {

    @Test
    void platformViewShouldNormalizeAndScore() {
        assertEquals(PlatformView.DOUYIN, PlatformView.normalize("抖音"));
        assertTrue(PlatformView.ALL.isAll());
        assertTrue(PlatformView.csvContains("天猫,抖音", PlatformView.DOUYIN));
        assertEquals(6, PlatformView.DOUYIN.insightCategoryBoost("宠物智能用品"));
    }

    @Test
    void decisionTypeShouldResolveRecommendation() {
        assertEquals(DecisionType.RECOMMEND, DecisionType.fromRecommendation("推荐优先切入"));
        assertEquals("推荐立项", DecisionType.RECOMMEND.getLabel());
        assertTrue(DecisionType.RECOMMEND.matchesRecommendation("推荐切入"));
    }

    @Test
    void signalStrengthShouldMapScore() {
        assertEquals(SignalStrength.STRONG, SignalStrength.fromScore(90));
        assertEquals(SignalStrength.MEDIUM, SignalStrength.fromScore(75));
        assertEquals(SignalStrength.WEAK, SignalStrength.fromScore(50));
    }

    @Test
    void workflowStageShouldAdvance() {
        assertEquals(WorkflowStageKey.RADAR, WorkflowStageKey.DATA_PREP.nextStage());
        assertEquals(WorkflowStageKey.RANKING, WorkflowStageKey.INSIGHT.nextStage());
        assertEquals(WorkflowStageKey.OPPORTUNITY, WorkflowStageKey.RANKING.nextStage());
        assertEquals(WorkflowStageStatus.DONE.getCode(), WorkflowStageKey.DATA_PREP.statusComparedTo(WorkflowStageKey.INSIGHT));
        assertEquals(WorkflowStageStatus.PENDING.getCode(), WorkflowStageKey.RANKING.statusComparedTo(WorkflowStageKey.INSIGHT));
    }
}
