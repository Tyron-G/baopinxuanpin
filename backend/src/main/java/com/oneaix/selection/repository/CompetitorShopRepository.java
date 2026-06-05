package com.oneaix.selection.repository;

import com.oneaix.selection.dto.CompetitorShop;

import java.util.List;
import java.util.Optional;

/** 竞品店铺仓储 2026-06-04 */
public interface CompetitorShopRepository {

    List<CompetitorShop> listAll(Long brandId, List<CompetitorShop> catalogFallback);

    Optional<CompetitorShop> findDuplicate(Long brandId, String shopName, String platform, String focusCategory);

    CompetitorShop save(Long brandId, CompetitorShop shop);
}
