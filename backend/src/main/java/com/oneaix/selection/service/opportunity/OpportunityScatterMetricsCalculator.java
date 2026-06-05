package com.oneaix.selection.service.opportunity;

import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.entity.CompetitionData;
import com.oneaix.selection.entity.Opportunity;
import com.oneaix.selection.entity.SupplyDemand;
import com.oneaix.selection.util.PlatformMarketFilter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

/**
 * PRD 散点三轴公式（基于内置样例市场数据动态计算，非第三方实时源）2026-06-05
 * 机会引力 = 搜索增速×20% + 内容声量增速×80%
 * 竞争阻力 = -(供给饱和度×60% + 头部垄断度×40%)
 * 利润弹性 = 100 - 同质化评分
 */
@Component
public class OpportunityScatterMetricsCalculator {

    public void enrich(
            Opportunity point,
            String categoryName,
            String platform,
            List<CategoryTrend> trends,
            List<CompetitionData> competition,
            List<SupplyDemand> supplyDemand
    ) {
        CategoryTrend latestTrend = latestTrend(categoryName, platform, trends);
        CompetitionData comp = pickCompetition(categoryName, platform, competition);
        SupplyDemand supply = pickSupply(categoryName, platform, supplyDemand);

        double searchGrowth = latestTrend != null ? latestTrend.getGrowthRate().doubleValue() : 15.0;
        double contentGrowth = resolveContentGrowth(categoryName, platform, trends, latestTrend, searchGrowth);
        double gravity = searchGrowth * 0.2 + contentGrowth * 0.8;

        double monopoly = comp != null ? comp.getCr5().doubleValue() : 45.0;
        double supplySaturation = supply != null
                ? Math.min(100, supply.getSupplyCount() * 100.0 / Math.max(1, supply.getSearchVolume()))
                : 40.0;
        double resistance = -(supplySaturation * 0.6 + monopoly * 0.4);

        double homogeneity = comp != null ? comp.getHomogeneityScore().doubleValue() : 50.0;
        double elasticity = Math.max(5, 100 - homogeneity);

        point.setOpportunityGravity(scale(gravity));
        point.setCompetitionResistance(scale(resistance));
        point.setProfitElasticity(scale(elasticity));
    }

    private double resolveContentGrowth(
            String categoryName,
            String platform,
            List<CategoryTrend> trends,
            CategoryTrend latestTrend,
            double searchGrowthFallback
    ) {
        if (latestTrend == null) {
            return searchGrowthFallback;
        }
        CategoryTrend previous = PlatformMarketFilter.byPlatform(trends, platform, CategoryTrend::getPlatform).stream()
                .filter(row -> categoryName.equals(row.getCategoryName()))
                .filter(row -> row.getTrendMonth().compareTo(latestTrend.getTrendMonth()) < 0)
                .max(Comparator.comparing(CategoryTrend::getTrendMonth))
                .orElse(null);
        if (previous != null && previous.getSocialHeat() != null && previous.getSocialHeat() > 0) {
            double rate = (latestTrend.getSocialHeat() - previous.getSocialHeat()) * 100.0 / previous.getSocialHeat();
            return Math.max(-20, Math.min(80, rate));
        }
        return Math.min(80, latestTrend.getSocialHeat() / 150.0);
    }

    private CategoryTrend latestTrend(String categoryName, String platform, List<CategoryTrend> trends) {
        return PlatformMarketFilter.byPlatform(trends, platform, CategoryTrend::getPlatform).stream()
                .filter(row -> categoryName.equals(row.getCategoryName()))
                .max(Comparator.comparing(CategoryTrend::getTrendMonth))
                .orElse(null);
    }

    private CompetitionData pickCompetition(String categoryName, String platform, List<CompetitionData> rows) {
        return PlatformMarketFilter.byPlatform(rows, platform, CompetitionData::getPlatform).stream()
                .filter(row -> categoryName.equals(row.getCategoryName()))
                .findFirst()
                .orElse(null);
    }

    private SupplyDemand pickSupply(String categoryName, String platform, List<SupplyDemand> rows) {
        return PlatformMarketFilter.byPlatform(rows, platform, SupplyDemand::getPlatform).stream()
                .filter(row -> categoryName.equals(row.getCategoryName()))
                .max(Comparator.comparing(SupplyDemand::getDemandSupplyRatio))
                .orElse(null);
    }

    private BigDecimal scale(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP);
    }
}
