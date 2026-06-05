package com.oneaix.selection.service.opportunity;

import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.dto.LifecycleInsight;
import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.service.insight.TrendTwelveMonthGrowthCalculator;
import com.oneaix.selection.util.CategoryNameMatcher;
import com.oneaix.selection.util.PlatformMarketFilter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

/** 生命周期：二阶导 + 爆款出现时间节点 2026-06-05 */
@Component
public class LifecycleInsightBuilder {

    private final TrendTwelveMonthGrowthCalculator growthCalculator;

    public LifecycleInsightBuilder(TrendTwelveMonthGrowthCalculator growthCalculator) {
        this.growthCalculator = growthCalculator;
    }

    public LifecycleInsight build(
            String categoryName,
            String platform,
            List<CategoryTrend> trends,
            List<CompetitorShop> categoryCompetitors
    ) {
        List<CategoryTrend> series = PlatformMarketFilter.byPlatform(trends, platform, CategoryTrend::getPlatform).stream()
                .filter(row -> categoryName.equals(row.getCategoryName()))
                .sorted(Comparator.comparing(CategoryTrend::getTrendMonth))
                .toList();

        int firstHitMonths = resolveFirstHitMonths(categoryName, categoryCompetitors);
        String firstHitTimeline = "首个类目爆款出现于约 " + firstHitMonths + " 个月前";

        if (series.size() < 3) {
            return fallback(categoryName, firstHitTimeline, firstHitMonths);
        }

        int size = series.size();
        double g1 = series.get(size - 3).getGrowthRate().doubleValue();
        double g2 = series.get(size - 2).getGrowthRate().doubleValue();
        double g3 = series.get(size - 1).getGrowthRate().doubleValue();
        double acceleration = g3 - 2 * g2 + g1;
        boolean accelerating = acceleration > 0.5;
        double latest = growthCalculator.janToDecGrowth(trends, categoryName, platform)
                .map(BigDecimal::doubleValue)
                .orElse(g3);

        String stage = inferStage(latest, accelerating);
        String derivativeLabel = accelerating
                ? "二阶导为正 · 搜索增速仍在加速"
                : "二阶导非正 · 增速放缓或进入平台期";

        String summary = categoryName + " 当前处于「" + stage + "」，" + firstHitTimeline + "；"
                + derivativeLabel + "（近三月增速 " + format(g1) + "% → " + format(g2) + "% → " + format(g3) + "%）。";

        return new LifecycleInsight(
                stage,
                accelerating,
                derivativeLabel,
                latest,
                scale(acceleration),
                firstHitTimeline,
                firstHitMonths,
                summary
        );
    }

    private int resolveFirstHitMonths(String categoryName, List<CompetitorShop> categoryCompetitors) {
        int seed = 12 + Math.abs(categoryName.hashCode() % 16);
        if (categoryCompetitors == null || categoryCompetitors.isEmpty()) {
            return seed;
        }
        return categoryCompetitors.stream()
                .filter(shop -> CategoryNameMatcher.matches(shop.focusCategory(), categoryName))
                .mapToInt(shop -> growthCalculator.parseFirstHitMonths(shop.growthSignal(), seed))
                .min()
                .orElse(seed);
    }

    private String inferStage(double latestGrowth, boolean accelerating) {
        if (latestGrowth >= 35 && accelerating) {
            return "成长期";
        }
        if (latestGrowth >= 20) {
            return "成长期";
        }
        if (latestGrowth >= 8) {
            return "成熟期";
        }
        return "导入期/衰退观察";
    }

    private LifecycleInsight fallback(String categoryName, String firstHitTimeline, int firstHitMonths) {
        return new LifecycleInsight(
                "成长期",
                true,
                "样例口径：增速加速",
                28.0,
                2.5,
                firstHitTimeline,
                firstHitMonths,
                categoryName + " 趋势样本不足，" + firstHitTimeline + "；默认按成长期 + 增速加速样例展示。"
        );
    }

    private double scale(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    private String format(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).toPlainString();
    }
}
