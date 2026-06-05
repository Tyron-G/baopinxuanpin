package com.oneaix.selection.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oneaix.selection.constant.ApiConstants;
import com.oneaix.selection.content.CategoryUniverseCatalog;
import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.entity.CompetitionData;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.entity.Opportunity;
import com.oneaix.selection.entity.SupplyDemand;
import com.oneaix.selection.mapper.CategoryTrendMapper;
import com.oneaix.selection.mapper.CompetitionDataMapper;
import com.oneaix.selection.mapper.InsightCardMapper;
import com.oneaix.selection.mapper.OpportunityMapper;
import com.oneaix.selection.mapper.SupplyDemandMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/** 启动时补齐 10 品类洞察卡片与市场/机会样例（含分平台切片）2026-06-05 */
@Component
public class CategoryCatalogBootstrap implements ApplicationRunner {

    private static final List<String> DOMESTIC_PLATFORMS = List.of("天猫", "抖音", "小红书");

    private final InsightCardMapper insightCardMapper;
    private final CategoryTrendMapper categoryTrendMapper;
    private final CompetitionDataMapper competitionDataMapper;
    private final SupplyDemandMapper supplyDemandMapper;
    private final OpportunityMapper opportunityMapper;

    public CategoryCatalogBootstrap(
            InsightCardMapper insightCardMapper,
            CategoryTrendMapper categoryTrendMapper,
            CompetitionDataMapper competitionDataMapper,
            SupplyDemandMapper supplyDemandMapper,
            OpportunityMapper opportunityMapper
    ) {
        this.insightCardMapper = insightCardMapper;
        this.categoryTrendMapper = categoryTrendMapper;
        this.competitionDataMapper = competitionDataMapper;
        this.supplyDemandMapper = supplyDemandMapper;
        this.opportunityMapper = opportunityMapper;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (CategoryUniverseCatalog.CategoryProfile profile : CategoryUniverseCatalog.profiles()) {
            InsightCard card = findOrCreateCard(profile);
            seedTrend(profile.categoryName());
            seedCompetition(profile.categoryName());
            seedSupply(profile.categoryName());
            seedOpportunity(card, profile);
            ensurePlatformMarketSlices(profile.categoryName());
        }
        seedCrossBorderPlatforms("跨境家居收纳");
    }

    private InsightCard findOrCreateCard(CategoryUniverseCatalog.CategoryProfile profile) {
        InsightCard existing = insightCardMapper.selectOne(new LambdaQueryWrapper<InsightCard>()
                .eq(InsightCard::getBrandId, ApiConstants.CATALOG_BRAND_ID)
                .eq(InsightCard::getCategoryName, profile.categoryName())
                .last("LIMIT 1"));
        if (existing != null) {
            return existing;
        }
        InsightCard card = new InsightCard();
        card.setBrandId(ApiConstants.CATALOG_BRAND_ID);
        card.setCategoryName(profile.categoryName());
        card.setMarketSize(profile.marketSize());
        card.setMarketGrowth(profile.marketGrowth());
        card.setCompetitionPattern(profile.competitionPattern());
        card.setCompetitionLevel(profile.competitionLevel());
        card.setPriceGap(profile.priceGap());
        card.setEstimatedStartupCost(profile.estimatedStartupCost());
        card.setRecommendation(profile.recommendation());
        insightCardMapper.insert(card);
        return card;
    }

    private void ensurePlatformMarketSlices(String categoryName) {
        for (String platform : DOMESTIC_PLATFORMS) {
            seedPlatformTrendIfMissing(categoryName, platform);
            seedPlatformCompetitionIfMissing(categoryName, platform);
            seedPlatformSupplyIfMissing(categoryName, platform);
        }
    }

    private void seedCrossBorderPlatforms(String categoryName) {
        if (!"跨境家居收纳".equals(categoryName)) {
            return;
        }
        for (String platform : List.of("亚马逊", "Shopee", "TikTok Shop")) {
            if (existsTrend(categoryName, platform)) {
                continue;
            }
            CategoryTrend row = new CategoryTrend();
            row.setCategoryName(categoryName);
            row.setPlatform(platform);
            row.setTrendMonth("2025-12");
            row.setSearchVolume(12000 + Math.abs(platform.hashCode() % 3000));
            row.setSalesVolume(2800);
            row.setGrowthRate(BigDecimal.valueOf(33.5));
            row.setSocialHeat(4100);
            row.setRisingWords("跨境小包,收纳六件套");
            categoryTrendMapper.insert(row);
        }
    }

