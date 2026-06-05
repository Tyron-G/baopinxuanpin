package com.oneaix.selection.repository.market;

import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.entity.CompetitionData;
import com.oneaix.selection.entity.SupplyDemand;

import java.util.Collection;
import java.util.List;

/** 市场趋势/竞争/供需数据仓储接口 2026-06-04 */
public interface MarketDataRepository {

    List<CategoryTrend> findTrendsByCategories(Collection<String> categoryNames);

    List<CompetitionData> findCompetitionByCategories(Collection<String> categoryNames);

    List<SupplyDemand> findSupplyDemandByCategories(Collection<String> categoryNames);
}
