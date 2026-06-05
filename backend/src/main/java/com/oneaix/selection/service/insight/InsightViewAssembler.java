package com.oneaix.selection.service.insight;

import com.oneaix.selection.dto.BrandFitAssessment;
import com.oneaix.selection.dto.BrandFitDetail;
import com.oneaix.selection.dto.CardViewEvaluation;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.ReasonItem;
import com.oneaix.selection.dto.RiskItem;
import com.oneaix.selection.dto.ScoreBreakdown;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.enums.CompetitionLevel;
import com.oneaix.selection.enums.DecisionType;
import com.oneaix.selection.enums.FitLevel;
import com.oneaix.selection.enums.MatchTag;
import com.oneaix.selection.enums.RiskLevel;
import com.oneaix.selection.enums.StockCycle;
import com.oneaix.selection.service.BrandFitEvaluator;
import com.oneaix.selection.service.constraint.BrandConstraintEvaluator;
import com.oneaix.selection.service.score.InsightScoreCalculator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** 洞察卡片视图组装（理由、风险、标签、适配详情）2026-06-04 */
@Component
public class InsightViewAssembler {

    private final BrandConstraintEvaluator constraintEvaluator;
    private final BrandFitEvaluator brandFitEvaluator;
    private final InsightScoreCalculator scoreCalculator;

    public InsightViewAssembler(
            BrandConstraintEvaluator constraintEvaluator,
            BrandFitEvaluator brandFitEvaluator,
            InsightScoreCalculator scoreCalculator
    ) {
        this.constraintEvaluator = constraintEvaluator;
        this.brandFitEvaluator = brandFitEvaluator;
        this.scoreCalculator = scoreCalculator;
    }

    public List<InsightCardView> toViews(BrandInfo brand, List<InsightCard> cards) {
        return cards.stream().map(card -> toView(brand, card)).toList();
    }

    public InsightCardView toView(BrandInfo brand, InsightCard card) {
        CardViewEvaluation evaluation = evaluate(brand, card);
        List<String> tags = buildMatchTags(brand, card);
        String decision = resolveDecision(card);
        return new InsightCardView(
                card,
                constraintEvaluator.isPinnedTarget(brand, card),
                constraintEvaluator.isBudgetCompatible(brand, card),
                tags,
                decision,
                evaluation.scoreBreakdown(),
                buildReasonItems(brand, card, evaluation),
                buildRiskItems(brand, card),
                buildBrandFitDetail(brand, card, evaluation),
                constraintEvaluator.buildConstraintMismatches(brand, card)
        );
    }

    public BrandFitDetail buildBrandFitDetail(BrandInfo brand, InsightCard card) {
        return buildBrandFitDetail(brand, card, evaluate(brand, card));
    }

    private CardViewEvaluation evaluate(BrandInfo brand, InsightCard card) {
        BrandFitAssessment assessment = brandFitEvaluator.assess(brand, card);
        ScoreBreakdown scoreBreakdown = scoreCalculator.buildScoreBreakdown(brand, card, assessment);
        return new CardViewEvaluation(assessment, scoreBreakdown);
    }

    public List<RiskItem> buildRiskItems(BrandInfo brand, InsightCard card) {
        List<RiskItem> risks = new ArrayList<>();
        if (!constraintEvaluator.isBudgetCompatible(brand, card)) {
            risks.add(new RiskItem(
                    "预算压力",
                    RiskLevel.HIGH.getCode(),
                    "预估启动资金 " + card.getEstimatedStartupCost() + "，可能超出当前预算带。"
            ));
        }
        CompetitionLevel competitionLevel = CompetitionLevel.fromLabel(card.getCompetitionLevel());
        if (competitionLevel.isHigh()) {
            risks.add(new RiskItem(
                    "竞争壁垒",
                    RiskLevel.HIGH.getCode(),
                    "当前类目竞争难度高，需更强差异化或更长验证周期。"
            ));
        } else if (competitionLevel.isMedium()) {
            risks.add(new RiskItem(
                    "竞争分化",
                    RiskLevel.MEDIUM.getCode(),
                    "已有腰部玩家聚集，建议先验证细分场景和切入点。"
            ));
        }
        StockCycle stockCycle = StockCycle.fromLabel(brand.getStockCycle());
        if (stockCycle != null && stockCycle.isLongCycle()) {
            risks.add(new RiskItem(
                    "响应速度",
                    RiskLevel.MEDIUM.getCode(),
                    "备货周期较长，可能错过短周期红利窗口。"
            ));
        }
        if (risks.isEmpty()) {
            risks.add(new RiskItem(
                    "执行风险",
                    RiskLevel.LOW.getCode(),
                    "当前主要风险在于小批量验证效果，需要持续跟踪 CPC 与转化率。"
            ));
        }
        return risks.stream().limit(2).toList();
    }

