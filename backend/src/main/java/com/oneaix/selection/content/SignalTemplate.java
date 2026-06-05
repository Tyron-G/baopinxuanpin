package com.oneaix.selection.content;

import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.enums.SignalStrength;
import com.oneaix.selection.enums.SignalType;

import java.util.List;

/** 内置信号模板 2026-06-04 */
public record SignalTemplate(
        String id,
        String categoryName,
        SignalType signalType,
        SignalStrength strength,
        int score,
        int confidence,
        PlatformView platform,
        String metric,
        String summary,
        String discoveredAt,
        String recommendedAction,
        List<String> reasonTags,
        PlatformGate platformGate
) {
    public enum PlatformGate {
        ALWAYS,
        REQUIRES_DOUYIN,
        REQUIRES_XIAOHONGSHU
    }

    public boolean matchesBrandPlatforms(String platformsCsv) {
        return switch (platformGate) {
            case ALWAYS -> true;
            case REQUIRES_DOUYIN -> PlatformView.csvContains(platformsCsv, PlatformView.DOUYIN);
            case REQUIRES_XIAOHONGSHU -> PlatformView.csvContains(platformsCsv, PlatformView.XIAOHONGSHU);
        };
    }
}

