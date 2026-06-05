package com.oneaix.selection.service.opportunity;

import com.oneaix.selection.content.CategoryPlaybook;
import com.oneaix.selection.content.CategoryPlaybookRegistry;
import com.oneaix.selection.dto.BrandSelectionContext;
import com.oneaix.selection.dto.DecisionSummary;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.OpportunityDetail;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.enums.DecisionType;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.service.CompetitorService;
import com.oneaix.selection.service.NextActionPlanner;
import com.oneaix.selection.repository.market.MarketDataRepository;
import com.oneaix.selection.service.catalog.InsightCardCatalogService;
import com.oneaix.selection.service.insight.InsightViewAssembler;
import com.oneaix.selection.util.TextFormats;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/** 机会详情页组装 2026-06-04 */
@Component
public class OpportunityDetailAssembler {

    private final InsightCardCatalogService catalogService;
    private final InsightViewAssembler viewAssembler;
    private final CompetitorService competitorService;
    private final CategoryPlaybookRegistry categoryPlaybookRegistry;
    private final NextActionPlanner nextActionPlanner;
    private final OpportunityPointService opportunityPointService;
    private final ConstraintMatchBuilder constraintMatchBuilder;
    private final DifferentiationAdviceBuilder differentiationAdviceBuilder;
    private final CompetitorSummaryBuilder competitorSummaryBuilder;
    private final PlatformPlaybookBuilder platformPlaybookBuilder;
    private final OpportunityIntelBuilder opportunityIntelBuilder;
    private final OpportunityExternalDriversBuilder externalDriversBuilder;
    private final EntryBarrierAssessmentBuilder entryBarrierAssessmentBuilder;
    private final CompetitionQuadrantBuilder competitionQuadrantBuilder;
    private final MarketDataRepository marketDataRepository;
    private final SupplyDemandGapModelBuilder supplyDemandGapModelBuilder;
    private final PriceBandDistributionBuilder priceBandDistributionBuilder;
    private final LifecycleInsightBuilder lifecycleInsightBuilder;

    public OpportunityDetailAssembler(
            InsightCardCatalogService catalogService,
            InsightViewAssembler viewAssembler,
            CompetitorService competitorService,
            CategoryPlaybookRegistry categoryPlaybookRegistry,
            NextActionPlanner nextActionPlanner,
            OpportunityPointService opportunityPointService,
            ConstraintMatchBuilder constraintMatchBuilder,
            DifferentiationAdviceBuilder differentiationAdviceBuilder,
            CompetitorSummaryBuilder competitorSummaryBuilder,
            PlatformPlaybookBuilder platformPlaybookBuilder,
            OpportunityIntelBuilder opportunityIntelBuilder,
            OpportunityExternalDriversBuilder externalDriversBuilder,
            EntryBarrierAssessmentBuilder entryBarrierAssessmentBuilder,
            CompetitionQuadrantBuilder competitionQuadrantBuilder,
            MarketDataRepository marketDataRepository,
            SupplyDemandGapModelBuilder supplyDemandGapModelBuilder,
            PriceBandDistributionBuilder priceBandDistributionBuilder,
            LifecycleInsightBuilder lifecycleInsightBuilder
    ) {
        this.catalogService = catalogService;
        this.viewAssembler = viewAssembler;
        this.competitorService = competitorService;
        this.categoryPlaybookRegistry = categoryPlaybookRegistry;
        this.nextActionPlanner = nextActionPlanner;
        this.opportunityPointService = opportunityPointService;
        this.constraintMatchBuilder = constraintMatchBuilder;
        this.differentiationAdviceBuilder = differentiationAdviceBuilder;
        this.competitorSummaryBuilder = competitorSummaryBuilder;
        this.platformPlaybookBuilder = platformPlaybookBuilder;
        this.opportunityIntelBuilder = opportunityIntelBuilder;
        this.externalDriversBuilder = externalDriversBuilder;
        this.entryBarrierAssessmentBuilder = entryBarrierAssessmentBuilder;
        this.competitionQuadrantBuilder = competitionQuadrantBuilder;
        this.marketDataRepository = marketDataRepository;
        this.supplyDemandGapModelBuilder = supplyDemandGapModelBuilder;
        this.priceBandDistributionBuilder = priceBandDistributionBuilder;
        this.lifecycleInsightBuilder = lifecycleInsightBuilder;
    }

