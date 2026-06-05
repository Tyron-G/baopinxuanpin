package com.oneaix.selection.dto;

/** Webhook 推送投递记录（样例/演示）2026-06-04 */
public record PushDeliveryRecord(
        Long id,
        Long brandId,
        String channelType,
        String status,
        String webhookMasked,
        String payloadPreview,
        String responseBody,
        String deliveredAt
) {
}
