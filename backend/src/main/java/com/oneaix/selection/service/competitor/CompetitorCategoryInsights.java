package com.oneaix.selection.service.competitor;

import com.oneaix.selection.enums.CategoryKeyword;

import java.util.List;

/** 按类目生成竞品跟踪文案（内置规则）2026-06-04 */
public final class CompetitorCategoryInsights {

    private CompetitorCategoryInsights() {
    }

    public static String latestHitSummary(String category) {
        if (CategoryKeyword.PET.matches(category)) {
            return "上新「AI 宠物摄像头」进入类目飙升榜 Top10";
        }
        if (CategoryKeyword.COFFEE.matches(category)) {
            return "发布「一键冷萃杯」站内搜索热度 +35%";
        }
        return "近期上新 1 款潜力 SKU，待进一步跟踪";
    }

    public static String recentLaunchSummary(String category) {
        if (CategoryKeyword.PET.matches(category)) {
            return "自动喂食器配件、夜视摄像头两条新品线同步试水";
        }
        if (CategoryKeyword.COFFEE.matches(category)) {
            return "围绕露营和办公场景，补了冷萃杯与折叠磨豆器";
        }
        if (CategoryKeyword.CLEANING.matches(category)) {
            return "集中迭代自清洁和宠物毛发防缠绕功能";
        }
        return "近期有连续上新动作，建议跟踪详情页和转化变化";
    }

    public static int estimatedHitProductCount(String category) {
        if (CategoryKeyword.PET.matches(category)) {
            return 3;
        }
        if (CategoryKeyword.COFFEE.matches(category)) {
            return 2;
        }
        if (CategoryKeyword.CLEANING.matches(category)) {
            return 1;
        }
        return 1;
    }

    public static List<String> typicalComplaintTopics(String category) {
        if (CategoryKeyword.PET.matches(category)) {
            return List.of("卡粮", "分餐不准", "远程提醒延迟");
        }
        if (CategoryKeyword.COFFEE.matches(category)) {
            return List.of("清洗麻烦", "防漏一般", "出液不稳");
        }
        if (CategoryKeyword.CLEANING.matches(category)) {
            return List.of("耗材贵", "基站占地", "宠物毛发缠绕");
        }
        return List.of("待补充差评主题");
    }
}
