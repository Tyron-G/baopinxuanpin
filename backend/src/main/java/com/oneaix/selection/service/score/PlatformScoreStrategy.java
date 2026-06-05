package com.oneaix.selection.service.score;

import com.oneaix.selection.enums.CategoryKeyword;
import com.oneaix.selection.enums.PlatformView;

/**
 * 平台视角下的排序加分策略（与 {@link PlatformView} 标签解耦）。
 * 2026-06-04
 */
public final class PlatformScoreStrategy {

    private PlatformScoreStrategy() {
    }

    public static int insightCategoryBoost(PlatformView platform, String categoryName) {
        if (platform == null || platform.isAll() || categoryName == null) {
            return 0;
        }
        return switch (platform) {
            case DOUYIN -> {
                int boost = 0;
                if (CategoryKeyword.PET.matches(categoryName)) {
                    boost += 6;
                }
                if (CategoryKeyword.COFFEE.matches(categoryName)) {
                    boost += 5;
                }
                yield boost;
            }
            case TMALL -> {
                int boost = 0;
                if (CategoryKeyword.CLEANING.matches(categoryName)) {
                    boost += 6;
                }
                if (CategoryKeyword.PET.matches(categoryName)) {
                    boost += 3;
                }
                yield boost;
            }
            case XIAOHONGSHU -> {
                int boost = 0;
                if (CategoryKeyword.COFFEE.matches(categoryName)) {
                    boost += 6;
                }
                if (CategoryKeyword.PET.matches(categoryName)) {
                    boost += 4;
                }
                yield boost;
            }
            default -> 0;
        };
    }

    public static int opportunityScoreBoost(
            PlatformView platform,
            String scenario,
            String differentiation,
            String lifecycleStage
    ) {
        if (platform == null || platform.isAll()) {
            return 0;
        }
        String safeScenario = scenario == null ? "" : scenario;
        String safeDiff = differentiation == null ? "" : differentiation;
        return switch (platform) {
            case DOUYIN -> {
                int boost = 0;
                if (safeScenario.contains("户外") || safeScenario.contains("独自在家") || safeScenario.contains("露营")) {
                    boost += 6;
                }
                if (safeDiff.contains("语音") || safeDiff.contains("便携") || safeDiff.contains("夜")) {
                    boost += 4;
                }
                yield boost;
            }
            case TMALL -> {
                int boost = 0;
                if (safeDiff.contains("稳定") || safeDiff.contains("分餐") || safeDiff.contains("可拆洗")) {
                    boost += 5;
                }
                if (lifecycleStage != null && lifecycleStage.contains("成长")) {
                    boost += 3;
                }
                yield boost;
            }
            case XIAOHONGSHU -> {
                int boost = 0;
                if (safeScenario.contains("礼物") || safeScenario.contains("露营") || safeScenario.contains("看护")) {
                    boost += 5;
                }
                if (safeDiff.contains("轻量") || safeDiff.contains("陪伴") || safeDiff.contains("高颜值")) {
                    boost += 4;
                }
                yield boost;
            }
            default -> 0;
        };
    }
}
