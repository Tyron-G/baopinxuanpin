package com.oneaix.selection.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oneaix.selection.constant.ApiConstants;
import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.entity.CompetitorShopEntity;
import com.oneaix.selection.mapper.CompetitorShopMapper;
import com.oneaix.selection.service.competitor.CompetitorShopConverter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** H2 竞品店铺仓储 2026-06-04 */
@Repository
public class JdbcCompetitorShopRepository implements CompetitorShopRepository {

    private final CompetitorShopMapper competitorShopMapper;
    private final CompetitorShopConverter converter;

    public JdbcCompetitorShopRepository(CompetitorShopMapper competitorShopMapper, CompetitorShopConverter converter) {
        this.competitorShopMapper = competitorShopMapper;
        this.converter = converter;
    }

    @Override
    public List<CompetitorShop> listAll(Long brandId, List<CompetitorShop> catalogFallback) {
        List<CompetitorShop> builtins = resolveBuiltins(brandId, catalogFallback);
        List<CompetitorShopEntity> userRows = competitorShopMapper.selectList(new LambdaQueryWrapper<CompetitorShopEntity>()
                .eq(CompetitorShopEntity::getBrandId, brandId)
                .eq(CompetitorShopEntity::getBuiltinSeed, false)
                .orderByDesc(CompetitorShopEntity::getId));
        if (userRows.isEmpty()) {
            return builtins;
        }
        return mergeBuiltinsWithUser(builtins, userRows.stream().map(converter::toDto).toList());
    }

    @Override
    public Optional<CompetitorShop> findDuplicate(Long brandId, String shopName, String platform, String focusCategory) {
        CompetitorShopEntity entity = competitorShopMapper.selectOne(new LambdaQueryWrapper<CompetitorShopEntity>()
                .eq(CompetitorShopEntity::getBrandId, brandId)
                .eq(CompetitorShopEntity::getShopName, shopName)
                .eq(CompetitorShopEntity::getPlatform, platform)
                .eq(CompetitorShopEntity::getFocusCategory, focusCategory)
                .last("LIMIT 1"));
        return Optional.ofNullable(entity).map(converter::toDto);
    }

    @Override
    public CompetitorShop save(Long brandId, CompetitorShop shop) {
        CompetitorShopEntity entity = converter.toEntity(brandId, shop, false);
        competitorShopMapper.insert(entity);
        return converter.toDto(entity);
    }

    /** 任意品牌均合并类目库内置竞品（H2 种子优先，无库时回退内存样例）2026-06-04 */
    private List<CompetitorShop> resolveBuiltins(Long brandId, List<CompetitorShop> catalogFallback) {
        List<CompetitorShopEntity> seeded = competitorShopMapper.selectList(new LambdaQueryWrapper<CompetitorShopEntity>()
                .eq(CompetitorShopEntity::getBrandId, ApiConstants.CATALOG_BRAND_ID)
                .eq(CompetitorShopEntity::getBuiltinSeed, true)
                .orderByAsc(CompetitorShopEntity::getId));
        if (!seeded.isEmpty()) {
            return seeded.stream().map(converter::toDto).toList();
        }
        return new ArrayList<>(catalogFallback);
    }

    private List<CompetitorShop> mergeBuiltinsWithUser(List<CompetitorShop> builtins, List<CompetitorShop> userShops) {
        List<CompetitorShop> ordered = new ArrayList<>(userShops);
        for (CompetitorShop builtin : builtins) {
            if (userShops.stream().noneMatch(shop -> shopKey(shop).equals(shopKey(builtin)))) {
                ordered.add(builtin);
            }
        }
        return ordered;
    }

    private String shopKey(CompetitorShop shop) {
        return shop.shopName() + "::" + shop.platform() + "::" + shop.focusCategory();
    }
}
