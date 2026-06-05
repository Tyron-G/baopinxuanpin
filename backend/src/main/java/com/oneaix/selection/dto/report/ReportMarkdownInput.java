package com.oneaix.selection.dto.report;

import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.OpportunityDetail;
import com.oneaix.selection.dto.OpportunityLensFocus;
import com.oneaix.selection.dto.OpportunityNarrative;
import com.oneaix.selection.dto.ReportAction;
import com.oneaix.selection.dto.ReportActionSummary;
import com.oneaix.selection.dto.ReportRiskSummary;
import com.oneaix.selection.dto.SignalItem;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.enums.PlatformView;

import java.util.List;

/** Markdown 渲染输入 2026-06-04 */
public record ReportMarkdownInput(
        String title,
        String generatedAt,
        PlatformView platform,
        BrandInfo brand,
        OpportunityDetail detail,
        List<SignalItem> signals,
        List<InsightCardView> rankedCards,
        List<OpportunityLensFocus> opportunityLensFocuses,
        OpportunityNarrative opportunityNarrative,
        ReportRiskSummary riskSummary,
        ReportActionSummary actionSummary,
        List<ReportAction> nextActions,
        CompetitorReasonResolver competitorReasonResolver
) {
    @FunctionalInterface
    public interface CompetitorReasonResolver {
        String resolve(com.oneaix.selection.dto.CompetitorShop shop, PlatformView platform);
    }
}
