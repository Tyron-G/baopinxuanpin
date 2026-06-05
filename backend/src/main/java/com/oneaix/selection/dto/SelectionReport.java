package com.oneaix.selection.dto;

import java.util.List;

/** 2026-06-03 选品报告导出 */
public record SelectionReport(
        String title,
        String generatedAt,
        String format,
        String content,
        String fileName,
        String platformView,
        String brandSummary,
        DecisionSummary decisionSummary,
        CompetitionReport competitionReport,
        ProfitAnalysis profitAnalysis,
        SupplyChainFeasibility supplyChainFeasibility,
        ReportRiskSummary riskSummary,
        List<String> keySignals,
        List<String> opportunityHighlights,
        OpportunityNarrative opportunityNarrative,
        ReportActionSummary actionSummary,
        List<ReportAction> nextActions
) {
}
