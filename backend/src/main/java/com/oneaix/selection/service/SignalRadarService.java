package com.oneaix.selection.service;

import com.oneaix.selection.content.SignalTemplate;
import com.oneaix.selection.content.SignalTemplateCatalog;
import com.oneaix.selection.dto.BrandSelectionContext;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.SignalItem;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.enums.SignalStrength;
import com.oneaix.selection.enums.SignalType;
import com.oneaix.selection.service.catalog.InsightCardQueryService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 爆品信号雷达服务 2026-06-04 */
@Service
public class SignalRadarService {
    private final BrandSelectionContextLoader contextLoader;
    private final InsightCardQueryService cardQueryService;
    private final SignalTemplateCatalog signalTemplateCatalog;

    public SignalRadarService(
            @Lazy BrandSelectionContextLoader contextLoader,
            InsightCardQueryService cardQueryService,
            SignalTemplateCatalog signalTemplateCatalog
    ) {
        this.contextLoader = contextLoader;
        this.cardQueryService = cardQueryService;
        this.signalTemplateCatalog = signalTemplateCatalog;
    }

    public List<SignalItem> signals(Long brandId) {
        return signals(brandId, PlatformView.ALL.getLabel());
    }

    /** 按平台口径 enrich 卡片增速后再生成信号 2026-06-05 */
    public List<SignalItem> signals(Long brandId, String platform) {
        BrandSelectionContext context = contextLoader.load(brandId);
        List<InsightCardView> cards = cardQueryService.rankedViews(
                context.brand(),
                context.catalog(),
                platform
        );
        return buildSignals(context.brand(), cards);
    }

    /** 由上下文加载器调用，避免 load → signals → load 递归 2026-06-04 */
    public List<SignalItem> buildSignals(BrandInfo brand, List<InsightCardView> cards) {
        Map<String, Long> cardIdByCategory = new LinkedHashMap<>();
        Map<String, InsightCardView> cardViewByCategory = new LinkedHashMap<>();
        for (InsightCardView view : cards) {
            String category = view.card().getCategoryName();
            cardIdByCategory.put(category, view.card().getId());
            cardViewByCategory.put(category, view);
        }
        java.util.Set<String> visibleCategories = cardIdByCategory.keySet();

        List<SignalItem> items = new ArrayList<>();
        for (SignalTemplate template : signalTemplateCatalog.templatesFor(brand)) {
            if (!visibleCategories.contains(template.categoryName())) {
                continue;
            }
            items.add(toSignalItem(template, cardIdByCategory, cardViewByCategory));
        }

        for (InsightCardView view : cards) {
            items.add(categorySignal(view, brand));
        }

        return items.stream()
                .filter(item -> visibleCategories.contains(item.categoryName()))
                .sorted(Comparator.comparingInt(SignalItem::score).reversed())
                .toList();
    }

    private SignalItem toSignalItem(
            SignalTemplate template,
            Map<String, Long> cardIdByCategory,
            Map<String, InsightCardView> cardViewByCategory
    ) {
        InsightCardView view = cardViewByCategory.get(template.categoryName());
        return signal(
                template.id(),
                template.categoryName(),
                template.signalType(),
                template.strength(),
                template.score(),
                template.confidence(),
                template.platform(),
                template.metric(),
                template.summary(),
                template.discoveredAt(),
                cardIdByCategory.get(template.categoryName()),
                template.recommendedAction(),
                template.reasonTags(),
                view
        );
    }

    private SignalItem categorySignal(InsightCardView view, BrandInfo brand) {
        int score = view.scoreBreakdown().totalScore();
        SignalStrength strength = SignalStrength.fromScore(score);
        List<String> platformLabels = PlatformView.parseCsv(brand.getTargetPlatforms());
        PlatformView platform = platformLabels.isEmpty()
                ? PlatformView.TMALL
                : PlatformView.normalize(platformLabels.get(0));
        return signal(
                "sig-card-" + view.card().getId(),
                view.card().getCategoryName(),
                SignalType.OPPORTUNITY_SCORE,
                strength,
                score,
                view.scoreBreakdown().confidence(),
                platform,
                view.card().getMarketGrowth(),
                view.card().getRecommendation(),
                "刚刚更新",
                view.card().getId(),
                view.decision(),
                view.reasons().stream().map(reason -> reason.title()).toList(),
                view
        );
    }

    private SignalItem signal(
            String id,
            String categoryName,
            SignalType signalType,
            SignalStrength strength,
            int score,
            int confidence,
            PlatformView platform,
            String metric,
            String summary,
            String discoveredAt,
            Long cardId,
            String recommendedAction,
            List<String> reasonTags,
            InsightCardView view
    ) {
        String decision = view == null ? recommendedAction : view.decision();
        return new SignalItem(
                id,
                categoryName,
                signalType.getLabel(),
                strength.getLabel(),
                score,
                confidence,
                platform.getLabel(),
                metric,
                summary,
                discoveredAt,
                cardId,
                recommendedAction,
                reasonTags,
                decision,
                view == null ? null : view.brandFitDetail(),
                view == null ? List.of() : view.reasons(),
                view == null ? List.of() : view.risks(),
                view == null ? List.of() : view.mismatches()
        );
    }
}
