package com.oneaix.selection.service.insight;

import com.oneaix.selection.dto.CategoryBrief;
import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.entity.CompetitionData;
import com.oneaix.selection.entity.SupplyDemand;
import com.oneaix.selection.enums.PlatformView;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 洞察摘要 Top3 榜单构建（PRD 推荐明细字段）2026-06-04 */
@Component
public class CategoryBriefBuilder {

    private final PotentialCategoryListBuilder potentialCategoryListBuilder;

    public CategoryBriefBuilder(PotentialCategoryListBuilder potentialCategoryListBuilder) {
        this.potentialCategoryListBuilder = potentialCategoryListBuilder;
    }

    public List<CategoryBrief> trendTop3(List<CategoryTrend> trends) {
        List<CategoryTrend> source = PlatformView.preferAllPlatformRows(trends, CategoryTrend::getPlatform);
        Map<String, CategoryTrend> latestByCategory = new LinkedHashMap<>();
        for (CategoryTrend trend : source) {
            latestByCategory.merge(trend.getCategoryName(), trend, (left, right) ->
                    left.getTrendMonth().compareTo(right.getTrendMonth()) >= 0 ? left : right);
        }
        return latestByCategory.values().stream()
                .sorted(Comparator.comparing(CategoryTrend::getGrowthRate).reversed())
                .limit(3)
                .map(this::fromTrend)
                .toList();
    }

    public List<CategoryBrief> competitionTop3(List<CompetitionData> competition) {
        List<CompetitionData> source = PlatformView.preferAllPlatformRows(competition, CompetitionData::getPlatform);
        return source.stream()
                .sorted(Comparator.comparing(CompetitionData::getCr5))
                .limit(3)
                .map(this::fromCompetition)
                .toList();
    }

    public List<CategoryBrief> supplyTop3(List<SupplyDemand> supplyDemand) {
        List<SupplyDemand> source = PlatformView.preferAllPlatformRows(supplyDemand, SupplyDemand::getPlatform);
        Map<String, SupplyDemand> bestGapByCategory = new LinkedHashMap<>();
        for (SupplyDemand row : source) {
            bestGapByCategory.merge(row.getCategoryName(), row, (left, right) ->
                    left.getDemandSupplyRatio().compareTo(right.getDemandSupplyRatio()) >= 0 ? left : right);
        }
        List<CategoryBrief> result = new ArrayList<>();
        bestGapByCategory.values().stream()
                .sorted(Comparator.comparing(SupplyDemand::getDemandSupplyRatio).reversed())
                .limit(3)
                .forEach(row -> result.add(fromSupply(row)));
        return result;
    }

    private CategoryBrief fromTrend(CategoryTrend row) {
        String growth12m = formatPercent(row.getGrowthRate());
        String platformRates = "天猫 +24% / 抖音 +39% / 小红书 +31%";
        String description = row.getCategoryName() + " 属于红利期赛道，需求爆发且竞争对手尚未完全跟上。";
        return new CategoryBrief(
                row.getCategoryName(),
                "月增速 " + growth12m,
                description,
                (long) row.getSearchVolume(),
                growth12m,
                row.getSocialHeat(),
                row.getRisingWords(),
                platformRates,
                description,
                tamSamSom(row.getCategoryName())
        );
    }

    private CategoryBrief fromCompetition(CompetitionData row) {
        String description = row.getConclusion() != null ? row.getConclusion()
                : "品类属于巨头领地，新品极难突围，除非有极致性价比或黑科技。";
        return new CategoryBrief(
                row.getCategoryName(),
                "CR5 " + row.getCr5() + "%",
                description,
                (long) row.getTotalSearchVolume(),
                "近12月搜索平稳",
                null,
                "头部集中度 " + row.getCr3() + "%",
                "全平台 CR5 " + row.getCr5() + "%",
                description,
                tamSamSom(row.getCategoryName())
        );
    }

    private CategoryBrief fromSupply(SupplyDemand row) {
        String description = "搜索柱很高 + 供给柱很矮："
                + row.getPriceRange()
                + " 存在价格真空地带（用户想买但供给不足）。";
        return new CategoryBrief(
                row.getCategoryName(),
                row.getPriceRange(),
                description,
                (long) row.getSearchVolume(),
                "供需比 " + row.getDemandSupplyRatio(),
                null,
                "供给 " + row.getSupplyCount(),
                "全平台供需比 " + row.getDemandSupplyRatio(),
                description,
                tamSamSom(row.getCategoryName())
        );
    }

    private String tamSamSom(String categoryName) {
        var scale = potentialCategoryListBuilder.marketScaleFor(categoryName);
        return "TAM " + scale.tam() + " · SAM " + scale.sam() + " · SOM " + scale.som();
    }

    private String formatPercent(BigDecimal value) {
        if (value == null) {
            return "—";
        }
        return value.setScale(1, RoundingMode.HALF_UP) + "%";
    }
}
