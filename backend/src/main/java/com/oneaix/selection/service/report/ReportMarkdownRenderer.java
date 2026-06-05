package com.oneaix.selection.service.report;

import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.OpportunityLensFocus;
import com.oneaix.selection.dto.ReportAction;
import com.oneaix.selection.dto.SignalItem;
import com.oneaix.selection.dto.report.ReportMarkdownInput;
import com.oneaix.selection.entity.Opportunity;
import com.oneaix.selection.util.TextFormats;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.IntStream;

/** 选品报告 Markdown 渲染器（静态节模板 + 动态列表拼装）2026-06-04 */
@Component
public class ReportMarkdownRenderer {

    private final ReportMarkdownTemplateLoader templateLoader;

    public ReportMarkdownRenderer(ReportMarkdownTemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    public String render(ReportMarkdownInput input) {
        StringBuilder md = new StringBuilder();
        appendHeader(md, input);
        appendBrandConstraints(md, input);
        appendCategoryConclusion(md, input);
        appendSignals(md, input);
        appendOpportunityPoints(md, input);
        appendRiskSummary(md, input);
        appendLensFocuses(md, input);
        appendProfitAndSupply(md, input);
        appendCompetitorStrategy(md, input);
        appendPlatformPlaybook(md, input);
        appendCompetitorComparison(md, input);
        appendCategoryRanking(md, input);
        appendActionSummary(md, input);
        appendNextActions(md, input);
        return md.toString();
    }

    private void appendHeader(StringBuilder md, ReportMarkdownInput input) {
        md.append(templateLoader.render("header.md", Map.of(
                "title", input.title(),
                "generatedAt", input.generatedAt(),
                "brandName", input.brand().getBrandName(),
                "industry", input.brand().getIndustry(),
                "platform", input.platform().getLabel()
        )));
    }

    private void appendBrandConstraints(StringBuilder md, ReportMarkdownInput input) {
        var brand = input.brand();
        md.append(templateLoader.render("brand-constraints.md", Map.of(
                "targetPlatforms", TextFormats.nullToDash(brand.getTargetPlatforms()),
                "budgetRange", TextFormats.nullToDash(brand.getBudgetRange()),
                "profitMin", TextFormats.nullToDash(brand.getProfitMin()),
                "supplyChain", TextFormats.nullToDash(brand.getSupplyChain())
        )));
    }

    private void appendCategoryConclusion(StringBuilder md, ReportMarkdownInput input) {
        var card = input.detail().insightCard();
        md.append(templateLoader.render("category-conclusion.md", Map.of(
                "categoryName", card.getCategoryName(),
                "marketSize", card.getMarketSize(),
                "marketGrowth", card.getMarketGrowth(),
                "recommendation", card.getRecommendation()
        )));
    }

    private void appendSignals(StringBuilder md, ReportMarkdownInput input) {
        md.append("## 三、今日重点信号\n");
        for (SignalItem signal : input.signals()) {
            md.append("- **").append(signal.signalType()).append("** | ")
                    .append(signal.categoryName()).append(" | ")
                    .append(signal.platform()).append(" | ")
                    .append(signal.summary()).append("\n");
        }
        md.append("\n");
    }

    private void appendOpportunityPoints(StringBuilder md, ReportMarkdownInput input) {
        md.append("## 四、机会点清单\n");
        for (Opportunity point : input.detail().points()) {
            md.append("### ").append(point.getTargetCrowd()).append(" · ").append(point.getScenarioText()).append("\n");
            md.append("- 评分：").append(point.getOpportunityScore()).append(" / ").append(point.getOpportunityLevel()).append("\n");
            md.append("- 差异化：").append(point.getDifferentiation()).append("\n");
            md.append("- 决策：").append(point.getDecision()).append(" — ").append(point.getReason()).append("\n\n");
        }
    }

    private void appendRiskSummary(StringBuilder md, ReportMarkdownInput input) {
        var riskSummary = input.riskSummary();
        md.append(templateLoader.render("risk-summary.md", Map.of(
                "totalCount", String.valueOf(riskSummary.totalCount()),
                "highestLevel", riskSummary.highestLevel(),
                "primaryRiskTitle", riskSummary.primaryRiskTitle(),
                "primaryRiskDescription", riskSummary.primaryRiskDescription(),
                "suggestedAttention", riskSummary.suggestedAttention(),
                "summary", riskSummary.summary()
        )));
    }

    private void appendLensFocuses(StringBuilder md, ReportMarkdownInput input) {
        if (input.opportunityLensFocuses().isEmpty()) {
            return;
        }
        md.append("## 六、机会点视角判断\n");
        for (OpportunityLensFocus item : input.opportunityLensFocuses()) {
            md.append("- ").append(item.lensLabel())
                    .append("：").append(item.scenarioText())
                    .append("（").append(item.targetCrowd()).append("）")
                    .append(" | 评分 ").append(item.opportunityScore())
                    .append(" | ").append(item.lifecycleStage())
                    .append(" / ").append(item.entryTiming())
                    .append("\n");
            md.append("  - 结论：").append(item.summary()).append("\n");
            md.append("  - 差异化：").append(item.differentiation()).append("\n");
        }
        md.append("\n");
    }

    private void appendProfitAndSupply(StringBuilder md, ReportMarkdownInput input) {
        md.append("## 七、利润与供应链\n");
        md.append("- 目标售价：").append(input.detail().profitAnalysis().targetPrice()).append("\n");
        md.append("- 预估净利：").append(input.detail().profitAnalysis().netMargin()).append("\n");
        md.append("- 供应链结论：").append(input.detail().supplyChainFeasibility().conclusion()).append("\n\n");
    }

    private void appendCompetitorStrategy(StringBuilder md, ReportMarkdownInput input) {
        var summary = input.detail().competitorSummary();
        md.append("## 八、竞品对比与切入策略\n");
        md.append("- 跟踪竞品数：").append(summary.trackedShopCount()).append("\n");
        md.append("- 覆盖平台：").append(summary.coveredPlatforms()).append("\n");
        md.append("- 爆品样本：").append(summary.totalHitProductCount()).append("\n");
        md.append("- 最强信号：").append(summary.strongestSignal()).append("\n");
        if (!summary.commonComplaintTopics().isEmpty()) {
            md.append("- 高频差评主题：").append(String.join(" / ", summary.commonComplaintTopics())).append("\n");
        }
        md.append("- 对比摘要：").append(summary.summary()).append("\n");
        if (!input.detail().differentiationAdvice().isEmpty()) {
            md.append("- 差异化建议：").append("\n");
            input.detail().differentiationAdvice().forEach(item -> md.append("  - ").append(item).append("\n"));
        }
        md.append("\n");
    }

    private void appendPlatformPlaybook(StringBuilder md, ReportMarkdownInput input) {
        var playbook = input.detail().platformPlaybook();
        md.append("## 九、平台切入建议\n");
        md.append("- 首发平台：").append(playbook.firstLaunchPlatform()).append("\n");
        md.append("- 验证平台：").append(playbook.validationPlatform()).append("\n");
        md.append("- 转化平台：").append(playbook.conversionPlatform()).append("\n");
        md.append("- 平台摘要：").append(playbook.summary()).append("\n");
        playbook.executionHints().forEach(item -> md.append("  - ").append(item).append("\n"));
        md.append("\n");
    }

    private void appendCompetitorComparison(StringBuilder md, ReportMarkdownInput input) {
        if (input.detail().relatedCompetitors().isEmpty()) {
            return;
        }
        md.append("## 十、竞品横向对比\n");
        input.detail().relatedCompetitors().stream().limit(3).forEach(shop -> {
            md.append("- ").append(shop.shopName())
                    .append(" | ").append(shop.platform())
                    .append(" | 爆品数 ").append(shop.hitProductCount())
                    .append(" | 差评主题 ").append(String.join(" / ", shop.complaintTopics()))
                    .append(" | 增长信号 ").append(shop.growthSignal())
                    .append(" | 优先关注理由 ")
                    .append(input.competitorReasonResolver().resolve(shop, input.platform()))
                    .append("\n");
        });
        md.append("\n");
    }

    private void appendCategoryRanking(StringBuilder md, ReportMarkdownInput input) {
        md.append("## 十一、候选赛道排行\n");
        for (int i = 0; i < input.rankedCards().size(); i++) {
            InsightCardView view = input.rankedCards().get(i);
            md.append(i + 1).append(". ").append(view.card().getCategoryName())
                    .append(" — ").append(view.card().getRecommendation()).append("\n");
        }
        md.append("\n");
    }

    private void appendActionSummary(StringBuilder md, ReportMarkdownInput input) {
        var actionSummary = input.actionSummary();
        md.append(templateLoader.render("action-summary.md", Map.of(
                "totalCount", String.valueOf(actionSummary.totalCount()),
                "completedCount", String.valueOf(actionSummary.completedCount()),
                "inProgressCount", String.valueOf(actionSummary.inProgressCount()),
                "pendingCount", String.valueOf(actionSummary.pendingCount()),
                "focusActionTitle", actionSummary.focusActionTitle(),
                "focusActionStatus", actionSummary.focusActionStatus(),
                "latestUpdatedAt", actionSummary.latestUpdatedAt(),
                "summary", actionSummary.summary()
        )));
    }

    private void appendNextActions(StringBuilder md, ReportMarkdownInput input) {
        md.append("## 十三、下一步动作\n");
        IntStream.range(0, input.nextActions().size()).forEach(index -> {
            ReportAction action = input.nextActions().get(index);
            md.append(index + 1).append(". ").append(action.title())
                    .append("（").append(action.priority()).append(" / ")
                    .append(action.ownerRole()).append(" / ")
                    .append(action.eta()).append("）\n");
            md.append("   - 目标：").append(action.expectedGoal()).append("\n");
        });
    }
}
