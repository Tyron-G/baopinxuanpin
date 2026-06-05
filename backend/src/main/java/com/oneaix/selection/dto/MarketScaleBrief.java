package com.oneaix.selection.dto;

/** TAM/SAM/SOM 市场规模摘要（内置样例）2026-06-04 */
public record MarketScaleBrief(
        String categoryName,
        String tam,
        String sam,
        String som,
        String annualGrowth,
        String summary
) {
}