    public List<String> buildMatchTags(BrandInfo brand, InsightCard card) {
        List<String> tags = new ArrayList<>();
        if (constraintEvaluator.matchesExistingProduct(brand, card)) {
            tags.add("已有产品相关");
        }
        if (constraintEvaluator.isTargetCategory(brand, card)) {
            tags.add(MatchTag.TARGET_CATEGORY.getLabel());
        }
        if (constraintEvaluator.isBudgetCompatible(brand, card)) {
            tags.add(MatchTag.BUDGET_MATCH.getLabel());
        } else {
            tags.add(MatchTag.BUDGET_RISK.getLabel());
        }
        tags.add(MatchTag.fromDecision(DecisionType.fromRecommendation(card.getRecommendation())).getLabel());
        return tags;
    }

    private List<ReasonItem> buildReasonItems(BrandInfo brand, InsightCard card, CardViewEvaluation evaluation) {
        BrandFitAssessment assessment = evaluation.assessment();
        ScoreBreakdown scoreBreakdown = evaluation.scoreBreakdown();
        List<ReasonItem> reasons = new ArrayList<>();
        reasons.add(new ReasonItem(
                "趋势验证",
                card.getMarketGrowth() + "，对应趋势分 " + scoreBreakdown.trendScore() + " 分。",
                "market_growth"
        ));
        reasons.add(new ReasonItem(
                "供需缺口",
                card.getPriceGap() + "，对应供需分 " + scoreBreakdown.supplyGapScore() + " 分。",
                "price_gap"
        ));
        reasons.add(new ReasonItem(
                "品牌适配",
                summarizeBrandFit(brand, card, assessment, scoreBreakdown.brandFitScore()),
                "brand_fit"
        ));
        return reasons;
    }

    private BrandFitDetail buildBrandFitDetail(BrandInfo brand, InsightCard card, CardViewEvaluation evaluation) {
        BrandFitAssessment assessment = evaluation.assessment();
        int brandFitScoreValue = evaluation.scoreBreakdown().brandFitScore();
        String budgetFit = constraintEvaluator.isBudgetCompatible(brand, card) ? "预算可承接" : "预算存在压力";
        String overallFitLevel = FitLevel.fromScore(brandFitScoreValue).getLabel();
        String summary = assessment.platformLabel() + "；" + budgetFit + "；" + assessment.profitLabel()
                + "；" + assessment.supplyChainLabel() + "；" + assessment.stockCycleLabel();
        return new BrandFitDetail(
                assessment.platformLabel(),
                budgetFit,
                assessment.profitLabel(),
                assessment.supplyChainLabel(),
                assessment.stockCycleLabel(),
                overallFitLevel,
                summary
        );
    }

    private String summarizeBrandFit(
            BrandInfo brand,
            InsightCard card,
            BrandFitAssessment assessment,
            int score
    ) {
        StringBuilder builder = new StringBuilder();
        if (constraintEvaluator.isPinnedTarget(brand, card)) {
            builder.append("命中目标品类；");
        }
        builder.append(assessment.platformLabel()).append("；");
        builder.append(constraintEvaluator.isBudgetCompatible(brand, card) ? "预算可承接；" : "预算存在压力；");
        builder.append(assessment.profitLabel()).append("；");
        builder.append("品牌适配分 ").append(score).append(" 分。");
        return builder.toString();
    }

    private String resolveDecision(InsightCard card) {
        return DecisionType.fromRecommendation(card.getRecommendation()).getLabel();
    }
}
