package com.oneaix.selection.service;

import com.oneaix.selection.annotation.TrackedExecution;
import com.oneaix.selection.dto.BrandSelectionContext;
import com.oneaix.selection.dto.CompetitorRequest;
import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.dto.CompetitorTimeline;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.repository.CompetitorShopRepository;
import com.oneaix.selection.service.competitor.BuiltinCompetitorCatalog;
import com.oneaix.selection.service.competitor.CompetitorCategoryInsights;
import com.oneaix.selection.service.competitor.CompetitorTimelineBuilder;
import com.oneaix.selection.service.competitor.CompetitorTimelinePointSeeder;
import com.oneaix.selection.util.CategoryNameMatcher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** 竞品跟踪服务（H2 持久化，按品牌查询与去重）2026-06-04 */
@Service
public class CompetitorService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final BrandSelectionContextLoader contextLoader;
    private final CompetitorShopRepository competitorShopRepository;
    private final CompetitorTimelineBuilder timelineBuilder;
    private final CompetitorTimelinePointSeeder timelinePointSeeder;

    public CompetitorService(
            BrandSelectionContextLoader contextLoader,
            CompetitorShopRepository competitorShopRepository,
            CompetitorTimelineBuilder timelineBuilder,
            CompetitorTimelinePointSeeder timelinePointSeeder
    ) {
        this.contextLoader = contextLoader;
        this.competitorShopRepository = competitorShopRepository;
        this.timelineBuilder = timelineBuilder;
        this.timelinePointSeeder = timelinePointSeeder;
    }

    public List<CompetitorShop> list(Long brandId) {
        return competitorShopRepository.listAll(brandId, BuiltinCompetitorCatalog.shops());
    }

    public List<CompetitorShop> relatedTo(Long brandId, Long cardId, String focusCategory) {
        return list(brandId).stream()
                .filter(shop -> {
                    boolean cardMatched = cardId != null && cardId.equals(shop.cardId());
                    return cardMatched || CategoryNameMatcher.matches(shop.focusCategory(), focusCategory);
                })
                .limit(3)
                .toList();
    }

    @TrackedExecution(value = "competitor-timelines", domain = "competitor")
    public List<CompetitorTimeline> timelines(Long brandId, String category, String platform) {
        return timelineBuilder.buildAll(list(brandId), category, platform);
    }

    @Transactional
    @TrackedExecution(value = "competitor-add", domain = "competitor")
    public CompetitorShop add(Long brandId, CompetitorRequest request) {
        String focusCategory = request.focusCategory() == null || request.focusCategory().isBlank()
                ? "未指定品类"
                : request.focusCategory();
        BrandSelectionContext context = contextLoader.load(brandId);
        List<InsightCardView> cards = context.cards();
        Long resolvedCardId = CategoryNameMatcher.resolveCardId(focusCategory, cards, request.cardId()).orElse(null);
        var duplicate = competitorShopRepository.findDuplicate(
                brandId, request.shopName(), request.platform(), focusCategory);
        if (duplicate.isPresent()) {
            return duplicate.get();
        }
        InsightCardView linkedView = CategoryNameMatcher.resolveCardView(resolvedCardId, focusCategory, cards).orElse(null);
        CompetitorShop shop = new CompetitorShop(
                request.shopName(),
                request.platform(),
                focusCategory,
                CompetitorCategoryInsights.latestHitSummary(focusCategory),
                growthSignalSummary(request.platform(), request.sourceSignalType()),
                LocalDateTime.now().format(FORMATTER),
                resolvedCardId,
                request.sourceSignalId(),
                request.sourceSignalType(),
                CompetitorCategoryInsights.recentLaunchSummary(focusCategory),
                CompetitorCategoryInsights.estimatedHitProductCount(focusCategory),
                CompetitorCategoryInsights.typicalComplaintTopics(focusCategory),
                buildOpportunityTags(linkedView, request.sourceSignalType())
        );
        CompetitorShop saved;
        try {
            saved = competitorShopRepository.save(brandId, shop);
        } catch (DuplicateKeyException ex) {
            return competitorShopRepository.findDuplicate(
                    brandId, request.shopName(), request.platform(), focusCategory
            ).orElseThrow(() -> ex);
        }
        timelinePointSeeder.seedIfAbsent(saved);
        return saved;
    }

    private String growthSignalSummary(String platform, String sourceSignalType) {
        if (sourceSignalType != null && !sourceSignalType.isBlank()) {
            return "来自信号雷达：" + sourceSignalType + "，建议继续跟踪上新和转化变化";
        }
        return PlatformView.normalize(platform).defaultGrowthSignal();
    }

    private List<String> buildOpportunityTags(InsightCardView view, String sourceSignalType) {
        List<String> tags = new ArrayList<>();
        if (view != null) {
            tags.addAll(view.matchTags());
            tags.add(view.decision());
        }
        if (sourceSignalType != null && !sourceSignalType.isBlank()) {
            tags.add(sourceSignalType);
        }
        if (tags.isEmpty()) {
            tags.add("待验证");
        }
        return tags.stream().distinct().limit(4).toList();
    }

}
