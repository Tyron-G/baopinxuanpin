package com.oneaix.selection.service.report;

import com.oneaix.selection.dto.CompetitorFocusReason;
import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.OpportunityLensFocus;
import com.oneaix.selection.dto.OpportunityNarrative;
import com.oneaix.selection.dto.ReportAction;
import com.oneaix.selection.dto.ReportActionSummary;
import com.oneaix.selection.dto.ReportRiskSummary;
import com.oneaix.selection.dto.RiskItem;
import com.oneaix.selection.entity.Opportunity;
import com.oneaix.selection.enums.ActionPriority;
import com.oneaix.selection.enums.ActionStatus;
import com.oneaix.selection.enums.EntryTiming;
import com.oneaix.selection.enums.LifecycleStage;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.enums.RiskLevel;
import com.oneaix.selection.enums.ScenarioKeyword;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/** 报告内容聚合：排序、摘要、视角判断 2026-06-04 */
@Component
public class ReportContentAssembler {

    public List<InsightCardView> sortCardsForPlatform(List<InsightCardView> cards, PlatformView platform) {
        if (platform.isAll()) {
            return cards;
        }
        return cards.stream()
                .sorted(Comparator
                        .comparingInt((InsightCardView view) -> platformAdjustedScore(view, platform))
                        .reversed()
                        .thenComparing(view -> view.card().getId()))
                .toList();
    }

    public String competitorPriorityReason(CompetitorShop shop, PlatformView platform) {
        StringBuilder builder = new StringBuilder();
        if (!platform.isAll() && shop.platform().equals(platform.getLabel())) {
            builder.append("与当前").append(platform.getLabel()).append("视角一致");
        }
        if (shop.hitProductCount() >= 3) {
            appendReason(builder, "爆品样本更多");
        }
        if (shop.complaintTopics() != null && shop.complaintTopics().size() >= 3) {
            appendReason(builder, "差评痛点更集中");
        }
        if (shop.sourceSignalType() != null && !shop.sourceSignalType().isBlank()) {
            appendReason(builder, "来源信号为" + shop.sourceSignalType());
        }
        if (builder.isEmpty()) {
            builder.append("覆盖当前类目的基础跟踪对象");
        }
        return builder.toString();
    }

    public OpportunityNarrative buildOpportunityNarrative(
            com.oneaix.selection.dto.CompetitorSummary competitorSummary,
            com.oneaix.selection.dto.PlatformPlaybook platformPlaybook,
            List<CompetitorShop> relatedCompetitors,
            List<String> differentiationAdvice,
            List<OpportunityLensFocus> opportunityLensFocuses,
            PlatformView platform
    ) {
        return new OpportunityNarrative(
                competitorSummary,
                platformPlaybook,
                relatedCompetitors.stream().limit(3).toList(),
                differentiationAdvice,
                relatedCompetitors.stream()
                        .limit(3)
                        .map(shop -> new CompetitorFocusReason(
                                shop.shopName(),
                                competitorPriorityReason(shop, platform)))
                        .toList(),
                opportunityLensFocuses
        );
    }

    public List<OpportunityLensFocus> buildOpportunityLensFocuses(List<Opportunity> points) {
        if (points == null || points.isEmpty()) {
            return List.of();
        }
        return List.of(
                buildLensFocus(
                        "balanced",
                        "综合判断",
                        topPoint(points, point -> point.getOpportunityScore() * 100
                                + number(point.getProfitElasticity()) * 2
                                - number(point.getCompetitionResistance())
                                + number(point.getOpportunityGravity())),
                        "当前优先兼顾机会评分、利润弹性、竞争阻力和机会引力，适合作为本轮默认先看的主机会点。"
                ),
                buildLensFocus(
                        "timing",
                        "入场时机",
                        topPoint(points, point -> point.getOpportunityScore() * 10
                                + LifecycleStage.priorityOf(point.getLifecycleStage()) * 20
                                + EntryTiming.priorityOf(point.getEntryTiming())),
                        "当前优先看“谁更适合现在进场”，更偏向生命周期仍在成长、且验证窗口更友好的机会点。"
                ),
                buildLensFocus(
                        "profit",
                        "利润弹性",
                        topPoint(points, point -> number(point.getProfitElasticity()) * 100
                                + point.getOpportunityScore()
                                - number(point.getCompetitionResistance())),
                        "当前优先看利润空间，系统会把更容易跑出毛利、且综合分依然健康的机会点排在前面。"
                ),
                buildLensFocus(
                        "scenario",
                        "人群场景",
                        topPoint(points, point -> ScenarioKeyword.matchScore(
                                        point.getScenarioText(), point.getTargetCrowd()) * 100
                                + number(point.getOpportunityGravity()) * 2
                                + point.getOpportunityScore()),
                        "当前优先看人群与场景表达，更适合需要先通过内容或场景切入完成市场教育的机会点。"
                )
        );
    }

