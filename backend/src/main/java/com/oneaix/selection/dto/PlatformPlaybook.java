package com.oneaix.selection.dto;

import java.util.List;

/** 2026-06-04 平台切入建议 */
public record PlatformPlaybook(
        String firstLaunchPlatform,
        String validationPlatform,
        String conversionPlatform,
        List<String> executionHints,
        String summary
) {
}
