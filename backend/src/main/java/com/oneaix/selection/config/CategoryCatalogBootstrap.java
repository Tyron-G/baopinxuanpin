package com.oneaix.selection.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oneaix.selection.constant.ApiConstants;
import com.oneaix.selection.content.CategoryUniverseCatalog;
import com.oneaix.selection.entity.CompetitionData;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.mapper.CompetitionDataMapper;
import com.oneaix.selection.mapper.InsightCardMapper;
import com.oneaix.selection.mapper.CategoryTrendMapper;
import com.oneaix.selection.entity.CategoryTrend;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/** 启动时补齐 10 品类洞察卡片与市场样例（迭代1 品类扩展）2026-06-04 */
@Component
public class CategoryCatalogBootstrap implements ApplicationRunner {

    private final InsightCardMapper insightCardMapper;
    private final CategoryTrendMapper categoryTrendMapper;
    private final CompetitionDataMapper competitionDataMapper;

    public CategoryCatalogBootstrap(
            InsightCardMapper insightCardMapper,
            CategoryTrendMapper categoryTrendMapper,
            CompetitionDataMapper competitionDataMapper
    ) {
        this.insightCardMapper = insightCardMapper;
        this.categoryTrendMapper = categoryTrendMapper;
        this.competitionDataMapper = competitionDataMapper;
    }

    @Override
    public void run(ApplicationArguments args) {
        long cardCount = insightCardMapper.selectCount(new LambdaQueryWrapper<InsightCard>()
                .eq(InsightCard::getBrandId, ApiConstants.CATALOG_BRAND_ID));
        if (cardCount >= CategoryUniverseCatalog.profiles().size()) {
            return;
        }
        for (CategoryUniverseCatalog.CategoryProfile profile : CategoryUniverseCatalog.profiles()) {
            if (existsCard(profile.categoryName())) {
                continue;
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
            seedTrend(profile.categoryName());
            seedCompetition(profile.categoryName());
        }
        seedCrossBorderPlatforms("跨境家居收纳");
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
            row.setSearchVolume(12000 + platform.hashCode() % 3000);
            row.setSalesVolume(2800);
            row.setGrowthRate(BigDecimal.valueOf(33.5));
            row.setSocialHeat(4100);
            row.setRisingWords("跨境小包,收纳六件套");
            categoryTrendMapper.insert(row);
        }
    }

    private boolean existsCard(String categoryName) {
        Long count = insightCardMapper.selectCount(new LambdaQueryWrapper<InsightCard>()
                .eq(InsightCard::getBrandId, ApiConstants.CATALOG_BRAND_ID)
                .eq(InsightCard::getCategoryName, categoryName));
        return count != null && count > 0;
    }

    private void seedTrend(String categoryName) {
        Long count = categoryTrendMapper.selectCount(new LambdaQueryWrapper<CategoryTrend>()
                .eq(CategoryTrend::getCategoryName, categoryName));
        if (count != null && count > 0) {
            return;
        }
        List<String> months = List.of("2025-10", "2025-11", "2025-12");
        int base = 28000 + categoryName.hashCode() % 12000;
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
        row.setTotalSearchVolume(32000 + categoryName.hashCode() % 8000);
        row.setTotalSkuCount(1200 + categoryName.hashCode() % 500);
        row.setTop10SalesRatio(BigDecimal.valueOf(38));
        row.setCr3(BigDecimal.valueOf(20));
        row.setCr5(BigDecimal.valueOf(32));
        row.setHomogeneityScore(BigDecimal.valueOf(48));
        row.setConclusion(categoryName + " 竞争格局中等，可通过场景细分建立差异。");
        competitionDataMapper.insert(row);
    }
}
