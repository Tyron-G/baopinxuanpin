package com.oneaix.selection.dto;

import java.util.List;

/** 推送执行结果 2026-06-04 */
public record PushDigestResult(
        boolean success,
        String message,
        int signalCount,
        List<String> channelResults,
        List<PushDeliveryRecord> deliveries
) {
}
