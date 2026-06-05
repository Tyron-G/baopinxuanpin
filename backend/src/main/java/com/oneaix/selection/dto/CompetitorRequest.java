package com.oneaix.selection.dto;

import com.oneaix.selection.validation.SupportedPlatform;
import jakarta.validation.constraints.NotBlank;

/** 2026-06-03 手动添加竞品请求 */
public record CompetitorRequest(
        @NotBlank String shopName,
        @NotBlank @SupportedPlatform String platform,
        String focusCategory,
        Long cardId,
        String sourceSignalId,
        String sourceSignalType
) {
}
