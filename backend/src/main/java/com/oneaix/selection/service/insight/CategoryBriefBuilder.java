package com.oneaix.selection.service.insight;



import com.oneaix.selection.dto.CategoryBrief;

import com.oneaix.selection.entity.CategoryTrend;

import com.oneaix.selection.entity.CompetitionData;

import com.oneaix.selection.entity.SupplyDemand;

import com.oneaix.selection.enums.PlatformView;

import com.oneaix.selection.util.PlatformMarketFilter;

import org.springframework.stereotype.Component;



import java.math.BigDecimal;

import java.math.RoundingMode;

import java.util.ArrayList;

import java.util.Comparator;

import java.util.LinkedHashMap;

import java.util.List;

import java.util.Map;



/** 洞察摘要 Top3 榜单构建（PRD 推荐明细字段，支持平台视角）2026-06-05 */

@Component

public class CategoryBriefBuilder {



    private final PotentialCategoryListBuilder potentialCategoryListBuilder;



    public CategoryBriefBuilder(PotentialCategoryListBuilder potentialCategoryListBuilder) {

        this.potentialCategoryListBuilder = potentialCategoryListBuilder;

    }



    public List<CategoryBrief> trendTop3(List<CategoryTrend> trends, String platform) {

        List<CategoryTrend> source = PlatformMarketFilter.byPlatform(trends, platform, CategoryTrend::getPlatform);

        Map<String, CategoryTrend> latestByCategory = new LinkedHashMap<>();

        for (CategoryTrend trend : source) {

            latestByCategory.merge(trend.getCategoryName(), trend, (left, right) ->

                    left.getTrendMonth().compareTo(right.getTrendMonth()) >= 0 ? left : right);

        }

        return latestByCategory.values().stream()

                .sorted(Comparator.comparing(CategoryTrend::getGrowthRate).reversed())

                .limit(3)

                .map(row -> fromTrend(row, trends, platform))

                .toList();

    }



    public List<CategoryBrief> competitionTop3(List<CompetitionData> competition, String platform) {

        List<CompetitionData> source = PlatformMarketFilter.byPlatform(competition, platform, CompetitionData::getPlatform);

        return source.stream()

                .sorted(Comparator.comparing(CompetitionData::getCr5))

                .limit(3)

                .map(row -> fromCompetition(row, platform))

                .toList();

    }



    public List<CategoryBrief> supplyTop3(List<SupplyDemand> supplyDemand, String platform) {

        List<SupplyDemand> source = PlatformMarketFilter.byPlatform(supplyDemand, platform, SupplyDemand::getPlatform);

        Map<String, SupplyDemand> bestGapByCategory = new LinkedHashMap<>();

        for (SupplyDemand row : source) {

            bestGapByCategory.merge(row.getCategoryName(), row, (left, right) ->

                    left.getDemandSupplyRatio().compareTo(right.getDemandSupplyRatio()) >= 0 ? left : right);

        }

        List<CategoryBrief> result = new ArrayList<>();

        bestGapByCategory.values().stream()

                .sorted(Comparator.comparing(SupplyDemand::getDemandSupplyRatio).reversed())

                .limit(3)

                .forEach(row -> result.add(fromSupply(row, platform)));

        return result;

    }



    private CategoryBrief fromTrend(CategoryTrend row, List<CategoryTrend> allTrends, String platform) {

        String growth12m = formatPercent(row.getGrowthRate());

        String platformLabel = platform == null || platform.isBlank() ? PlatformView.ALL.getLabel() : platform;

        String platformRates = buildPlatformGrowthRates(row.getCategoryName(), allTrends, platformLabel);

        String description = row.getCategoryName() + " 在 " + platformLabel + " 搜索与内容声量同步上升，"

                + row.getRisingWords() + " 等词加速。";

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



    private CategoryBrief fromCompetition(CompetitionData row, String platform) {

        String platformLabel = platform == null || platform.isBlank() ? PlatformView.ALL.getLabel() : platform;

        String description = row.getConclusion() != null ? row.getConclusion()

                : "品类属于巨头领地，新品极难突围，除非有极致性价比或黑科技。";

        return new CategoryBrief(

                row.getCategoryName(),

                "CR5 " + row.getCr5() + "%",

                description,

                (long) row.getTotalSearchVolume(),

                "近12月搜索平稳",

                null,

                "CR3 " + row.getCr3() + "%",

                platformLabel + " CR3 " + row.getCr3() + "% · CR5 " + row.getCr5() + "%",

                description,

                tamSamSom(row.getCategoryName())

        );

    }



    private CategoryBrief fromSupply(SupplyDemand row, String platform) {

        String platformLabel = platform == null || platform.isBlank() ? PlatformView.ALL.getLabel() : platform;

        String description = row.getCategoryName() + " 在 " + platformLabel + " 的 " + row.getPriceRange()

                + " 价格带供需比 " + row.getDemandSupplyRatio() + "，供给仍偏紧。";

        return new CategoryBrief(

                row.getCategoryName(),

                row.getPriceRange(),

                description,

                (long) row.getSearchVolume(),

                "供需比 " + row.getDemandSupplyRatio(),

                null,

                "供给 " + row.getSupplyCount(),

                platformLabel + " 供需比 " + row.getDemandSupplyRatio(),

                description,

                tamSamSom(row.getCategoryName())

        );

    }



    private String buildPlatformGrowthRates(String categoryName, List<CategoryTrend> trends, String activePlatform) {

        if (!PlatformView.ALL.getLabel().equals(activePlatform)) {

            return trends.stream()

                    .filter(row -> categoryName.equals(row.getCategoryName()) && activePlatform.equals(row.getPlatform()))

                    .max(Comparator.comparing(CategoryTrend::getTrendMonth))

                    .map(row -> activePlatform + " 月增速 " + formatPercent(row.getGrowthRate()))

                    .orElse(activePlatform + " 暂无分平台增速样例");

        }

        Map<String, BigDecimal> latestGrowth = new LinkedHashMap<>();

        for (String label : List.of("天猫", "抖音", "小红书")) {

            trends.stream()

                    .filter(row -> categoryName.equals(row.getCategoryName()) && label.equals(row.getPlatform()))

                    .max(Comparator.comparing(CategoryTrend::getTrendMonth))

                    .ifPresent(row -> latestGrowth.put(label, row.getGrowthRate()));

        }

        if (latestGrowth.isEmpty()) {

            return "全平台增速见趋势图";

        }

        StringBuilder builder = new StringBuilder();

        latestGrowth.forEach((label, growth) -> {

            if (!builder.isEmpty()) {

                builder.append(" / ");

            }

            builder.append(label).append(" ").append(formatPercent(growth));

        });

        return builder.toString();

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

