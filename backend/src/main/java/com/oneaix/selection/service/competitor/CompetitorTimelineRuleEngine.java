package com.oneaix.selection.service.competitor;

import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.dto.CompetitorTimelinePoint;
import com.oneaix.selection.enums.PlatformView;
import org.springframework.stereotype.Component;

import java.util.List;

/** 竞品时间轴基线节点规则（不含视图平台加成）2026-06-04 */
@Component
public class CompetitorTimelineRuleEngine {

    public List<CompetitorTimelinePoint> generateBasePoints(CompetitorShop shop) {
        PlatformView shopPlatform = PlatformView.normalize(shop.platform());
        int baseHeat = shopPlatform.heatBase();
        int baseSales = shopPlatform.salesBase();

        return List.of(
                new CompetitorTimelinePoint("第1周", baseHeat - 10, baseSales - 8, "开始上新预热"),
                new CompetitorTimelinePoint("第2周", baseHeat - 4, baseSales - 3, "内容/搜索承接开始放量"),
                new CompetitorTimelinePoint("第3周", baseHeat + 2, baseSales + 1, "爆品曝光与转化同步提升"),
                new CompetitorTimelinePoint("第4周", baseHeat + 8, baseSales + 6, latestNoteForShop(shop))
        );
    }

    public List<CompetitorTimelinePoint> applyViewBoost(List<CompetitorTimelinePoint> basePoints, PlatformView viewPlatform) {
        int heatBoost = viewPlatform.heatBoost();
        int salesBoost = viewPlatform.salesBoost();
        return basePoints.stream()
                .map(point -> new CompetitorTimelinePoint(
                        point.period(),
                        point.heatIndex() + heatBoost,
                        point.salesIndex() + salesBoost,
                        point.note()
                ))
                .toList();
    }

    public String latestNoteForShop(CompetitorShop shop) {
        PlatformView shopPlatform = PlatformView.normalize(shop.platform());
        if (shopPlatform == PlatformView.DOUYIN) {
            return "短视频反馈继续走强，需验证是否能稳定沉淀到成交。";
        }
        if (shopPlatform == PlatformView.TMALL) {
            return "货架转化更稳，需重点跟踪评价和客服承接。";
        }
        if (shopPlatform == PlatformView.XIAOHONGSHU) {
            return "种草互动较好，需确认能否持续外溢到站内搜索。";
        }
        return "热度与销量同步改善，建议继续观察下一轮上新节奏。";
    }
}
