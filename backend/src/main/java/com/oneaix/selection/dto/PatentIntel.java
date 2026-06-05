package com.oneaix.selection.dto;

import java.util.List;

/** 专利检索结论（合享检索样例）2026-06-04 */
public record PatentIntel(
        String riskLevel,
        String summary,
        List<String> highlights,
        String dataProvider,
        String syncedAt,
        String searchQuery,
        List<PatentRecordItem> records
) {
}
