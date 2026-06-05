package com.oneaix.selection.content;

import com.oneaix.selection.dto.PainPointItem;

import java.util.List;

/** 洞察摘要叙事内容 2026-06-04 */
public final class InsightNarrativeContent {

    private InsightNarrativeContent() {
    }

    public static List<PainPointItem> summaryPainPointItems() {
        return List.of(
                new PainPointItem(1, "卡粮/分餐不准", 18, "高", "Top20 竞品中 18 家存在分餐误差或卡粮差评，用户情绪强度高。"),
                new PainPointItem(2, "噪音与 APP 稳定性", 14, "高", "夜间运行噪音与连接失败是第二大差评簇，影响复购。"),
                new PainPointItem(3, "便携清洗与防漏", 11, "中", "露营/办公场景下清洗麻烦、防漏一般，适合场景型差异化。"),
                new PainPointItem(4, "续航与耗材成本", 9, "中", "用户关注长期使用成本，愿意为有耗材订阅的产品付费。"),
                new PainPointItem(5, "安装与售后响应", 7, "中", "大件/智能品类安装复杂、售后慢会显著拉低转化。")
        );
    }

    public static List<String> summaryPainPoints() {
        return summaryPainPointItems().stream().map(PainPointItem::topic).toList();
    }
}
