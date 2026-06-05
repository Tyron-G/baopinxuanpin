package com.oneaix.selection.content;

import java.util.List;

/** TOP50 榜单补齐样例（当可见品类不足时填充）2026-06-04 */
public final class RankingPadCatalog {

    public record PadItem(String productTitle, String categoryName, int score, String reason) {
    }

    private static final List<PadItem> PAD_ITEMS = List.of(
            new PadItem("便携榨汁杯", "健康轻食代餐", 68, "办公室场景搜索稳步上升"),
            new PadItem("磁吸充电宝", "数码配件快充", 66, "GaN 规格迭代带来换机需求"),
            new PadItem("儿童防晒喷雾", "运动户外防晒", 72, "季节前置 + 社媒种草同步"),
            new PadItem("射频美容仪入门款", "美妆个护仪器", 70, "功效细分仍有效"),
            new PadItem("门磁传感套装", "智能家居安防", 67, "低门槛智能家居套装"),
            new PadItem("防胀气奶瓶", "母婴喂养用品", 65, "安全标准驱动复购"),
            new PadItem("高蛋白代餐棒", "健康轻食代餐", 64, "便携装搜索占比提升"),
            new PadItem("真空压缩袋六件套", "跨境家居收纳", 71, "跨境小件长尾稳定"),
            new PadItem("露营手压咖啡杯", "便携式咖啡器具", 69, "场景内容带动搜索"),
            new PadItem("分餐识别喂食器", "宠物智能用品", 74, "多宠家庭痛点未充分解决"),
            new PadItem("轻量扫地机", "家用清洁机器人", 58, "低价段仍有试错位"),
            new PadItem("冰感防晒服", "运动户外防晒", 73, "UPF50+ 关键词增长"),
            new PadItem("可视门铃", "智能家居安防", 68, "入门安防套装缺口"),
            new PadItem("便携暖奶器", "母婴喂养用品", 63, "场景礼品化潜力"),
            new PadItem("冷泡燕麦杯", "健康轻食代餐", 62, "办公室轻食子品类"),
            new PadItem("抽屉分隔盒", "跨境家居收纳", 66, "亚马逊小件稳定需求"),
            new PadItem("编织快充线", "数码配件快充", 61, "配件换代周期短"),
            new PadItem("宠物摄像头", "宠物智能用品", 76, "分离焦虑内容声量高"),
            new PadItem("手压浓缩杯", "便携式咖啡器具", 67, "露营场景增速快"),
            new PadItem("LED 夜视牵引绳", "宠物智能用品", 75, "差评痛点未覆盖的安全场景")
    );

    private RankingPadCatalog() {
    }

    public static List<PadItem> padItems() {
        return PAD_ITEMS;
    }
}
