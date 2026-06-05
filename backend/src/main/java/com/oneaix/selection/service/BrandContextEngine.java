package com.oneaix.selection.service;

import com.oneaix.selection.dto.BrandFitDetail;
import com.oneaix.selection.dto.ConstraintMismatch;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.ScoreBreakdown;
import com.oneaix.selection.constant.ApiConstants;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.service.constraint.BrandConstraintEvaluator;
import com.oneaix.selection.service.insight.InsightViewAssembler;
import com.oneaix.selection.service.score.InsightScoreCalculator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 品牌约束规则引擎门面：对外 API 保持不变，内部委托专职组件。
 * 2026-06-04
 */
@Component
public class BrandContextEngine {

    private final BrandConstraintEvaluator constraintEvaluator;
    private final InsightScoreCalculator scoreCalculator;
    private final InsightViewAssembler viewAssembler;

    public BrandContextEngine(
            BrandConstraintEvaluator constraintEvaluator,
            InsightScoreCalculator scoreCalculator,
            InsightViewAssembler viewAssembler
    ) {
        this.constraintEvaluator = constraintEvaluator;
        this.scoreCalculator = scoreCalculator;
        this.viewAssembler = viewAssembler;
    }

    public long catalogBrandId() {
        return ApiConstants.CATALOG_BRAND_ID;
    }

    public Set<String> resolveVisibleCategories(BrandInfo brand, List<String> allCategories) {
        return constraintEvaluator.resolveVisibleCategories(brand, allCategories);
    }

    public List<InsightCard> filterAndRankCards(BrandInfo brand, List<InsightCard> catalog) {
        return constraintEvaluator.filterAndRankCards(brand, catalog);
    }

    public boolean isBudgetCompatible(BrandInfo brand, InsightCard card) {
        return constraintEvaluator.isBudgetCompatible(brand, card);
    }

    public boolean isPlatformCompatible(BrandInfo brand, InsightCard card) {
        return constraintEvaluator.isPlatformCompatible(brand, card);
    }

    public boolean isProfitCompatible(BrandInfo brand, InsightCard card) {
        return constraintEvaluator.isProfitCompatible(brand, card);
    }

    public List<String> buildConstraintHints(BrandInfo brand, InsightCard card) {
        return constraintEvaluator.buildConstraintHints(brand, card);
    }

    public BrandFitDetail buildBrandFitDetail(BrandInfo brand, InsightCard card) {
        return viewAssembler.buildBrandFitDetail(brand, card);
    }

    public List<ConstraintMismatch> buildConstraintMismatches(BrandInfo brand, InsightCard card) {
        return constraintEvaluator.buildConstraintMismatches(brand, card);
    }

    public List<InsightCardView> toViews(BrandInfo brand, List<InsightCard> cards) {
        return viewAssembler.toViews(brand, cards);
    }

    public InsightCardView toView(BrandInfo brand, InsightCard card) {
        return viewAssembler.toView(brand, card);
    }

    public ScoreBreakdown buildScoreBreakdown(BrandInfo brand, InsightCard card) {
        return scoreCalculator.buildScoreBreakdown(brand, card);
    }

    public boolean isPinnedTarget(BrandInfo brand, InsightCard card) {
        return constraintEvaluator.isPinnedTarget(brand, card);
    }
}