    private void seedTrend(String categoryName) {
        if (hasAnyTrend(categoryName)) {
            return;
        }
        insertTrendMonths(categoryName, "全平台", 1.0, 0.0);
    }

    private void seedPlatformTrendIfMissing(String categoryName, String platform) {
        if (existsTrend(categoryName, platform)) {
            return;
        }
        double volumeFactor = switch (platform) {
            case "天猫" -> 0.42;
            case "抖音" -> 0.36;
            case "小红书" -> 0.30;
            default -> 0.35;
        };
        double growthBoost = switch (platform) {
            case "天猫" -> -1.5;
            case "抖音" -> 4.0;
            case "小红书" -> 2.0;
            default -> 0.0;
        };
        insertTrendMonths(categoryName, platform, volumeFactor, growthBoost);
    }

    private void insertTrendMonths(String categoryName, String platform, double volumeFactor, double growthBoost) {
        List<String> months = List.of("2025-10", "2025-11", "2025-12");
        int base = 28000 + Math.abs(categoryName.hashCode() % 12000);
        for (int i = 0; i < months.size(); i++) {
            CategoryTrend row = new CategoryTrend();
            row.setCategoryName(categoryName);
            row.setPlatform(platform);
            row.setTrendMonth(months.get(i));
            int volume = (int) ((base + i * 1800) * volumeFactor);
            row.setSearchVolume(Math.max(3000, volume));
            row.setSalesVolume(Math.max(800, volume / 4));
            row.setGrowthRate(BigDecimal.valueOf(18 + i * 4.5 + growthBoost));
            row.setSocialHeat(5200 + i * 600 + (int) (growthBoost * 120));
            row.setRisingWords(platform + "场景词,增长词");
            categoryTrendMapper.insert(row);
        }
    }

    private void seedCompetition(String categoryName) {
        if (hasAnyCompetition(categoryName)) {
            return;
        }
        insertCompetitionRow(categoryName, "全平台", 0, 0);
    }

    private void seedPlatformCompetitionIfMissing(String categoryName, String platform) {
        if (existsCompetition(categoryName, platform)) {
            return;
        }
        int platformOffset = switch (platform) {
            case "天猫" -> 1;
            case "抖音" -> -2;
            case "小红书" -> -3;
            default -> 0;
        };
        insertCompetitionRow(categoryName, platform, platformOffset, platformOffset);
    }

    private void insertCompetitionRow(String categoryName, String platform, int cr3Offset, int cr5Offset) {
        CompetitionData row = new CompetitionData();
        row.setCategoryName(categoryName);
        row.setPlatform(platform);
        row.setTotalSearchVolume(32000 + Math.abs(categoryName.hashCode() % 8000));
        row.setTotalSkuCount(1200 + Math.abs(categoryName.hashCode() % 500));
        row.setTop10SalesRatio(BigDecimal.valueOf(38));
        row.setCr3(BigDecimal.valueOf(Math.max(8, 20 + cr3Offset)));
        row.setCr5(BigDecimal.valueOf(Math.max(12, 32 + cr5Offset)));
        row.setHomogeneityScore(BigDecimal.valueOf(48));
        row.setConclusion(categoryName + " 在 " + platform + " 可通过场景细分建立差异（内置样例）。");
        competitionDataMapper.insert(row);
    }

    private void seedSupply(String categoryName) {
        if (hasAnySupply(categoryName)) {
            return;
        }
        insertSupplyBands(categoryName, "全平台", 1.0);
    }

    private void seedPlatformSupplyIfMissing(String categoryName, String platform) {
        if (existsSupply(categoryName, platform)) {
            return;
        }
        double ratioFactor = switch (platform) {
            case "天猫" -> 0.95;
            case "抖音" -> 1.15;
            case "小红书" -> 1.05;
            default -> 1.0;
        };
        insertSupplyBands(categoryName, platform, ratioFactor);
    }

