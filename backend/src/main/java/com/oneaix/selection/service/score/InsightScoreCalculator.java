package com.oneaix.selection.service.score;

import com.oneaix.selection.dto.BrandFitAssessment;
import com.oneaix.selection.dto.ScoreBreakdown;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.enums.CompetitionLevel;
import com.oneaix.selection.enums.DecisionType;
import com.oneaix.selection.enums.StockCycle;
import com.oneaix.selection.service.BrandFitEvaluator;
import com.oneaix.selection.service.constraint.BrandConstraintEvaluator;
import com.oneaix.selection.util.TextFormats;
import org.springframework.stereotype.Component;

/** 洞察卡片评分拆解 2026-06-04 */
@Component
public class InsightScoreCalculator {

    private final BrandConstraintEvaluator constraintEvaluator;
    private final BrandFitEvaluator brandFitEvaluator;

    public InsightScoreCalculator(
            BrandConstraintEvaluator constraintEvaluator,
            BrandFitEvaluator brandFitEvaluator
    ) {
        this.constraintEvaluator = constraintEvaluator;
        this.brandFitEvaluator = brandFitEvaluator;
    }

    public ScoreBreakdown buildScoreBreakdown(BrandInfo brand, InsightCard card) {
        return buildScoreBreakdown(brand, card, brandFitEvaluator.assess(brand, card));
    }

    public ScoreBreakdown buildScoreBreakdown(BrandInfo brand, InsightCard card, BrandFitAssessment assessment) {
        int trendScore = trendScore(card);
        int competitionScore = competitionScore(card);
        int supplyGapScore = supplyGapScore(card);
        int brandFitScore = brandFitScore(brand, card, assessment);
        int riskPenalty = riskPenalty(brand, card);
        int totalScore = Math.max(0, Math.min(100, trendScore + competitionScore + supplyGapScore + brandFitScore - riskPenalty));
        int confidence = confidence(card, totalScore);
        return new ScoreBreakdown(
                trendScore,
                competitionScore,
                supplyGapScore,
                brandFitScore,
                riskPenalty,
                totalScore,
                confidence
        );
    }

    private int trendScore(InsightCard card) {
        double growth = parseGrowth(card.getMarketGrowth());
        if (growth >= 35) {
            return 30;
        }
        if (growth >= 25) {
            return 26;
        }
        if (growth >= 15) {
            return 21;
        }
        if (growth >= 8) {
            return 15;
        }
        return 10;
    }

    private int competitionScore(InsightCard card) {
        return CompetitionLevel.fromLabel(card.getCompetitionLevel()).scoreWeight();
    }

    private int supplyGapScore(InsightCard card) {
        String priceGap = TextFormats.nullToDash(card.getPriceGap());
        if (priceGap.contains("明显")) {
            return 18;
        }
        if (priceGap.contains("空间")) {
            return 14;
        }
        return 9;
    }

    private int brandFitScore(BrandInfo brand, InsightCard card, BrandFitAssessment assessment) {
        int score = 0;
        if (constraintEvaluator.isPinnedTarget(brand, card)) {
            score += 6;
        }
        score += constraintEvaluator.isBudgetCompatible(brand, card) ? 6 : 2;
        if (brand.getTargetPlatforms() != null && !brand.getTargetPlatforms().isBlank()) {
            score += assessment.platformBoost();
        }
        if (brand.getProfitMin() != null && !brand.getProfitMin().isBlank()) {
            score += assessment.profitBoost();
        }
        if (brand.getSupplyChain() != null && !brand.getSupplyChain().isBlank()) {
            score += assessment.supplyChainBoost();
        }
        if (brand.getStockCycle() != null && StockCycle.fromLabel(brand.getStockCycle()) == StockCycle.ABOVE_60) {
            score += 1;
        } else {
            score += 3;
        }
        return Math.min(score, 20);
    }

    private int riskPenalty(BrandInfo brand, InsightCard card) {
        int penalty = 0;
        if (!constraintEvaluator.isBudgetCompatible(brand, card)) {
            penalty += 3;
        }
        penalty += DecisionType.fromRecommendation(card.getRecommendation()).riskPenaltyWeight();
        StockCycle stockCycle = StockCycle.fromLabel(brand.getStockCycle());
        if (stockCycle != null && stockCycle.isLongCycle()) {
            penalty += 1;
        }
        return Math.min(penalty, 10);
    }

    private int confidence(InsightCard card, int totalScore) {
        int base = DecisionType.fromRecommendation(card.getRecommendation()).confidenceBase();
        return Math.min(95, Math.max(55, base + ((totalScore - 70) / 3)));
    }

    private double parseGrowth(String marketGrowth) {
        if (marketGrowth == null) {
            return 0;
        }
        String digits = marketGrowth.replaceAll("[^0-9.]", "");
        if (digits.isBlank()) {
            return 0;
        }
        try {
            return Double.parseDouble(digits);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

}
