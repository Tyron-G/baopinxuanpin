package com.oneaix.selection.service.opportunity;

import com.oneaix.selection.dto.LifecycleInsight;
import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.util.PlatformMarketFilter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

/** 生命周期：搜索增速二阶导是否为正 2026-06-05 */
@Component
public class LifecycleInsightBuilder {

    public LifecycleInsight build(String categoryName, String platform, List<CategoryTrend> trends) {
        List<CategoryTrend> series = PlatformMarketFilter.byPlatform(trends, platform, CategoryTrend::getPlatform).stream()
                .filter(row -> categoryName.equals(row.getCategoryName()))
                .sorted(Comparator.comparing(CategoryTrend::getTrendMonth))
                .toList();

        if (series.size() < 3) {
            return fallback(categoryName);
        }

        int size = series.size();
        double g1 = series.get(size - 3).getGrowthRate().doubleValue();
        double g2 = series.get(size - 2).getGrowthRate().doubleValue();
        double g3 = series.get(size - 1).getGrowthRate().doubleValue();
        double acceleration = g3 - 2 * g2 + g1;
        boolean accelerating = acceleration > 0.5;
        double latest = g3;

        String stage = inferStage(latest, accelerating);
        String derivativeLabel = accelerating
                ? "二阶导为正 · 搜索增速仍在加速"
                : "二阶导非正 · 增速放缓或进入平台期";

        String summary = categoryName + " 当前处于「" + stage + "」，"
                + derivativeLabel + "（近三月增速 " + format(g1) + "% → " + format(g2) + "% → " + format(g3) + "%）。";

        return new LifecycleInsight(stage, accelerating, derivativeLabel, latest, scale(acceleration), summary);
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

    private LifecycleInsight fallback(String categoryName) {
        return new LifecycleInsight(
                "成长期",
                true,
                "样例口径：增速加速",
                28.0,
                2.5,
                categoryName + " 趋势样本不足，默认按成长期 + 增速加速样例展示。"
        );
    }

    private double scale(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    private String format(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).toPlainString();
    }
}