    public ReportActionSummary buildActionSummary(List<ReportAction> actions) {
        if (actions == null || actions.isEmpty()) {
            return new ReportActionSummary(0, 0, 0, 0, "暂无动作", "-", "-", "当前还没有生成推进动作。");
        }

        int completedCount = (int) actions.stream()
                .filter(action -> ActionStatus.isCompleted(action.status()))
                .count();
        int inProgressCount = (int) actions.stream()
                .filter(action -> ActionStatus.isInProgress(action.status()))
                .count();
        int pendingCount = actions.size() - completedCount - inProgressCount;
        ReportAction focusAction = actions.stream()
                .sorted(Comparator
                        .comparingInt(this::actionPriorityRank)
                        .thenComparing(ReportAction::title))
                .findFirst()
                .orElse(actions.get(0));
        String latestUpdatedAt = actions.stream()
                .map(ReportAction::updatedAt)
                .filter(value -> value != null && !value.isBlank())
                .max(String::compareTo)
                .orElse("-");

        String summary = "当前共 " + actions.size() + " 个推进动作，已完成 " + completedCount
                + " 个，进行中 " + inProgressCount + " 个，待推进 " + pendingCount
                + " 个。当前主焦点为「" + focusAction.title() + "」，状态为「" + focusAction.status() + "」。";

        return new ReportActionSummary(
                actions.size(),
                completedCount,
                inProgressCount,
                pendingCount,
                focusAction.title(),
                focusAction.status(),
                latestUpdatedAt,
                summary
        );
    }

    public ReportRiskSummary buildRiskSummary(List<RiskItem> risks) {
        if (risks == null || risks.isEmpty()) {
            return new ReportRiskSummary(
                    0,
                    RiskLevel.LOW.getCode(),
                    "暂无显性风险",
                    "当前未识别到高优先级风险。",
                    RiskLevel.LOW.suggestedAttention(),
                    "当前风险水平较低，重点保持验证节奏。"
            );
        }

        RiskItem primaryRisk = risks.stream()
                .sorted(Comparator
                        .comparingInt((RiskItem item) -> RiskLevel.fromCode(item.level()).sortRank())
                        .thenComparing(RiskItem::title))
                .findFirst()
                .orElse(risks.get(0));
        RiskLevel highestLevel = RiskLevel.fromCode(primaryRisk.level());
        String summary = "当前共识别 " + risks.size() + " 条主要风险，其中最高等级为「" + highestLevel.getCode()
                + "」，首要风险是「" + primaryRisk.title() + "」。";

        return new ReportRiskSummary(
                risks.size(),
                highestLevel.getCode(),
                primaryRisk.title(),
                primaryRisk.description(),
                highestLevel.suggestedAttention(),
                summary
        );
    }

    public String sanitizeFileName(String value) {
        return value.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private int platformAdjustedScore(InsightCardView view, PlatformView platform) {
        return view.scoreBreakdown().totalScore()
                + platform.insightCategoryBoost(view.card().getCategoryName());
    }

    private OpportunityLensFocus buildLensFocus(String lensKey, String lensLabel, Opportunity point, String summary) {
        return new OpportunityLensFocus(
                lensKey,
                lensLabel,
                summary,
                point.getTargetCrowd(),
                point.getScenarioText(),
                point.getDifferentiation(),
                point.getOpportunityScore(),
                point.getOpportunityLevel(),
                point.getEntryTiming(),
                point.getLifecycleStage(),
                point.getReason()
        );
    }

    private Opportunity topPoint(List<Opportunity> points, java.util.function.ToIntFunction<Opportunity> scorer) {
        return points.stream()
                .max(Comparator
                        .comparingInt(scorer)
                        .thenComparingInt(Opportunity::getOpportunityScore))
                .orElse(points.get(0));
    }

    private int number(BigDecimal value) {
        return value == null ? 0 : value.intValue();
    }

    private int actionPriorityRank(ReportAction action) {
        int priorityRank = ActionPriority.rankOf(action.priority());
        int statusRank = ActionStatus.fromLabel(action.status()).rank() - 1;
        if (statusRank < 0) {
            statusRank = 3;
        }
        return priorityRank * 10 + statusRank;
    }

    private void appendReason(StringBuilder builder, String reason) {
        if (!builder.isEmpty()) {
            builder.append("，");
        }
        builder.append(reason);
    }
}
