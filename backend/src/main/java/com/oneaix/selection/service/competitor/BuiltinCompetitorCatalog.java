package com.oneaix.selection.service.competitor;

import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.enums.PlatformView;

import java.util.List;

/** 内置竞品样例（与 data.sql 种子对齐；H2 不可用时回退）2026-06-04 */
public final class BuiltinCompetitorCatalog {

    private BuiltinCompetitorCatalog() {
    }

    public static List<CompetitorShop> shops() {
        return List.of(
                new CompetitorShop(
                        "小佩宠物旗舰店",
                        PlatformView.TMALL.getLabel(),
                        "宠物智能用品",
                        "智能喂食器 SE 月销 1.2 万+",
                        "新品「视频喂食」搜索关联度上升",
                        "2026-06-01 10:00",
                        1L,
                        "builtin-001",
                        "搜索飙升",
                        "近 7 天上新 2 款宠物看护周边 SKU",
                        3,
                        List.of("卡粮", "噪音", "APP 连接不稳"),
                        List.of("目标品类", "预算匹配", "建议立项")
                ),
                new CompetitorShop(
                        "九阳便携厨电",
                        PlatformView.DOUYIN.getLabel(),
                        "便携式咖啡器具",
                        "手压咖啡杯直播 GMV 破 80 万",
                        "露营场景短视频转化率高于类目均值",
                        "2026-05-28 15:30",
                        2L,
                        "builtin-002",
                        "内容种草",
                        "近 10 天上新 1 款冷萃场景 SKU",
                        2,
                        List.of("清洗麻烦", "防漏一般", "保温时长短"),
                        List.of("内容平台相关", "建议观望", "场景细分")
                )
        );
    }
}
