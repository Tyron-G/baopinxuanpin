package com.oneaix.selection.content;

import java.util.List;

/** 迭代1：10 个核心消费品类宇宙（内置样例）2026-06-04 */
public final class CategoryUniverseCatalog {

    public record CategoryProfile(
            String categoryName,
            String marketSize,
            String marketGrowth,
            String competitionPattern,
            String competitionLevel,
            String priceGap,
            String estimatedStartupCost,
            String recommendation,
            List<String> productVariants
    ) {
    }

    private static final List<CategoryProfile> PROFILES = List.of(
            profile("宠物智能用品", "约12.8亿/年", "+41.3%", "分散型竞争", "低到中", "100-150元供需缺口", "28-45万",
                    "推荐立项：需求处于红利期。", List.of("智能喂食器", "宠物摄像头", "自动饮水机", "分餐识别器", "夜视看护套装")),
            profile("便携式咖啡器具", "约8.6亿/年", "+22.8%", "腰部品牌较多", "中", "100-200元礼品化", "18-35万",
                    "建议观望：先验证场景。", List.of("手压浓缩杯", "露营咖啡套装", "便携磨豆机", "冷萃随行杯", "控温咖啡杯")),
            profile("家用清洁机器人", "约54亿/年", "+8.7%", "头部高度集中", "高", "低价段利润薄", "80万以上",
                    "建议放弃：进入门槛高。", List.of("扫拖一体机", "全能基站款", "宠物毛发款", "轻量扫地机", "热水洗拖布款")),
            profile("美妆个护仪器", "约22亿/年", "+31.6%", "功效细分有效", "中", "200-400元空白", "25-40万",
                    "推荐立项：功效+便携组合。", List.of("射频美容仪", "LED面罩", "头皮护理仪", "便携脱毛仪", "冷热护理仪")),
            profile("运动户外防晒", "约15亿/年", "+28.4%", "季节+功能双驱动", "中", "80-150元供给不足", "15-30万",
                    "推荐立项：场景防晒细分。", List.of("冰感防晒服", "儿童防晒喷雾", "骑行面罩", "沙滩防晒套装", "UPF50+外套")),
            profile("智能家居安防", "约18亿/年", "+26.1%", "品牌分散", "低到中", "入门套装缺口", "20-35万",
                    "推荐立项：低门槛套装。", List.of("门磁传感套装", "宠物看护摄像头", "可视门铃", "门窗传感器", "本地存储主机")),
            profile("母婴喂养用品", "约11亿/年", "+19.8%", "安全标准驱动", "中", "中高端奶瓶缺口", "22-38万",
                    "建议观望：需过认证。", List.of("防胀气奶瓶", "温奶消毒一体机", "辅食料理棒", "便携暖奶器", "硅胶辅食碗")),
            profile("健康轻食代餐", "约9亿/年", "+24.2%", "内容种草强", "中", "便携装价格带空", "12-25万",
                    "推荐立项：办公室场景。", List.of("高蛋白代餐棒", "冷泡燕麦杯", "0蔗糖酸奶", "办公室轻食盒", "便携蛋白粉")),
            profile("跨境家居收纳", "约7亿/年", "+33.5%", "跨境长尾机会", "低", "亚马逊小件真空", "10-22万",
                    "推荐立项：跨境小件。", List.of("真空压缩袋", "抽屉分隔盒", "壁挂收纳架", "旅行收纳六件套", "鞋盒收纳柜")),
            profile("数码配件快充", "约13亿/年", "+21.7%", "规格迭代快", "中", "GaN 多口缺口", "15-28万",
                    "建议观望：认证门槛。", List.of("65W GaN 充电器", "磁吸充电宝", "多口桌面充", "车载快充头", "编织快充线"))
    );

    private CategoryUniverseCatalog() {
    }

    public static List<CategoryProfile> profiles() {
        return PROFILES;
    }

    public static List<String> categoryNames() {
        return PROFILES.stream().map(CategoryProfile::categoryName).toList();
    }

    private static CategoryProfile profile(
            String name,
            String size,
            String growth,
            String pattern,
            String level,
            String gap,
            String cost,
            String rec,
            List<String> variants
    ) {
        return new CategoryProfile(name, size, growth, pattern, level, gap, cost, rec, variants);
    }
}
