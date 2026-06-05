package com.oneaix.selection.service.report;

import com.oneaix.selection.dto.BrandSelectionContext;
import com.oneaix.selection.dto.SelectionReport;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.entity.InsightCard;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** 2026-06-04 报告导出组装 */
class ReportExportAssemblerTest {

    @Test
    void shouldUseInjectedClockForGeneratedAt() {
        Clock clock = Clock.fixed(Instant.parse("2026-06-04T10:15:00Z"), ZoneId.of("Asia/Shanghai"));
        ReportContentAssembler contentAssembler = mock(ReportContentAssembler.class);
        ReportMarkdownRenderer markdownRenderer = mock(ReportMarkdownRenderer.class);
        when(contentAssembler.sortCardsForPlatform(org.mockito.ArgumentMatchers.anyList(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of());
        when(contentAssembler.buildOpportunityLensFocuses(org.mockito.ArgumentMatchers.anyList()))
                .thenReturn(List.of());
        when(contentAssembler.buildOpportunityNarrative(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.anyList(),
                org.mockito.ArgumentMatchers.anyList(),
                org.mockito.ArgumentMatchers.anyList(),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(mock(com.oneaix.selection.dto.OpportunityNarrative.class));
        when(contentAssembler.buildActionSummary(org.mockito.ArgumentMatchers.anyList()))
                .thenReturn(mock(com.oneaix.selection.dto.ReportActionSummary.class));
        when(contentAssembler.buildRiskSummary(org.mockito.ArgumentMatchers.anyList()))
                .thenReturn(mock(com.oneaix.selection.dto.ReportRiskSummary.class));
        when(contentAssembler.sanitizeFileName(org.mockito.ArgumentMatchers.anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0, String.class));
        when(markdownRenderer.render(org.mockito.ArgumentMatchers.any()))
                .thenReturn("# report");

        BrandInfo brand = new BrandInfo();
        brand.setBrandName("测试品牌");
        brand.setIndustry("消费品");
        brand.setTargetPlatforms("天猫");
        brand.setBudgetRange("5-20万");

        InsightCard card = new InsightCard();
        card.setId(1L);
        card.setCategoryName("宠物智能用品");

        var detail = mock(com.oneaix.selection.dto.OpportunityDetail.class);
        when(detail.insightCard()).thenReturn(card);
        when(detail.points()).thenReturn(List.of());
        when(detail.nextActions()).thenReturn(List.of());
        when(detail.decisionSummary()).thenReturn(mock(com.oneaix.selection.dto.DecisionSummary.class));
        when(detail.competitionReport()).thenReturn(mock(com.oneaix.selection.dto.CompetitionReport.class));
        when(detail.profitAnalysis()).thenReturn(mock(com.oneaix.selection.dto.ProfitAnalysis.class));
        when(detail.supplyChainFeasibility()).thenReturn(mock(com.oneaix.selection.dto.SupplyChainFeasibility.class));

        BrandSelectionContext context = new BrandSelectionContext(
                brand, List.of(card), java.util.Set.of("宠物智能用品"), List.of(), List.of());

        ReportExportAssembler assembler = new ReportExportAssembler(contentAssembler, markdownRenderer, clock);
        SelectionReport report = assembler.assemble(context, detail, "全平台");

        assertEquals("2026-06-04 18:15", report.generatedAt());
        assertTrue(report.title().contains("测试品牌"));
        assertTrue(report.fileName().endsWith("_选品报告.md"));
    }
}
