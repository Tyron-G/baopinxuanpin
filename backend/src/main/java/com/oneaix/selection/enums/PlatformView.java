package com.oneaix.selection.enums;

import com.oneaix.selection.service.score.PlatformScoreStrategy;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/** 销售/分析平台视图 2026-06-03 */
public enum PlatformView {
    ALL("全平台"),
    TMALL("天猫"),
    TAOBAO("淘宝"),
    DOUYIN("抖音"),
    XIAOHONGSHU("小红书"),
    AMAZON("亚马逊"),
    SHOPEE("Shopee"),
    TIKTOK_SHOP("TikTok Shop");

    private final String label;

    PlatformView(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean isAll() {
        return this == ALL;
    }

    public static PlatformView normalize(String raw) {
        if (raw == null || raw.isBlank()) {
            return ALL;
        }
        for (PlatformView platform : values()) {
            if (platform.label.equals(raw)) {
                return platform;
            }
        }
        return ALL;
    }

    /** 品牌建档可选平台（不含「全平台」）2026-06-04 */
    public static boolean isSupportedSelectionLabel(String raw) {
        if (raw == null || raw.isBlank()) {
            return false;
        }
        for (PlatformView platform : values()) {
            if (platform == ALL) {
                continue;
            }
            if (platform.label.equals(raw.trim())) {
                return true;
            }
        }
        return false;
    }

    /** 去重并保持顺序 2026-06-04 */
    public static List<String> normalizeSelection(List<String> platforms) {
        if (platforms == null || platforms.isEmpty()) {
            return List.of();
        }
        java.util.LinkedHashSet<String> ordered = new java.util.LinkedHashSet<>();
        for (String platform : platforms) {
            if (platform == null || platform.isBlank()) {
                continue;
            }
            String trimmed = platform.trim();
            if (isSupportedSelectionLabel(trimmed)) {
                ordered.add(trimmed);
            }
        }
        return List.copyOf(ordered);
    }

    public static List<String> parseCsv(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of(TMALL.label, DOUYIN.label);
        }
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList();
    }

    public static boolean csvContains(String raw, PlatformView platform) {
        return parseCsv(raw).contains(platform.label);
    }

    public static boolean onlyDouyin(String raw) {
        List<String> platforms = parseCsv(raw);
        return platforms.contains(DOUYIN.label)
                && !platforms.contains(TMALL.label)
                && !platforms.contains(TAOBAO.label);
    }

    public static boolean onlyShelfSearch(String raw) {
        List<String> platforms = parseCsv(raw);
        return !platforms.contains(DOUYIN.label)
                && (platforms.contains(TMALL.label) || platforms.contains(TAOBAO.label));
    }

    public String defaultGrowthSignal() {
        return switch (this) {
            case DOUYIN -> "短视频种草带动店铺搜索 +28%";
            case XIAOHONGSHU -> "笔记互动率高于同类店铺 15%";
            case TMALL -> "直通车 CPC 低于类目均值 12%";
            case TAOBAO -> "店铺整体动销率稳定";
            case AMAZON -> "BSR 排名周环比上升，Review 增速高于类目";
            case SHOPEE -> "东南亚站点搜索热度连续 4 周上升";
            case TIKTOK_SHOP -> "短视频带货 GMV 周环比 +35%";
            case ALL -> "店铺整体动销率稳定";
        };
    }

    public int heatBase() {
        return switch (this) {
            case DOUYIN -> 68;
            case XIAOHONGSHU -> 63;
            case TMALL -> 58;
            case TAOBAO -> 52;
            case AMAZON -> 56;
            case SHOPEE -> 54;
            case TIKTOK_SHOP -> 62;
            case ALL -> 52;
        };
    }

    public int salesBase() {
        return switch (this) {
            case TMALL -> 72;
            case DOUYIN -> 60;
            case XIAOHONGSHU -> 50;
            case TAOBAO -> 48;
            case AMAZON -> 55;
            case SHOPEE -> 53;
            case TIKTOK_SHOP -> 58;
            case ALL -> 48;
        };
    }

    public int heatBoost() {
        return switch (this) {
            case DOUYIN -> 6;
            case XIAOHONGSHU -> 4;
            case TMALL -> 3;
            default -> 0;
        };
    }

    public int salesBoost() {
        return switch (this) {
            case TMALL -> 6;
            case DOUYIN -> 4;
            case XIAOHONGSHU -> 2;
            default -> 0;
        };
    }

    /** 视图或店铺平台是否命中指定平台 */
    public static boolean involves(String viewPlatformRaw, String shopPlatformRaw, PlatformView target) {
        PlatformView view = normalize(viewPlatformRaw);
        PlatformView shop = normalize(shopPlatformRaw);
        return view == target || shop == target;
    }

    /** 优先取「全平台」行，无则回退全量 2026-06-04 */
    public static <T> List<T> preferAllPlatformRows(List<T> rows, Function<T, String> platformExtractor) {
        List<T> allPlatformRows = rows.stream()
                .filter(row -> ALL.getLabel().equals(platformExtractor.apply(row)))
                .toList();
        return allPlatformRows.isEmpty() ? rows : allPlatformRows;
    }

    /** 洞察卡片在指定平台视图下的排序加分 */
    public int insightCategoryBoost(String categoryName) {
        return PlatformScoreStrategy.insightCategoryBoost(this, categoryName);
    }

    /** 机会点在指定平台视图下的排序加分 */
    public int opportunityScoreBoost(String scenario, String differentiation, String lifecycleStage) {
        return PlatformScoreStrategy.opportunityScoreBoost(this, scenario, differentiation, lifecycleStage);
    }
}
