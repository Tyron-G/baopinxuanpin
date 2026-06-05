package com.oneaix.selection.repository;

import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.service.competitor.BuiltinCompetitorCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 2026-06-04 竞品 JDBC 仓储 */
@SpringBootTest
@Transactional
class JdbcCompetitorShopRepositoryTest {

    @Autowired
    private CompetitorShopRepository competitorShopRepository;

    @Test
    void shouldLoadBuiltinSeedsForCatalogBrand() {
        List<CompetitorShop> shops = competitorShopRepository.listAll(1L, BuiltinCompetitorCatalog.shops());
        assertTrue(shops.size() >= 2);
        assertTrue(shops.stream().anyMatch(shop -> "小佩宠物旗舰店".equals(shop.shopName())));
    }

    @Test
    void shouldExposeBuiltinSeedsForNewBrand() {
        List<CompetitorShop> shops = competitorShopRepository.listAll(88L, BuiltinCompetitorCatalog.shops());
        assertTrue(shops.size() >= 2);
        assertTrue(shops.stream().anyMatch(shop -> "九阳便携厨电".equals(shop.shopName())));
    }

    @Test
    void shouldPersistUserAddedShop() {
        CompetitorShop custom = new CompetitorShop(
                "测试店",
                PlatformView.TMALL.getLabel(),
                "宠物智能用品",
                "摘要",
                "增长",
                "2026-06-04 10:00",
                1L,
                "custom-001",
                "搜索飙升",
                "上新说明",
                1,
                List.of("痛点"),
                List.of("标签")
        );
        competitorShopRepository.save(99L, custom);
        List<CompetitorShop> shops = competitorShopRepository.listAll(99L, BuiltinCompetitorCatalog.shops());
        assertTrue(shops.stream().anyMatch(shop -> "测试店".equals(shop.shopName())));
    }
}
