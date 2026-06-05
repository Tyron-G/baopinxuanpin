package com.oneaix.selection.service.competitor;

import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.dto.CompetitorTimeline;
import com.oneaix.selection.dto.CompetitorTimelinePoint;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.repository.CompetitorTimelinePointRepository;
import com.oneaix.selection.util.CategoryNameMatcher;
import org.springframework.stereotype.Component;

import java.util.List;

/** 竞品时间轴与趋势文案组装（H2 节点优先，规则回退）2026-06-04 */
@Component
public class CompetitorTimelineBuilder {

    private final CompetitorTimelinePointRepository timelinePointRepository;
    private final CompetitorTimelineRuleEngine ruleEngine;

    public CompetitorTimelineBuilder(
            CompetitorTimelinePointRepository timelinePointRepository,
            CompetitorTimelineRuleEngine ruleEngine
    ) {
        this.timelinePointRepository = timelinePointRepository;
        this.ruleEngine = ruleEngine;
    }

    public List<CompetitorTimeline> buildAll(
            List<CompetitorShop> shops,
            String category,
            String platform
    ) {
        PlatformView normalizedPlatform = PlatformView.normalize(platform);
        return shops.stream()
                .filter(shop -> matchesCategory(shop, category))
                .filter(shop -> matchesPlatform(shop, normalizedPlatform))
                .map(shop -> buildOne(shop, normalizedPlatform))
                .toList();
    }

    public CompetitorTimeline buildOne(CompetitorShop shop, PlatformView platform) {
        return new CompetitorTimeline(
                shop.shopName(),
                shop.platform(),
                shop.focusCategory(),
                trendLabel(shop),
                timelineSummary(shop, platform),
                resolveTimelinePoints(shop, platform)
        );
    }

    private List<CompetitorTimelinePoint> resolveTimelinePoints(CompetitorShop shop, PlatformView platform) {
        List<CompetitorTimelinePoint> persisted = timelinePointRepository.findByShop(
                shop.shopName(),
                shop.platform(),
                shop.focusCategory()
        );
        if (!persisted.isEmpty()) {
            return ruleEngine.applyViewBoost(persisted, platform);
        }
        return ruleEngine.applyViewBoost(ruleEngine.generateBasePoints(shop), platform);
    }

    private boolean matchesCategory(CompetitorShop shop, String category) {
        if (category == null || category.isBlank()) {
            return true;
        }
        return CategoryNameMatcher.matches(shop.focusCategory(), category);
    }

    private boolean matchesPlatform(CompetitorShop shop, PlatformView platform) {
        if (platform.isAll()) {
            return true;
        }
        return platform.getLabel().equals(shop.platform());
    }

    private String trendLabel(CompetitorShop shop) {
        if (shop.growthSignal().contains("上升") || shop.growthSignal().contains("+")) {
            return "持续走强";
        }
        if (shop.growthSignal().contains("高于") || shop.growthSignal().contains("低于")) {
            return "效率分化";
        }
        return "稳定跟踪";
    }

    private String timelineSummary(CompetitorShop shop, PlatformView platform) {
        if (PlatformView.involves(platform.getLabel(), shop.platform(), PlatformView.DOUYIN)) {
            return "近 4 周内容热度抬升更明显，重点观察短视频起量是否持续传导到加购与成交。";
        }
        if (PlatformView.involves(platform.getLabel(), shop.platform(), PlatformView.TMALL)) {
            return "近 4 周搜索承接更稳定，重点观察评价沉淀和详情页转化效率。";
        }
        if (PlatformView.involves(platform.getLabel(), shop.platform(), PlatformView.XIAOHONGSHU)) {
            return "近 4 周种草互动更活跃，重点观察收藏与评论关键词是否持续正向累积。";
        }
        return "近 4 周整体表现保持抬升，适合继续跟踪热度、转化和差评变化是否同步。";
    }
}
