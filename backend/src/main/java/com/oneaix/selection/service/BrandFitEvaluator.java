package com.oneaix.selection.service;

import com.oneaix.selection.dto.BrandFitAssessment;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.enums.CategoryKeyword;
import com.oneaix.selection.enums.DecisionType;
import com.oneaix.selection.enums.FitLevel;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.enums.ProfitMin;
import com.oneaix.selection.enums.StockCycle;
import org.springframework.stereotype.Component;

/**
 * 品牌适配结构化评估：先算等级再生成文案，避免 substring 反查评分。
 * 2026-06-04
 */
@Component
public class BrandFitEvaluator {

    public BrandFitAssessment assess(BrandInfo brand, InsightCard card) {
        FitLevel platformLevel = evaluatePlatformLevel(brand, card);
        FitLevel profitLevel = evaluateProfitLevel(brand, card);
        FitLevel supplyChainLevel = evaluateSupplyChainLevel(brand, card);
        FitLevel stockCycleLevel = evaluateStockCycleLevel(brand);
        return new BrandFitAssessment(
                platformLevel,
                platformLabel(brand, card, platformLevel),
                profitLevel,
                profitLabel(profitLevel),
                supplyChainLevel,
                supplyChainLabel(supplyChainLevel, brand, card),
                stockCycleLevel,
                stockCycleLabel(stockCycleLevel, brand)
        );
    }

    public boolean isProfitCompatible(BrandInfo brand, InsightCard card) {
        return evaluateProfitLevel(brand, card) != FitLevel.LOW;
    }

    private FitLevel evaluatePlatformLevel(BrandInfo brand, InsightCard card) {
        String platforms = brand.getTargetPlatforms();
        if (platforms == null || platforms.isBlank()) {
            return FitLevel.LOW;
        }
        if (PlatformView.csvContains(platforms, PlatformView.DOUYIN)
                && (CategoryKeyword.PET.matches(card.getCategoryName())
                || CategoryKeyword.COFFEE.matches(card.getCategoryName()))) {
            return FitLevel.HIGH;
        }
        if ((PlatformView.csvContains(platforms, PlatformView.TMALL)
                || PlatformView.csvContains(platforms, PlatformView.TAOBAO))
                && CategoryKeyword.CLEANING.matches(card.getCategoryName())) {
            return FitLevel.HIGH;
        }
        return FitLevel.MEDIUM;
    }

    private FitLevel evaluateProfitLevel(BrandInfo brand, InsightCard card) {
        ProfitMin profitMin = ProfitMin.fromLabel(brand.getProfitMin());
        if (profitMin == null) {
            return FitLevel.HIGH;
        }
        DecisionType decision = DecisionType.fromRecommendation(card.getRecommendation());
        if (profitMin.requiresRecommendDecision()) {
            return decision == DecisionType.RECOMMEND ? FitLevel.HIGH : FitLevel.LOW;
        }
        if (profitMin.toleratesWatchDecision()) {
            return decision != DecisionType.ABANDON ? FitLevel.MEDIUM : FitLevel.LOW;
        }
        return FitLevel.HIGH;
    }

    private FitLevel evaluateSupplyChainLevel(BrandInfo brand, InsightCard card) {
        if (brand.getSupplyChain() == null || brand.getSupplyChain().isBlank()) {
            return FitLevel.LOW;
        }
        if (CategoryKeyword.PET.matches(card.getCategoryName())
                || CategoryKeyword.CLEANING.matches(card.getCategoryName())) {
            return FitLevel.HIGH;
        }
        return FitLevel.MEDIUM;
    }

    private FitLevel evaluateStockCycleLevel(BrandInfo brand) {
        if (brand.getStockCycle() == null || brand.getStockCycle().isBlank()) {
            return FitLevel.LOW;
        }
        StockCycle stockCycle = StockCycle.fromLabel(brand.getStockCycle());
        if (stockCycle == null) {
            return FitLevel.LOW;
        }
        if (stockCycle.isShortCycle()) {
            return FitLevel.HIGH;
        }
        if (stockCycle.isLongCycle()) {
            return FitLevel.LOW;
        }
        return FitLevel.MEDIUM;
    }

    private String platformLabel(BrandInfo brand, InsightCard card, FitLevel level) {
        String platforms = brand.getTargetPlatforms();
        if (platforms == null || platforms.isBlank()) {
            return "平台尚未设置";
        }
        if (level == FitLevel.HIGH) {
            if (PlatformView.csvContains(platforms, PlatformView.DOUYIN)
                    && (CategoryKeyword.PET.matches(card.getCategoryName())
                    || CategoryKeyword.COFFEE.matches(card.getCategoryName()))) {
                return "内容平台匹配度高";
            }
            return "搜索平台匹配度高";
        }
        return "平台适配度中等";
    }

    private String profitLabel(FitLevel level) {
        if (level == FitLevel.LOW) {
            return "利润目标偏高，当前类目需谨慎";
        }
        return "利润目标可承接";
    }

    private String supplyChainLabel(FitLevel level, BrandInfo brand, InsightCard card) {
        if (level == FitLevel.LOW) {
            return "供应链资源待补充";
        }
        if (level == FitLevel.HIGH) {
            return "供应链资源具备协同空间";
        }
        return "供应链需额外验证";
    }

    private String stockCycleLabel(FitLevel level, BrandInfo brand) {
        if (brand.getStockCycle() == null || brand.getStockCycle().isBlank()) {
            return "备货周期未设置";
        }
        StockCycle stockCycle = StockCycle.fromLabel(brand.getStockCycle());
        if (stockCycle == null) {
            return "备货周期未设置";
        }
        return switch (level) {
            case HIGH -> "备货响应速度快";
            case LOW -> "备货周期偏长";
            default -> "备货周期可接受";
        };
    }
}
