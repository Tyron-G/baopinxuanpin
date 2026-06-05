package com.oneaix.selection.service.catalog;

import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.service.BrandService;
import com.oneaix.selection.service.constraint.BrandConstraintEvaluator;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.service.insight.InsightCardHomogeneityEnricher;
import com.oneaix.selection.service.insight.InsightCardMarketGrowthEnricher;
import com.oneaix.selection.service.insight.InsightMarketDataService;
import com.oneaix.selection.service.insight.InsightViewAssembler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** 品牌约束下的洞察卡片视图查询 2026-06-04 */
@Service
public class InsightCardQueryService {

    private final BrandService brandService;
    private final InsightCardCatalogService catalogService;
    private final BrandConstraintEvaluator constraintEvaluator;
    private final InsightViewAssembler viewAssembler;
    private final InsightMarketDataService marketDataService;
    private final InsightCardMarketGrowthEnricher marketGrowthEnricher;
    private final InsightCardHomogeneityEnricher homogeneityEnricher;

    public InsightCardQueryService(
            BrandService brandService,
            InsightCardCatalogService catalogService,
            BrandConstraintEvaluator constraintEvaluator,
            InsightViewAssembler viewAssembler,
            InsightMarketDataService marketDataService,
            InsightCardMarketGrowthEnricher marketGrowthEnricher,
            InsightCardHomogeneityEnricher homogeneityEnricher
    ) {
        this.brandService = brandService;
        this.catalogService = catalogService;
        this.constraintEvaluator = constraintEvaluator;
        this.viewAssembler = viewAssembler;
        this.marketDataService = marketDataService;
        this.marketGrowthEnricher = marketGrowthEnricher;
        this.homogeneityEnricher = homogeneityEnricher;
    }

    public BrandInfo requireBrand(Long brandId) {
        return brandService.requireById(brandId);
    }

    public List<InsightCard> loadCatalog() {
        return catalogService.loadCatalog();
    }

    public List<InsightCardView> rankedViews(Long brandId) {
        BrandInfo brand = requireBrand(brandId);
        return rankedViews(brand, loadCatalog());
    }

    public List<InsightCardView> rankedViews(BrandInfo brand, List<InsightCard> catalog) {
        return rankedViews(brand, catalog, PlatformView.ALL.getLabel());
    }

    public List<InsightCardView> rankedViews(BrandInfo brand, List<InsightCard> catalog, String platform) {
        Set<String> visible = visibleCategories(brand, catalog);
        List<com.oneaix.selection.entity.CategoryTrend> trends = marketDataService.trends(visible);
        catalog.stream()
                .filter(card -> visible.contains(card.getCategoryName()))
                .forEach(card -> marketGrowthEnricher.applyOne(card, trends, platform));
        List<InsightCard> ranked = constraintEvaluator.filterAndRankCards(brand, catalog);
        return homogeneityEnricher.enrich(
                viewAssembler.toViews(brand, ranked),
                visible,
                platform
        );
    }

    public Set<String> visibleCategories(BrandInfo brand, List<InsightCard> catalog) {
        List<String> allNames = catalog.stream()
                .map(InsightCard::getCategoryName)
                .distinct()
                .toList();
        return constraintEvaluator.resolveVisibleCategories(brand, allNames);
    }

    public Set<String> visibleCategories(Long brandId) {
        BrandInfo brand = requireBrand(brandId);
        return visibleCategories(brand, loadCatalog());
    }

    public List<String> filteredCategoryNames(List<InsightCard> catalog, Set<String> visible) {
        return catalog.stream()
                .map(InsightCard::getCategoryName)
                .filter(category -> !visible.contains(category))
                .distinct()
                .collect(Collectors.toList());
    }
}
