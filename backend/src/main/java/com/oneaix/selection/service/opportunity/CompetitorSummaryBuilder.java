package com.oneaix.selection.service.opportunity;

import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.dto.CompetitorSummary;
import com.oneaix.selection.entity.InsightCard;
import org.springframework.stereotype.Component;

import java.util.List;

/** 竞品摘要组装 2026-06-04 */
@Component
public class CompetitorSummaryBuilder {

    public CompetitorSummary build(InsightCard card, List<CompetitorShop> competitors) {
        if (competitors.isEmpty()) {
            return new CompetitorSummary(
                    0,
                    "暂无",
                    0,
                    List.of(),
                    "暂无直接竞品跟踪数据",
                    "当前类目还没有沉淀出足够的竞品样本，建议先从信号页或竞品页补充跟踪对象。"
            );
        }

        String coveredPlatforms = competitors.stream()
                .map(CompetitorShop::platform)
                .distinct()
                .reduce((left, right) -> left + " / " + right)
                .orElse("暂无");
        int totalHitProductCount = competitors.stream()
                .mapToInt(CompetitorShop::hitProductCount)
                .sum();
        List<String> commonComplaintTopics = competitors.stream()
                .flatMap(shop -> shop.complaintTopics().stream())
                .distinct()
                .limit(4)
                .toList();
        String strongestSignal = competitors.stream()
                .map(shop -> shop.shopName() + "：" + shop.growthSignal())
                .findFirst()
                .orElse("暂无明显增长信号");
        String summary = "已跟踪 " + competitors.size() + " 家同类对象，覆盖 " + coveredPlatforms
                + "，累计发现 " + totalHitProductCount + " 个爆品/重点 SKU。"
                + (commonComplaintTopics.isEmpty()
                ? " 当前仍需继续补充差评与上新样本。"
                : " 高频痛点集中在「" + String.join("、", commonComplaintTopics) + "」，可作为 "
                + card.getCategoryName() + " 的优先切入口。");

        return new CompetitorSummary(
                competitors.size(),
                coveredPlatforms,
                totalHitProductCount,
                commonComplaintTopics,
                strongestSignal,
                summary
        );
    }
}
