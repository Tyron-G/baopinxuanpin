package com.oneaix.selection.util;

import com.oneaix.selection.enums.PlatformView;

import java.util.List;
import java.util.function.Function;

/** 按平台视角过滤市场数据行（无匹配时回退全平台）2026-06-05 */
public final class PlatformMarketFilter {

    private PlatformMarketFilter() {
    }

    public static <T> List<T> byPlatform(List<T> rows, String platform, Function<T, String> platformExtractor) {
        if (rows == null || rows.isEmpty()) {
            return List.of();
        }
        if (platform == null || platform.isBlank() || PlatformView.ALL.getLabel().equals(platform)) {
            return PlatformView.preferAllPlatformRows(rows, platformExtractor);
        }
        List<T> matched = rows.stream()
                .filter(row -> platform.equals(platformExtractor.apply(row)))
                .toList();
        if (!matched.isEmpty()) {
            return matched;
        }
        return PlatformView.preferAllPlatformRows(rows, platformExtractor);
    }
}
