package com.oneaix.selection.dto;

/** 第三方数据源连接状态（样例）2026-06-04 */
public record DataConnectorStatus(
        String provider,
        String providerType,
        String status,
        String lastSyncedAt,
        String coverage,
        String sampleHighlight,
        boolean demoData
) {
}
