package com.oneaix.selection.dto;

import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.entity.Opportunity;

import java.util.List;

public record OpportunityDetail(
        InsightCard insightCard,
        BrandInfo brand,
        DecisionSummary decisionSummary,
        ScoreBreakdown scoreBreakdown,
        ConstraintMatch constraintMatch,
        BrandFitDetail brandFitDetail,
        CompetitionReport competitionReport,
        ProfitAnalysis profitAnalysis,
        SupplyChainFeasibility supplyChainFeasibility,
        PlatformPlaybook platformPlaybook,
        List<CompetitorShop> relatedCompetitors,
        CompetitorSummary competitorSummary,
        List<String> differentiationAdvice,
        List<ReportAction> nextActions,
        List<Opportunity> points,
        List<SentimentTerm> sentimentTerms,
        List<CrowdScene> crowdScenes,
        PatentIntel patentIntel,
        Alibaba1688Intel alibaba1688Intel,
        List<SellingPointSuggestion> sellingPoints,
        OpportunityMarketContext marketContext,
        List<ExternalDriverItem> externalDrivers,
        EntryBarrierAssessment entryBarrier,
        CompetitionQuadrantReport competitionQuadrant
) {
}
