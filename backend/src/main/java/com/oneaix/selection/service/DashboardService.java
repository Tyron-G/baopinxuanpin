package com.oneaix.selection.service;

import com.oneaix.selection.dto.BrandSelectionContext;
import com.oneaix.selection.dto.DashboardSummary;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.WorkflowProgress;
import com.oneaix.selection.dto.WorkflowStage;
import com.oneaix.selection.entity.Opportunity;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.enums.WorkflowStageKey;
import com.oneaix.selection.service.catalog.InsightCardQueryService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/** 2026-06-05 仪表盘 KPI 聚合（平台口径） */
@Service
public class DashboardService {
    private final BrandSelectionContextLoader contextLoader;
    private final InsightCardQueryService cardQueryService;
    private final SignalRadarService signalRadarService;
    private final OpportunityService opportunityService;
    private final WatchlistService watchlistService;

    public DashboardService(
            BrandSelectionContextLoader contextLoader,
            InsightCardQueryService cardQueryService,
            SignalRadarService signalRadarService,
            OpportunityService opportunityService,
            WatchlistService watchlistService
    ) {
        this.contextLoader = contextLoader;
        this.cardQueryService = cardQueryService;
        this.signalRadarService = signalRadarService;
        this.opportunityService = opportunityService;
        this.watchlistService = watchlistService;
    }

    public DashboardSummary summary(Long brandId) {
        return summary(brandId, PlatformView.ALL.getLabel());
    }

    public DashboardSummary summary(Long brandId, String platform) {
        BrandSelectionContext context = contextLoader.load(brandId);
        String platformLabel = normalizePlatform(platform);
        List<InsightCardView> cards = cardQueryService.rankedViews(context.brand(), context.catalog(), platformLabel);
        int signalCount = signalRadarService.signals(brandId, platformLabel).size();

        if (cards.isEmpty()) {
            return new DashboardSummary(
                    0,
                    signalCount,
                    0,
                    "-",
                    null,
                    context.brand().getBrandName(),
                    watchlistService.count(brandId),
                    50
            );
        }

        InsightCardView top = cards.get(0);
        int bestScore = opportunityService.points(top.card().getId(), brandId, platformLabel).stream()
                .mapToInt(Opportunity::getOpportunityScore)
                .max()
                .orElse(0);

        return new DashboardSummary(
                cards.size(),
                signalCount,
                bestScore,
                top.card().getCategoryName(),
                top.card().getId(),
                context.brand().getBrandName(),
                watchlistService.count(brandId),
                50
        );
    }

    public WorkflowProgress workflow(Long brandId) {
        return workflow(brandId, PlatformView.ALL.getLabel());
    }

    public WorkflowProgress workflow(Long brandId, String platform) {
        BrandSelectionContext context = contextLoader.load(brandId);
        String platformLabel = normalizePlatform(platform);
        List<InsightCardView> cards = cardQueryService.rankedViews(context.brand(), context.catalog(), platformLabel);
        int signalCount = signalRadarService.signals(brandId, platformLabel).size();
        int insightCount = cards.size();
        int opportunityCount = cards.isEmpty()
                ? 0
                : opportunityService.points(cards.get(0).card().getId(), brandId, platformLabel).size();

        WorkflowStageKey currentStage = resolveCurrentStage(context.brand(), signalCount, insightCount, opportunityCount);
        WorkflowStageKey nextStage = currentStage.nextStage();

        return new WorkflowProgress(
                context.brand().getId(),
                context.brand().getBrandName(),
                currentStage.getKey(),
                nextStage.getKey(),
                Arrays.stream(WorkflowStageKey.values())
                        .map(stage -> new WorkflowStage(
                                stage.getKey(),
                                stage.getTitle(),
                                stage.statusComparedTo(currentStage),
                                stage.getSummary(),
                                stage.getNextAction()
                        ))
                        .toList(),
                signalCount,
                insightCount,
                opportunityCount,
                opportunityCount > 0
        );
    }

    private WorkflowStageKey resolveCurrentStage(
            com.oneaix.selection.entity.BrandInfo brand,
            int signalCount,
            int insightCount,
            int opportunityCount
    ) {
        boolean hasBrandContext = brand.getId() != null && brand.getId() > 1;
        if (!hasBrandContext || signalCount <= 0) {
            return WorkflowStageKey.DATA_PREP;
        }
        if (insightCount <= 0) {
            return WorkflowStageKey.RADAR;
        }
        if (opportunityCount <= 0) {
            return WorkflowStageKey.RANKING;
        }
        return WorkflowStageKey.OPPORTUNITY;
    }

    private String normalizePlatform(String platform) {
        if (platform == null || platform.isBlank()) {
            return PlatformView.ALL.getLabel();
        }
        return PlatformView.normalize(platform).getLabel();
    }
}
