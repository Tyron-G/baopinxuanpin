package com.oneaix.selection.service;

import com.oneaix.selection.annotation.TrackedExecution;
import com.oneaix.selection.dto.BrandSelectionContext;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.InsightSummary;
import com.oneaix.selection.dto.InsightSummaryBuildRequest;
import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.entity.CompetitionData;
import com.oneaix.selection.entity.SupplyDemand;
import com.oneaix.selection.service.insight.InsightMarketDataService;
import com.oneaix.selection.service.insight.InsightSummaryAssembler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/** 洞察 API 编排 2026-06-04 */
@Service
public class InsightService {

    private final BrandSelectionContextLoader contextLoader;
    private final InsightMarketDataService marketDataService;
    private final InsightSummaryAssembler summaryAssembler;

    public InsightService(
            BrandSelectionContextLoader contextLoader,
            InsightMarketDataService marketDataService,
            InsightSummaryAssembler summaryAssembler
    ) {
        this.contextLoader = contextLoader;
        this.marketDataService = marketDataService;
        this.summaryAssembler = summaryAssembler;
    }

    public List<CategoryTrend> trends(Long brandId) {
        return marketDataService.trends(visibleFor(brandId));
    }

    public List<CompetitionData> competition(Long brandId) {
        return marketDataService.competition(visibleFor(brandId));
    }

    public List<SupplyDemand> supplyDemand(Long brandId) {
        return marketDataService.supplyDemand(visibleFor(brandId));
    }

    public List<InsightCardView> cards(Long brandId) {
        return contextLoader.load(brandId).cards();
    }

    @TrackedExecution(value = "insight-summary", domain = "insight")
    public InsightSummary summary(Long brandId) {
        BrandSelectionContext context = contextLoader.load(brandId);
        Set<String> visible = context.visibleCategoryNames();
        return summaryAssembler.build(new InsightSummaryBuildRequest(
                context.brand(),
                context.catalog(),
                visible,
                context.cards(),
                marketDataService.trends(visible),
                marketDataService.competition(visible),
                marketDataService.supplyDemand(visible)
        ));
    }

    private Set<String> visibleFor(Long brandId) {
        return contextLoader.load(brandId).visibleCategoryNames();
    }
}
