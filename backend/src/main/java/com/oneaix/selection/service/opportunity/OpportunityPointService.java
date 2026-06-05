package com.oneaix.selection.service.opportunity;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oneaix.selection.dto.LifecycleInsight;
import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.entity.CompetitionData;
import com.oneaix.selection.entity.Opportunity;
import com.oneaix.selection.entity.SupplyDemand;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.mapper.OpportunityMapper;
import com.oneaix.selection.repository.market.MarketDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/** 机会点查询、平台排序与 PRD 散点三轴重算 2026-06-05 */
@Service
public class OpportunityPointService {

    private final OpportunityMapper opportunityMapper;
    private final MarketDataRepository marketDataRepository;
    private final OpportunityScatterMetricsCalculator scatterMetricsCalculator;

    public OpportunityPointService(
            OpportunityMapper opportunityMapper,
            MarketDataRepository marketDataRepository,
            OpportunityScatterMetricsCalculator scatterMetricsCalculator
    ) {
        this.opportunityMapper = opportunityMapper;
        this.marketDataRepository = marketDataRepository;
        this.scatterMetricsCalculator = scatterMetricsCalculator;
    }

    public List<Opportunity> list(Long cardId, String categoryName, String platformView) {
        return list(cardId, categoryName, platformView, null);
    }

    public List<Opportunity> list(
            Long cardId,
            String categoryName,
            String platformView,
            LifecycleInsight lifecycleInsight
    ) {
        PlatformView platform = PlatformView.normalize(platformView);
        Set<String> categories = Set.of(categoryName);
        List<CategoryTrend> trends = marketDataRepository.findTrendsByCategories(categories);
        List<CompetitionData> competition = marketDataRepository.findCompetitionByCategories(categories);
        List<SupplyDemand> supplyDemand = marketDataRepository.findSupplyDemandByCategories(categories);

        return opportunityMapper.selectList(new LambdaQueryWrapper<Opportunity>()
                        .eq(Opportunity::getInsightCardId, cardId)
                        .orderByDesc(Opportunity::getOpportunityScore))
                .stream()
                .peek(point -> scatterMetricsCalculator.enrich(
                        point, categoryName, platformView, trends, competition, supplyDemand))
                .peek(point -> applyLifecycle(point, lifecycleInsight))
                .sorted((left, right) -> Integer.compare(
                        platformAdjustedScore(right, platform),
                        platformAdjustedScore(left, platform)))
                .toList();
    }

    private void applyLifecycle(Opportunity point, LifecycleInsight lifecycleInsight) {
        if (lifecycleInsight == null) {
            return;
        }
        point.setLifecycleStage(lifecycleInsight.lifecycleStage());
        if (lifecycleInsight.growthAccelerating()) {
            String timing = point.getEntryTiming();
            if (timing == null || timing.isBlank()) {
                point.setEntryTiming(lifecycleInsight.secondDerivativeLabel());
            } else if (!timing.contains("二阶导")) {
                point.setEntryTiming(timing + " · " + lifecycleInsight.secondDerivativeLabel());
            }
        }
    }

    private int platformAdjustedScore(Opportunity point, PlatformView platform) {
        return point.getOpportunityScore() + platform.opportunityScoreBoost(
                point.getScenarioText(),
                point.getDifferentiation(),
                point.getLifecycleStage()
        );
    }
}
