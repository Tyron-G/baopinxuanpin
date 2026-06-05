package com.oneaix.selection.service.insight;

import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.MarketScaleBrief;
import com.oneaix.selection.dto.PotentialCategoryItem;
import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.enums.PlatformView;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 潜力类目清单（PRD 产出）2026-06-04 */
@Component
public class PotentialCategoryListBuilder {

    public List<PotentialCategoryItem> build(List<InsightCardView> rankedCards, List<CategoryTrend> trends) {
        Map<String, CategoryTrend> latestTrend = latestTrendByCategory(trends);
        List<PotentialCategoryItem> items = new ArrayList<>();
        for (InsightCardView view : rankedCards) {
            String category = view.card().getCategoryName();
            CategoryTrend trend = latestTrend.get(category);
            BigDecimal growth = trend != null ? trend.getGrowthRate() : BigDecimal.ZERO;
            int socialHeat = trend != null ? trend.getSocialHeat() : 0;
            boolean syncUp = growth.doubleValue() >= 30 && socialHeat >= 5000;
            if (!syncUp && items.size() >= 5) {
                continue;
            }
            items.add(new PotentialCategoryItem(
                    category,
                    "搜索增速 " + growth + "%",
                    "社媒热度 " + socialHeat,
                    trend != null ? trend.getRisingWords() : "—",
                    syncUp,
                    syncUp
                            ? category + " 搜索与社媒同步上升，属于 PRD 定义的潜力类目。"
                            : category + " 具备观察价值，建议结合平台视角继续验证。"
            ));
        }
        if (items.isEmpty() && !rankedCards.isEmpty()) {
            InsightCardView first = rankedCards.get(0);
            items.add(new PotentialCategoryItem(
                    first.card().getCategoryName(),
                    first.card().getMarketGrowth(),
                    "社媒热度同步",
                    "内置样例词",
                    true,
                    "当前约束下优先推荐的潜力类目。"
            ));
        }
        return items;
    }

    public MarketScaleBrief marketScaleFor(String categoryName) {
        return switch (categoryName) {
            case "宠物智能用品" -> new MarketScaleBrief(categoryName, "约 50 亿", "约 12.8 亿", "约 3.6 亿", "+41.3%",
                    "TAM 宽、SAM 聚焦智能看护细分、SOM 可在 100-150 元价格带切入。");
            case "家用清洁机器人" -> new MarketScaleBrief(categoryName, "约 320 亿", "约 54 亿", "约 8 亿", "+8.7%",
                    "大盘大但 SOM 受头部锁定，更适合有供应链壁垒的品牌。");
            default -> new MarketScaleBrief(categoryName, "约 30 亿", "约 8 亿", "约 2 亿", "+25%",
                    categoryName + " 细分赛道仍有结构性增长空间（内置样例口径）。");
        };
    }

    private Map<String, CategoryTrend> latestTrendByCategory(List<CategoryTrend> trends) {
        List<CategoryTrend> source = PlatformView.preferAllPlatformRows(trends, CategoryTrend::getPlatform);
        Map<String, CategoryTrend> latest = new LinkedHashMap<>();
        for (CategoryTrend row : source) {
            latest.merge(row.getCategoryName(), row, (left, right) ->
                    left.getTrendMonth().compareTo(right.getTrendMonth()) >= 0 ? left : right);
        }
        return latest;
    }
}
