package com.oneaix.selection.config;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.oneaix.selection.constant.ApiConstants;

import com.oneaix.selection.content.CategoryUniverseCatalog;

import com.oneaix.selection.entity.CompetitionData;

import com.oneaix.selection.entity.InsightCard;

import com.oneaix.selection.entity.Opportunity;

import com.oneaix.selection.entity.SupplyDemand;

import com.oneaix.selection.mapper.CompetitionDataMapper;

import com.oneaix.selection.mapper.InsightCardMapper;

import com.oneaix.selection.mapper.CategoryTrendMapper;

import com.oneaix.selection.mapper.OpportunityMapper;

import com.oneaix.selection.mapper.SupplyDemandMapper;

import com.oneaix.selection.entity.CategoryTrend;

import org.springframework.boot.ApplicationArguments;

import org.springframework.boot.ApplicationRunner;

import org.springframework.stereotype.Component;



import java.math.BigDecimal;

import java.util.List;



/** 启动时补齐 10 品类洞察卡片与市场/机会样例（迭代1 品类扩展）2026-06-05 */

@Component

public class CategoryCatalogBootstrap implements ApplicationRunner {



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

        long cardCount = insightCardMapper.selectCount(new LambdaQueryWrapper<InsightCard>()

                .eq(InsightCard::getBrandId, ApiConstants.CATALOG_BRAND_ID));

        if (cardCount >= CategoryUniverseCatalog.profiles().size()) {

            seedMissingMarketAndOpportunityData();

            return;

        }

        for (CategoryUniverseCatalog.CategoryProfile profile : CategoryUniverseCatalog.profiles()) {

            InsightCard card = findOrCreateCard(profile);

            seedTrend(profile.categoryName());

            seedCompetition(profile.categoryName());

            seedSupply(profile.categoryName());

            seedOpportunity(card, profile);

        }

        seedCrossBorderPlatforms("跨境家居收纳");

    }



    private void seedMissingMarketAndOpportunityData() {

        for (CategoryUniverseCatalog.CategoryProfile profile : CategoryUniverseCatalog.profiles()) {

            InsightCard card = insightCardMapper.selectOne(new LambdaQueryWrapper<InsightCard>()

                    .eq(InsightCard::getBrandId, ApiConstants.CATALOG_BRAND_ID)

                    .eq(InsightCard::getCategoryName, profile.categoryName())

                    .last("LIMIT 1"));

            if (card == null) {

                continue;

            }

            seedSupply(profile.categoryName());

            seedOpportunity(card, profile);

        }

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



    private void seedCrossBorderPlatforms(String categoryName) {

        if (!"跨境家居收纳".equals(categoryName)) {

            return;

        }

        for (String platform : List.of("亚马逊", "Shopee", "TikTok Shop")) {

            if (categoryTrendMapper.selectCount(new LambdaQueryWrapper<CategoryTrend>()

                    .eq(CategoryTrend::getCategoryName, categoryName)

                    .eq(CategoryTrend::getPlatform, platform)) > 0) {

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

        Long count = categoryTrendMapper.selectCount(new LambdaQueryWrapper<CategoryTrend>()

                .eq(CategoryTrend::getCategoryName, categoryName));

        if (count != null && count > 0) {

            return;

        }

        List<String> months = List.of("2025-10", "2025-11", "2025-12");

        int base = 28000 + Math.abs(categoryName.hashCode() % 12000);

        for (int i = 0; i < months.size(); i++) {

            CategoryTrend row = new CategoryTrend();

            row.setCategoryName(categoryName);

            row.setPlatform("全平台");

            row.setTrendMonth(months.get(i));

            row.setSearchVolume(base + i * 1800);

            row.setSalesVolume((base + i * 1800) / 4);

            row.setGrowthRate(BigDecimal.valueOf(18 + i * 4.5));

            row.setSocialHeat(5200 + i * 600);

            row.setRisingWords("场景词,增长词");

            categoryTrendMapper.insert(row);

        }

    }



    private void seedCompetition(String categoryName) {

        Long count = competitionDataMapper.selectCount(new LambdaQueryWrapper<CompetitionData>()

                .eq(CompetitionData::getCategoryName, categoryName));

        if (count != null && count > 0) {

            return;

        }

        CompetitionData row = new CompetitionData();

        row.setCategoryName(categoryName);

        row.setPlatform("全平台");

        row.setTotalSearchVolume(32000 + Math.abs(categoryName.hashCode() % 8000));

        row.setTotalSkuCount(1200 + Math.abs(categoryName.hashCode() % 500));

        row.setTop10SalesRatio(BigDecimal.valueOf(38));

        row.setCr3(BigDecimal.valueOf(20));

        row.setCr5(BigDecimal.valueOf(32));

        row.setHomogeneityScore(BigDecimal.valueOf(48));

        row.setConclusion(categoryName + " 竞争格局中等，可通过场景细分建立差异。");

        competitionDataMapper.insert(row);

    }



    private void seedSupply(String categoryName) {

        Long count = supplyDemandMapper.selectCount(new LambdaQueryWrapper<SupplyDemand>()

                .eq(SupplyDemand::getCategoryName, categoryName));

        if (count != null && count > 0) {

            return;

        }

        SupplyDemand low = new SupplyDemand();

        low.setCategoryName(categoryName);

        low.setPlatform("全平台");

        low.setPriceRange("80-150元");

        low.setSearchVolume(18000 + Math.abs(categoryName.hashCode() % 4000));

        low.setSupplyCount(420);

        low.setDemandSupplyRatio(BigDecimal.valueOf(42.8));

        supplyDemandMapper.insert(low);



        SupplyDemand mid = new SupplyDemand();

        mid.setCategoryName(categoryName);

        mid.setPlatform("全平台");

        mid.setPriceRange("150-250元");

        mid.setSearchVolume(22000 + Math.abs(categoryName.hashCode() % 3000));

        mid.setSupplyCount(280);

        mid.setDemandSupplyRatio(BigDecimal.valueOf(78.5));

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

}

