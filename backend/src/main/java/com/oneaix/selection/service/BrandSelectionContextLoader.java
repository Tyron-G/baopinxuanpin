package com.oneaix.selection.service;

import com.oneaix.selection.config.RequestScopedSelectionContext;
import com.oneaix.selection.dto.BrandSelectionContext;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.service.catalog.InsightCardCatalogService;
import com.oneaix.selection.service.catalog.InsightCardQueryService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/** 品牌选品上下文加载器，请求内复用快照 2026-06-04 */
@Component
public class BrandSelectionContextLoader {
    private final InsightCardQueryService cardQueryService;
    private final InsightCardCatalogService catalogService;
    private final RequestScopedSelectionContext requestScope;

    public BrandSelectionContextLoader(
            InsightCardQueryService cardQueryService,
            InsightCardCatalogService catalogService,
            RequestScopedSelectionContext requestScope
    ) {
        this.cardQueryService = cardQueryService;
        this.catalogService = catalogService;
        this.requestScope = requestScope;
    }

    public BrandSelectionContext load(Long brandId) {
        return requestScope.getOrLoad(brandId, () -> {
            BrandInfo brand = cardQueryService.requireBrand(brandId);
            List<InsightCard> catalog = catalogService.loadCatalog();
            Set<String> visible = cardQueryService.visibleCategories(brand, catalog);
            List<InsightCardView> cards = cardQueryService.rankedViews(brand, catalog);
            return new BrandSelectionContext(brand, catalog, visible, cards);
        });
    }
}