    public OpportunityDetail assemble(Long cardId, BrandSelectionContext context, String platformView) {
        InsightCard card = catalogService.requireVisible(cardId, context);
        BrandInfo brand = context.brand();
        InsightCardView cardView = context.findCard(cardId)
                .orElseGet(() -> viewAssembler.toView(brand, card));
        CategoryPlaybook playbook = categoryPlaybookRegistry.resolve(card);
        PlatformView platform = PlatformView.normalize(platformView);

        var relatedCompetitors = CompetitorShopFilter.filterByPlatform(
                competitorService.relatedTo(brand.getId(), cardId, card.getCategoryName()),
                platformView
        );
        var differentiationAdvice = differentiationAdviceBuilder.build(playbook, brand, relatedCompetitors, platformView);
        var competitionReport = playbook.buildCompetitionReport(card, platform);
        var supplyChainFeasibility = buildSupplyChainFeasibility(playbook, brand);
        var patentIntel = opportunityIntelBuilder.buildPatent(card, playbook);
        var marketContext = opportunityIntelBuilder.buildMarketContext(card);
        String categoryName = card.getCategoryName();
        Set<String> categories = Set.of(categoryName);
        List<com.oneaix.selection.entity.CategoryTrend> trends =
                marketDataRepository.findTrendsByCategories(categories);
        List<com.oneaix.selection.entity.SupplyDemand> supplyRows =
                marketDataRepository.findSupplyDemandByCategories(categories);
        List<com.oneaix.selection.entity.CompetitionData> competitionRows =
                marketDataRepository.findCompetitionByCategories(categories);
        var lifecycleInsight = lifecycleInsightBuilder.build(categoryName, platformView, trends);
        var supplyGapModel = supplyDemandGapModelBuilder.build(
                card, platformView, trends, supplyRows, competitionRows);
        var priceBands = priceBandDistributionBuilder.build(categoryName, platformView, supplyRows);
        var points = opportunityPointService.list(cardId, categoryName, platformView, lifecycleInsight);

        return new OpportunityDetail(
                card,
                brand,
                buildDecisionSummary(card, cardView),
                cardView.scoreBreakdown(),
                constraintMatchBuilder.build(brand, card, cardView),
                cardView.brandFitDetail(),
                competitionReport,
                playbook.buildProfitAnalysis(platform),
                supplyChainFeasibility,
                platformPlaybookBuilder.build(brand, card, relatedCompetitors),
                relatedCompetitors,
                competitorSummaryBuilder.build(card, relatedCompetitors),
                differentiationAdvice,
                nextActionPlanner.build(cardId, card, brand, platformView),
                points,
                playbook.sentimentTerms(),
                playbook.crowdScenes(),
                patentIntel,
                opportunityIntelBuilder.build1688(card, playbook),
                opportunityIntelBuilder.buildSellingPoints(card),
                marketContext,
                externalDriversBuilder.build(card),
                entryBarrierAssessmentBuilder.build(
                        card,
                        competitionReport,
                        marketContext,
                        patentIntel,
                        supplyChainFeasibility
                ),
                competitionQuadrantBuilder.build(card, relatedCompetitors),
                supplyGapModel,
                priceBands,
                lifecycleInsight
        );
    }

    private DecisionSummary buildDecisionSummary(InsightCard card, InsightCardView cardView) {
        DecisionType decisionType = DecisionType.fromRecommendation(card.getRecommendation());
        var scoreBreakdown = cardView.scoreBreakdown();
        return new DecisionSummary(
                cardView.decision(),
                scoreBreakdown.confidence(),
                decisionType.getHeadline(),
                cardView.reasons(),
                cardView.risks(),
                scoreBreakdown
        );
    }

    private com.oneaix.selection.dto.SupplyChainFeasibility buildSupplyChainFeasibility(
            CategoryPlaybook playbook,
            BrandInfo brand
    ) {
        String chain = brand.getSupplyChain();
        if (chain == null || chain.isBlank()) {
            chain = "未填写供应链";
        }
        return playbook.buildSupplyChainFeasibility(brand, TextFormats.abbreviate(chain, 24));
    }
}