    private void insertSupplyBands(String categoryName, String platform, double ratioFactor) {
        SupplyDemand low = new SupplyDemand();
        low.setCategoryName(categoryName);
        low.setPlatform(platform);
        low.setPriceRange("80-150元");
        low.setSearchVolume(18000 + Math.abs(categoryName.hashCode() % 4000));
        low.setSupplyCount(420);
        low.setDemandSupplyRatio(BigDecimal.valueOf(42.8 * ratioFactor).setScale(2, java.math.RoundingMode.HALF_UP));
        supplyDemandMapper.insert(low);

        SupplyDemand mid = new SupplyDemand();
        mid.setCategoryName(categoryName);
        mid.setPlatform(platform);
        mid.setPriceRange("150-250元");
        mid.setSearchVolume(22000 + Math.abs(categoryName.hashCode() % 3000));
        mid.setSupplyCount(280);
        mid.setDemandSupplyRatio(BigDecimal.valueOf(78.5 * ratioFactor).setScale(2, java.math.RoundingMode.HALF_UP));
        supplyDemandMapper.insert(mid);
    }

    private void seedOpportunity(InsightCard card, CategoryUniverseCatalog.CategoryProfile profile) {
        Long count = opportunityMapper.selectCount(new LambdaQueryWrapper<Opportunity>()
                .eq(Opportunity::getInsightCardId, card.getId()));
        if (count != null && count > 0) {
            return;
        }
        String variant = profile.productVariants().isEmpty() ? profile.categoryName() : profile.productVariants().get(0);
        Opportunity point = new Opportunity();
        point.setInsightCardId(card.getId());
        point.setCategoryName(card.getCategoryName());
        point.setOpportunityGravity(BigDecimal.valueOf(72));
        point.setCompetitionResistance(BigDecimal.valueOf(38));
        point.setProfitElasticity(BigDecimal.valueOf(55));
        point.setOpportunityScore(78);
        point.setOpportunityLevel("高");
        point.setTargetCrowd("核心目标人群");
        point.setScenarioText(variant + " 高频使用场景");
        point.setDifferentiation("围绕 " + profile.priceGap() + " 做体验差异化");
        point.setMarketEstimate("首年可验证 800-1500 万 GMV（样例口径）");
        point.setEntryTiming("需求成长期，竞争尚未完全饱和");
        point.setLifecycleStage("成长期");
        point.setDecision(profile.recommendation().contains("放弃") ? "建议放弃" : "推荐立项");
        point.setReason(profile.recommendation());
        opportunityMapper.insert(point);
    }

    private boolean hasAnyTrend(String categoryName) {
        Long count = categoryTrendMapper.selectCount(new LambdaQueryWrapper<CategoryTrend>()
                .eq(CategoryTrend::getCategoryName, categoryName));
        return count != null && count > 0;
    }

    private boolean existsTrend(String categoryName, String platform) {
        Long count = categoryTrendMapper.selectCount(new LambdaQueryWrapper<CategoryTrend>()
                .eq(CategoryTrend::getCategoryName, categoryName)
                .eq(CategoryTrend::getPlatform, platform));
        return count != null && count > 0;
    }

    private boolean hasAnyCompetition(String categoryName) {
        Long count = competitionDataMapper.selectCount(new LambdaQueryWrapper<CompetitionData>()
                .eq(CompetitionData::getCategoryName, categoryName));
        return count != null && count > 0;
    }

    private boolean existsCompetition(String categoryName, String platform) {
        Long count = competitionDataMapper.selectCount(new LambdaQueryWrapper<CompetitionData>()
                .eq(CompetitionData::getCategoryName, categoryName)
                .eq(CompetitionData::getPlatform, platform));
        return count != null && count > 0;
    }

    private boolean hasAnySupply(String categoryName) {
        Long count = supplyDemandMapper.selectCount(new LambdaQueryWrapper<SupplyDemand>()
                .eq(SupplyDemand::getCategoryName, categoryName));
        return count != null && count > 0;
    }

    private boolean existsSupply(String categoryName, String platform) {
        Long count = supplyDemandMapper.selectCount(new LambdaQueryWrapper<SupplyDemand>()
                .eq(SupplyDemand::getCategoryName, categoryName)
                .eq(SupplyDemand::getPlatform, platform));
        return count != null && count > 0;
    }
}
