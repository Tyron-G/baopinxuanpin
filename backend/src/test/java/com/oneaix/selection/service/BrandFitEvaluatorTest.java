package com.oneaix.selection.service;

import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.enums.FitLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 2026-06-04 BrandFitEvaluator 结构化评估测试 */
class BrandFitEvaluatorTest {
    private BrandFitEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new BrandFitEvaluator();
    }

    @Test
    void shouldDetectHighPlatformFitForPetOnDouyin() {
        BrandInfo brand = new BrandInfo();
        brand.setTargetPlatforms("抖音");
        InsightCard card = new InsightCard();
        card.setCategoryName("宠物智能用品");
        card.setRecommendation("推荐立项");

        var assessment = evaluator.assess(brand, card);
        assertEquals(FitLevel.HIGH, assessment.platformLevel());
        assertEquals("内容平台匹配度高", assessment.platformLabel());
        assertEquals(4, assessment.platformBoost());
    }

    @Test
    void shouldMarkProfitIncompatibleWhenTargetTooHigh() {
        BrandInfo brand = new BrandInfo();
        brand.setProfitMin("＞25%");
        InsightCard card = new InsightCard();
        card.setCategoryName("家用清洁机器人");
        card.setRecommendation("建议放弃");

        assertFalse(evaluator.isProfitCompatible(brand, card));
        assertEquals(1, evaluator.assess(brand, card).profitBoost());
    }
}
