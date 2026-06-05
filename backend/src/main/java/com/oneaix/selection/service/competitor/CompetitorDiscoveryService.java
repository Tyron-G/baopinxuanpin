package com.oneaix.selection.service.competitor;

import com.oneaix.selection.dto.BrandSelectionContext;
import com.oneaix.selection.dto.CompetitorRequest;
import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.service.BrandSelectionContextLoader;
import com.oneaix.selection.service.CompetitorService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** 自动竞品发现（迭代1 P0）2026-06-04 */
@Service
public class CompetitorDiscoveryService {

    private final BrandSelectionContextLoader contextLoader;
    private final CompetitorService competitorService;

    public CompetitorDiscoveryService(
            BrandSelectionContextLoader contextLoader,
            CompetitorService competitorService
    ) {
        this.contextLoader = contextLoader;
        this.competitorService = competitorService;
    }

    public List<CompetitorShop> suggest(Long brandId) {
        BrandSelectionContext context = contextLoader.load(brandId);
        Set<String> existing = new LinkedHashSet<>();
        competitorService.list(brandId).forEach(shop -> existing.add(key(shop)));
        List<CompetitorShop> suggestions = new ArrayList<>();
        for (InsightCardView view : context.cards()) {
            String category = view.card().getCategoryName();
            for (String platform : PlatformView.parseCsv(context.brand().getTargetPlatforms())) {
                String shopName = category + "对标店·" + platform;
                CompetitorShop candidate = new CompetitorShop(
                        shopName,
                        platform,
                        category,
                        "系统识别：近30天有爆品上新",
                        PlatformView.normalize(platform).defaultGrowthSignal(),
                        "待添加",
                        view.card().getId(),
                        "auto-discover",
                        "自动发现",
                        "近14天上新 1-2 款",
                        2,
                        List.of("同质化", "价格竞争"),
                        List.of("自动发现", "建议跟踪")
                );
                if (!existing.contains(key(candidate))) {
                    suggestions.add(candidate);
                }
            }
        }
        return suggestions.stream().limit(12).toList();
    }

    public List<CompetitorShop> discoverAndAdd(Long brandId) {
        List<CompetitorShop> added = new ArrayList<>();
        for (CompetitorShop suggestion : suggest(brandId)) {
            CompetitorShop saved = competitorService.add(brandId, new CompetitorRequest(
                    suggestion.shopName(),
                    suggestion.platform(),
                    suggestion.focusCategory(),
                    suggestion.cardId(),
                    suggestion.sourceSignalId(),
                    suggestion.sourceSignalType()
            ));
            added.add(saved);
        }
        return added;
    }

    private String key(CompetitorShop shop) {
        return shop.shopName() + "|" + shop.platform() + "|" + shop.focusCategory();
    }
}
