package com.oneaix.selection.service.insight;

import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.entity.CompetitionData;
import com.oneaix.selection.entity.SupplyDemand;
import com.oneaix.selection.repository.market.MarketDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/** 按可见品类查询趋势/竞争/供需（H2 表 + 仓储）2026-06-04 */
@Service
public class InsightMarketDataService {

    private final MarketDataRepository marketDataRepository;

    public InsightMarketDataService(MarketDataRepository marketDataRepository) {
        this.marketDataRepository = marketDataRepository;
    }

    public List<CategoryTrend> trends(Set<String> visibleCategories) {
        return trends(visibleCategories, null);
    }

    public List<CategoryTrend> trends(Set<String> visibleCategories, String platform) {
        return marketDataRepository.findTrendsByCategories(visibleCategories, platform);
    }

    public List<CompetitionData> competition(Set<String> visibleCategories) {
        return competition(visibleCategories, null);
    }

    public List<CompetitionData> competition(Set<String> visibleCategories, String platform) {
        return marketDataRepository.findCompetitionByCategories(visibleCategories, platform);
    }

    public List<SupplyDemand> supplyDemand(Set<String> visibleCategories) {
        return supplyDemand(visibleCategories, null);
    }

    public List<SupplyDemand> supplyDemand(Set<String> visibleCategories, String platform) {
        return marketDataRepository.findSupplyDemandByCategories(visibleCategories, platform);
    }
}
