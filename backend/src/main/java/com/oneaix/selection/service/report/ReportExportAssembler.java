package com.oneaix.selection.service.report;

import com.oneaix.selection.dto.BrandSelectionContext;
import com.oneaix.selection.dto.OpportunityDetail;
import com.oneaix.selection.dto.OpportunityLensFocus;
import com.oneaix.selection.dto.OpportunityNarrative;
import com.oneaix.selection.dto.ReportAction;
import com.oneaix.selection.dto.ReportActionSummary;
import com.oneaix.selection.dto.ReportRiskSummary;
import com.oneaix.selection.dto.SelectionReport;
import com.oneaix.selection.dto.SignalItem;
import com.oneaix.selection.dto.report.ReportMarkdownInput;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.util.TextFormats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** 选品报告导出内容聚合与 Markdown 组装 2026-06-04 */
@Component
public class ReportExportAssembler {

    private static final DateTimeFormatter GENERATED_AT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int TOP_SIGNAL_LIMIT = 5;

    private final ReportContentAssembler contentAssembler;
    private final ReportMarkdownRenderer markdownRenderer;
    private final Clock clock;

    public ReportExportAssembler(
            ReportContentAssembler contentAssembler,
            ReportMarkdownRenderer markdownRenderer,
            @Autowired(required = false) Clock clock
    ) {
        this.contentAssembler = contentAssembler;
        this.markdownRenderer = markdownRenderer;
        this.clock = clock != null ? clock : Clock.systemDefaultZone();
    }

    public SelectionReport assemble(BrandSelectionContext context, OpportunityDetail detail, String platformView) {
        PlatformView platform = PlatformView.normalize(platformView);
        BrandInfo brand = context.brand();

        List<SignalItem> signals = context.signals().stream().limit(TOP_SIGNAL_LIMIT).toList();
        var rankedCards = contentAssembler.sortCardsForPlatform(context.cards(), platform);
        List<OpportunityLensFocus> opportunityLensFocuses = contentAssembler.buildOpportunityLensFocuses(detail.points());
        OpportunityNarrative opportunityNarrative = contentAssembler.buildOpportunityNarrative(
                detail.competitorSummary(),
                detail.platformPlaybook(),
                detail.relatedCompetitors(),
                detail.differentiationAdvice(),
                opportunityLensFocuses,
                platform
        );
        List<ReportAction> nextActions = detail.nextActions();
        ReportActionSummary actionSummary = contentAssembler.buildActionSummary(nextActions);
        ReportRiskSummary riskSummary = contentAssembler.buildRiskSummary(detail.decisionSummary().risks());

        String generatedAt = LocalDateTime.now(clock).format(GENERATED_AT_FORMAT);
        String title = brand.getBrandName() + " · " + detail.insightCard().getCategoryName() + " 选品报告";
        String fileName = contentAssembler.sanitizeFileName(brand.getBrandName())
                + "_" + contentAssembler.sanitizeFileName(detail.insightCard().getCategoryName()) + "_选品报告.md";
        String brandSummary = brand.getBrandName() + " / " + brand.getIndustry()
                + " / 平台 " + brand.getTargetPlatforms()
                + " / 预算 " + TextFormats.nullToDash(brand.getBudgetRange());
        List<String> keySignals = signals.stream()
                .map(signal -> signal.signalType() + " | " + signal.categoryName() + " | " + signal.summary())
                .toList();
        List<String> opportunityHighlights = detail.points().stream()
                .map(point -> point.getTargetCrowd() + " · " + point.getScenarioText() + " | "
                        + point.getDecision() + " | " + point.getDifferentiation())
                .toList();

        String markdown = markdownRenderer.render(new ReportMarkdownInput(
                title,
                generatedAt,
                platform,
                brand,
                detail,
                signals,
                rankedCards,
                opportunityLensFocuses,
                opportunityNarrative,
                riskSummary,
                actionSummary,
                nextActions,
                contentAssembler::competitorPriorityReason
        ));

        return new SelectionReport(
                title,
                generatedAt,
                "markdown",
                markdown,
                fileName,
                platform.getLabel(),
                brandSummary,
                detail.decisionSummary(),
                detail.competitionReport(),
                detail.profitAnalysis(),
                detail.supplyChainFeasibility(),
                riskSummary,
                keySignals,
                opportunityHighlights,
                opportunityNarrative,
                actionSummary,
                nextActions
        );
    }
}
