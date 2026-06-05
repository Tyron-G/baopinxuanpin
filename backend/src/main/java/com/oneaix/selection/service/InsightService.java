package com.oneaix.selection.service;

import com.oneaix.selection.annotation.TrackedExecution;
import com.oneaix.selection.dto.BrandSelectionContext;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.InsightSummary;
import com.oneaix.selection.dto.InsightSummaryBuildRequest;
import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.entity.CompetitionData;
import com.oneaix.selection.entity.SupplyDemand;
import com.oneaix.selection.service.catalog.InsightCardQueryService;
import com.oneaix.selection.service.insight.InsightMarketDataService;
import com.oneaix.selection.service.insight.InsightSummaryAssembler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/** 洞察 API 编排 2026-06-04 */
@Service
public class InsightService {

    private final BrandSelectionContextLoader contextLoader;
    private final InsightCardQueryService cardQueryService;
    private final InsightMarketDataService marketDataService;
    private final InsightSummaryAssembler summaryAssembler;

    public InsightService(
            BrandSelectionContextLoader contextLoader,
            InsightCardQueryService cardQueryService,
            InsightMarketDataService marketDataService,
            InsightSummaryAssembler summaryAssembler
    ) {
        this.contextLoader = contextLoader;
        this.cardQueryService = cardQueryService;
        this.marketDataService = marketDataService;
        this.summaryAssembler = summaryAssembler;
    }

    public List<CategoryTrend> trends(Long brandId, String platform) {
        return marketDataService.trends(visibleFor(brandId), platform);
    }

    public List<CompetitionData> competition(Long brandId, String platform) {
        return marketDataService.competition(visibleFor(brandId), platform);
    }

    public List<SupplyDemand> supplyDemand(Long brandId, String platform) {
        return marketDataService.supplyDemand(visibleFor(brandId), platform);
    }

    public List<InsightCardView> cards(Long brandId, String platform) {
        var context = contextLoader.load(brandId);
        return cardQueryService.rankedViews(context.brand(), context.catalog(), platform);
    }

    @TrackedExecution(value = "insight-summary", domain = "insight")
    public InsightSummary summary(Long brandId, String platform) {
        BrandSelectionContext context = contextLoader.load(brandId);
        Set<String> visible = context.visibleCategoryNames();
        List<InsightCardView> rankedCards = cardQueryService.rankedViews(context.brand(), context.catalog(), platform);
        return summaryAssembler.build(new InsightSummaryBuildRequest(
                context.brand(),
                context.catalog(),
                visible,
                rankedCards,
                marketDataService.trends(visible, platform),
                marketDataService.competition(visible, platform),
                marketDataService.supplyDemand(visible, platform),
                platform
        ));
    }

    private Set<String> visibleFor(Long brandId) {
        return contextLoader.load(brandId).visibleCategoryNames();
    }
}
