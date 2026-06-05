package com.oneaix.selection.dto;

import jakarta.validation.constraints.NotBlank;

/** 推送渠道保存请求 2026-06-04 */
public record PushChannelRequest(
        @NotBlank String channelType,
        @NotBlank String webhookUrl,
        boolean enabled
) {
}
