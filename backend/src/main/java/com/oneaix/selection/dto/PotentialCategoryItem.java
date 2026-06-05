package com.oneaix.selection.dto;

/** 潜力类目清单项（PRD 产出：搜索增速＞30% 且社媒同步上升）2026-06-04 */
public record PotentialCategoryItem(
        String categoryName,
        String searchGrowth,
        String socialTrend,
        String risingWords,
        boolean socialSyncUp,
        String summary
) {
}
