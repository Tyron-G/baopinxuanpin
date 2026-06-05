package com.oneaix.selection.dto;

import java.util.List;

/** TOP50 机会榜单分页结果 2026-06-04 */
public record OpportunityRankingPage(
        int total,
        int page,
        int pageSize,
        List<OpportunityRankItem> items
) {
}
