package com.oneaix.selection.dto;

/** SaaS 多品牌工作区摘要 2026-06-05 */
public record BrandWorkspaceItem(
        Long brandId,
        String brandName,
        String industry,
        String targetPlatforms,
        boolean current
) {
}
