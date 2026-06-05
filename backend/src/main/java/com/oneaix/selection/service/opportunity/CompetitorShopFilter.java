package com.oneaix.selection.service.opportunity;

import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.enums.PlatformView;

import java.util.List;

/** 竞品列表按平台视角过滤 2026-06-04 */
public final class CompetitorShopFilter {

    private CompetitorShopFilter() {
    }

    public static List<CompetitorShop> filterByPlatform(List<CompetitorShop> shops, String platformView) {
        PlatformView platform = PlatformView.normalize(platformView);
        if (platform.isAll()) {
            return shops;
        }
        List<CompetitorShop> matched = shops.stream()
                .filter(shop -> platform.getLabel().equals(shop.platform()))
                .toList();
        return matched.isEmpty() ? shops : matched;
    }
}
