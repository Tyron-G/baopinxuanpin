package com.oneaix.selection.dto;

/** 信号推送渠道配置（钉钉/企业微信）2026-06-04 */
public record PushChannelConfig(
        Long id,
        Long brandId,
        String channelType,
        String webhookUrl,
        boolean enabled,
        String updatedAt
) {
}
