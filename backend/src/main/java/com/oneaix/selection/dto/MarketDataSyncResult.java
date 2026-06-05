package com.oneaix.selection.dto;

/** 市场数据同步结果 2026-06-05 */
public record MarketDataSyncResult(
        boolean success,
        String message,
        String syncedAt
) {
}
