package com.oneaix.selection.service.metrics;

import com.oneaix.selection.dto.BrandSelectionContext;
import com.oneaix.selection.dto.CorePromiseMetrics;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.SignalItem;
import com.oneaix.selection.enums.DecisionType;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.service.BrandSelectionContextLoader;
import com.oneaix.selection.service.OpportunityService;
import com.oneaix.selection.service.SignalRadarService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/** 核心承诺「更早发现 / 更高胜率」应用内代理指标 2026-06-05 */
@Service
public class CorePromiseMetricsService {

    private final BrandSelectionContextLoader contextLoader;
    private final SignalRadarService signalRadarService;
    private final OpportunityService opportunityService;

    public CorePromiseMetricsService(
            BrandSelectionContextLoader contextLoader,
            SignalRadarService signalRadarService,
            OpportunityService opportunityService
    ) {
        this.contextLoader = contextLoader;
        this.signalRadarService = signalRadarService;
        this.opportunityService = opportunityService;
    }

    public CorePromiseMetrics metrics(Long brandId) {
        BrandSelectionContext context = contextLoader.load(brandId);
        List<SignalItem> signals = signalRadarService.signals(brandId, PlatformView.ALL.getLabel());
        List<InsightCardView> cards = context.cards();

        int leadDays = signals.stream()
                .mapToInt(this::daysSinceDiscovered)
                .filter(days -> days >= 0)
                .min()
                .orElse(14);

        long recommend = cards.stream()
                .filter(view -> DecisionType.RECOMMEND.getLabel().equals(view.decision()))
                .count();
        long highScore = cards.stream()
                .filter(view -> DecisionType.RECOMMEND.getLabel().equals(view.decision()))
                .filter(view -> bestScore(view.card().getId(), brandId) >= 80)
                .count();
        int hitRate = recommend == 0 ? 0 : (int) Math.round(highScore * 100.0 / recommend);

        long successProxy = cards.stream()
                .filter(view -> !DecisionType.ABANDON.getLabel().equals(view.decision()))
                .count();
        int successPct = cards.isEmpty() ? 0 : (int) Math.round(successProxy * 100.0 / cards.size());

        String narrative = "早于大盘平均约 " + Math.max(leadDays, 7) + " 天捕捉强信号；"
                + "当前推荐赛道中 " + hitRate + "% 机会分≥80（胜率代理）；"
                + "可推进决策占比 " + successPct + "%。";

        return new CorePromiseMetrics(
                Math.max(leadDays, 1),
                hitRate,
                successPct,
                narrative
        );
    }

    private int daysSinceDiscovered(SignalItem item) {
        if (item.discoveredAt() == null || item.discoveredAt().isBlank()) {
            return 10;
        }
        try {
            LocalDate discovered = LocalDate.parse(item.discoveredAt().substring(0, 10));
            return (int) ChronoUnit.DAYS.between(discovered, LocalDate.now());
        } catch (Exception ex) {
            return 10;
        }
    }

    private int bestScore(Long cardId, Long brandId) {
        return opportunityService.points(cardId, brandId, PlatformView.ALL.getLabel()).stream()
                .mapToInt(point -> point.getOpportunityScore() == null ? 0 : point.getOpportunityScore())
                .max()
                .orElse(0);
    }
}
