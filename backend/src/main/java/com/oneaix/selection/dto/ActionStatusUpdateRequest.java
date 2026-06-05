package com.oneaix.selection.dto;

import com.oneaix.selection.validation.AllowedActionStatus;
import jakarta.validation.constraints.NotBlank;

/** 2026-06-04 动作状态更新请求 */
public record ActionStatusUpdateRequest(
        @NotBlank @AllowedActionStatus String status,
        String note
) {
}
