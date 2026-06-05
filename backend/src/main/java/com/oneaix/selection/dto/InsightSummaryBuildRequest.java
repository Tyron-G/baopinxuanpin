package com.oneaix.selection.dto;

import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.entity.CompetitionData;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.entity.SupplyDemand;

import java.util.List;
import java.util.Set;

/** 洞察摘要构建入参 2026-06-04 */
public record InsightSummaryBuildRequest(
        BrandInfo brand,
        List<InsightCard> catalog,
        Set<String> visibleCategoryNames,
        List<InsightCardView> rankedCards,
        List<CategoryTrend> trends,
        List<CompetitionData> competition,
        List<SupplyDemand> supplyDemand,
        String platform
) {
}
