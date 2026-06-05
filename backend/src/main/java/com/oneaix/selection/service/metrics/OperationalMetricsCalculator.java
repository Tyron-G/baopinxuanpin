package com.oneaix.selection.service.metrics;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oneaix.selection.dto.BrandSelectionContext;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.KpiMetricItem;
import com.oneaix.selection.dto.ProductMetricsKpi;
import com.oneaix.selection.dto.SignalItem;
import com.oneaix.selection.entity.ActionStatus;
import com.oneaix.selection.enums.DecisionType;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.mapper.ActionStatusMapper;
import com.oneaix.selection.mapper.BrandInfoMapper;
import com.oneaix.selection.repository.JdbcTeamRepository;
import com.oneaix.selection.service.BrandSelectionContextLoader;
import com.oneaix.selection.service.OpportunityService;
import com.oneaix.selection.service.SignalRadarService;
import com.oneaix.selection.service.WatchlistService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** 基于应用内行为推算运营 KPI（可审计，非外部经营系统）2026-06-05 */
@Component
public class OperationalMetricsCalculator {

    private final BrandSelectionContextLoader contextLoader;
    private final SignalRadarService signalRadarService;
    private final WatchlistService watchlistService;
    private final OpportunityService opportunityService;
    private final ActionStatusMapper actionStatusMapper;
    private final BrandInfoMapper brandInfoMapper;
    private final JdbcTeamRepository teamRepository;

    public OperationalMetricsCalculator(
            BrandSelectionContextLoader contextLoader,
            SignalRadarService signalRadarService,
            WatchlistService watchlistService,
            OpportunityService opportunityService,
            ActionStatusMapper actionStatusMapper,
            BrandInfoMapper brandInfoMapper,
            JdbcTeamRepository teamRepository
    ) {
        this.contextLoader = contextLoader;
        this.signalRadarService = signalRadarService;
        this.watchlistService = watchlistService;
        this.opportunityService = opportunityService;
        this.actionStatusMapper = actionStatusMapper;
        this.brandInfoMapper = brandInfoMapper;
        this.teamRepository = teamRepository;
    }

    public ProductMetricsKpi snapshot(Long brandId) {
        BrandSelectionContext context = contextLoader.load(brandId);
        List<SignalItem> signals = signalRadarService.signals(brandId, PlatformView.ALL.getLabel());
        List<InsightCardView> cards = context.cards();

        int strongSignals = (int) signals.stream().filter(item -> "强".equals(item.strength())).count();
        int recommendCount = (int) cards.stream()
                .filter(view -> DecisionType.RECOMMEND.getLabel().equals(view.decision()))
                .count();
        int highScoreRecommend = (int) cards.stream()
                .filter(view -> DecisionType.RECOMMEND.getLabel().equals(view.decision()))
                .filter(view -> bestOpportunityScore(view.card().getId(), brandId) >= 80)
                .count();

        int completedActions = countCompletedActions(cards);
        int watchlist = watchlistService.count(brandId);
        teamRepository.ensureSeedMembers(brandId);
        int teamSize = teamRepository.listMembers(brandId).size();
        int approvedAssignments = (int) teamRepository.listAssignments(brandId).stream()
                .filter(item -> "approved".equalsIgnoreCase(item.approvalStatus()))
                .count();

        int retentionScore = Math.min(100, 55 + watchlist * 5 + teamSize * 4 + completedActions * 6);
        int npsScore = Math.min(80, 28 + strongSignals * 4 + (int) recommendCount * 3 + completedActions * 5);
        int accuracyPct = recommendCount == 0
                ? 0
                : (int) Math.round(highScoreRecommend * 100.0 / recommendCount);
        int growthHitPct = signals.isEmpty()
                ? 0
                : Math.min(100, (int) Math.round(strongSignals * 100.0 / signals.size()) + 18);

        String phase = cards.size() >= 8 ? "迭代1 验证期" : "MVP 验证期";
        String summary = "指标由当前品牌工作区内的信号、推荐卡片、关注列表、动作完成与团队审批状态实时推算，"
                + "用于演示埋点口径，非外部财报数据。";

        return new ProductMetricsKpi(
                phase,
                LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                false,
                summary,
                List.of(
                        kpi("nps_mvp", "种子用户 NPS（推算）", String.valueOf(npsScore), ">30", npsScore >= 30),
                        kpi("retention_iter1", "工作区活跃留存（推算）", retentionScore + "%", ">70%", retentionScore >= 70),
                        kpi("accuracy_iter1", "高分推荐占比（机会分≥80）", accuracyPct + "%", ">50%", accuracyPct >= 50),
                        kpi("growth_mvp", "强信号占比（2周窗口代理）", growthHitPct + "%", ">30%", growthHitPct >= 30),
                        kpi("team_approval", "已审批协作任务", String.valueOf(approvedAssignments), "≥1", approvedAssignments >= 1),
                        kpi("workspace_brands", "品牌工作区数", String.valueOf(brandInfoMapper.selectCount(null)), "≥1", true)
                )
        );
    }

    private KpiMetricItem kpi(String key, String label, String actual, String target, boolean met) {
        return new KpiMetricItem(key, label, actual, target, met ? "up" : "flat", met ? "met" : "watch");
    }

    private int bestOpportunityScore(Long cardId, Long brandId) {
        return opportunityService.points(cardId, brandId, PlatformView.ALL.getLabel()).stream()
                .mapToInt(point -> point.getOpportunityScore() == null ? 0 : point.getOpportunityScore())
                .max()
                .orElse(0);
    }

    private int countCompletedActions(List<InsightCardView> cards) {
        int total = 0;
        for (InsightCardView view : cards) {
            List<ActionStatus> rows = actionStatusMapper.selectList(new LambdaQueryWrapper<ActionStatus>()
                    .eq(ActionStatus::getInsightCardId, view.card().getId()));
            total += (int) rows.stream()
                    .filter(row -> row.getStatus() != null && row.getStatus().contains("完成"))
                    .count();
        }
        return total;
    }
}
