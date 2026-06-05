package com.oneaix.selection.service.competitor;

import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.repository.CompetitorTimelinePointRepository;
import org.springframework.stereotype.Component;

/** 用户新增竞品时写入时间轴基线节点 2026-06-04 */
@Component
public class CompetitorTimelinePointSeeder {

    private final CompetitorTimelinePointRepository timelinePointRepository;
    private final CompetitorTimelineRuleEngine ruleEngine;

    public CompetitorTimelinePointSeeder(
            CompetitorTimelinePointRepository timelinePointRepository,
            CompetitorTimelineRuleEngine ruleEngine
    ) {
        this.timelinePointRepository = timelinePointRepository;
        this.ruleEngine = ruleEngine;
    }

    public void seedIfAbsent(CompetitorShop shop) {
        if (timelinePointRepository.existsByShop(shop.shopName(), shop.platform(), shop.focusCategory())) {
            return;
        }
        timelinePointRepository.saveBasePoints(
                shop.shopName(),
                shop.platform(),
                shop.focusCategory(),
                ruleEngine.generateBasePoints(shop)
        );
    }
}
