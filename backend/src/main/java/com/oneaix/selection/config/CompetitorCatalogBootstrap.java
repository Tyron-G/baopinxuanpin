package com.oneaix.selection.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oneaix.selection.constant.ApiConstants;
import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.entity.CompetitorShopEntity;
import com.oneaix.selection.mapper.CompetitorShopMapper;
import com.oneaix.selection.service.competitor.BuiltinCompetitorCatalog;
import com.oneaix.selection.service.competitor.CompetitorShopConverter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** 启动时补齐 10 品类内置竞品差评种子 2026-06-05 */
@Component
public class CompetitorCatalogBootstrap implements ApplicationRunner {

    private final CompetitorShopMapper competitorShopMapper;
    private final CompetitorShopConverter converter;

    public CompetitorCatalogBootstrap(
            CompetitorShopMapper competitorShopMapper,
            CompetitorShopConverter converter
    ) {
        this.competitorShopMapper = competitorShopMapper;
        this.converter = converter;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (CompetitorShop shop : BuiltinCompetitorCatalog.shops()) {
            Long count = competitorShopMapper.selectCount(new LambdaQueryWrapper<CompetitorShopEntity>()
                    .eq(CompetitorShopEntity::getBrandId, ApiConstants.CATALOG_BRAND_ID)
                    .eq(CompetitorShopEntity::getShopName, shop.shopName())
                    .eq(CompetitorShopEntity::getPlatform, shop.platform())
                    .eq(CompetitorShopEntity::getFocusCategory, shop.focusCategory()));
            if (count != null && count > 0) {
                continue;
            }
            CompetitorShopEntity entity = converter.toEntity(ApiConstants.CATALOG_BRAND_ID, shop, true);
            competitorShopMapper.insert(entity);
        }
    }
}
