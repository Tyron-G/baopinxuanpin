package com.oneaix.selection.service.report;

import com.oneaix.selection.dto.DecisionSummary;
import com.oneaix.selection.dto.ScoreBreakdown;
import com.oneaix.selection.dto.SelectionReport;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import com.oneaix.selection.util.ReportFileNames;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportBinaryExporterTest {

    @Test
    void pdfFileNameShouldDropMarkdownSuffix() {
        assertTrue(ReportFileNames.pdfFileName("壹沓生活_宠物智能用品_选品报告.md").endsWith(".pdf"));
        assertFalse(ReportFileNames.pdfFileName("壹沓生活_宠物智能用品_选品报告.md").contains(".md"));
    }

    @Test
    @EnabledIf("com.oneaix.selection.service.report.ReportBinaryExporterTest#hasChineseFont")
    void exportPdfShouldContainChineseText() throws Exception {
        ReportPdfFontResolver fontResolver = new ReportPdfFontResolver();
        ReportMarkdownPdfRenderer renderer = new ReportMarkdownPdfRenderer(fontResolver);
        ReportBinaryExporter exporter = new ReportBinaryExporter(renderer, fontResolver);

        SelectionReport report = new SelectionReport(
                "壹沓生活 · 宠物智能用品 选品报告",
                "2026-06-05 14:00",
                "markdown",
                """
                # 选品报告摘要

                ## 一、品牌约束
                - 目标平台：天猫 / 抖音
                - 预算带：20-50万

                ## 二、机会点
                | 人群 | 场景 | 评分 |
                | --- | --- | --- |
                | 25-35岁宠物主 | 夜间遛狗 | 91 |
                """,
                "壹沓生活_宠物智能用品_选品报告.md",
                "全平台",
                "壹沓生活 / 宠物 / 平台 天猫 / 预算 20-50万",
                new DecisionSummary("推荐立项", 86, "可进入验证", List.of(), List.of(),
                        new ScoreBreakdown(20, 18, 16, 15, 0, 86, 86)),
                null,
                null,
                null,
                null,
                List.of(),
                List.of(),
                null,
                null,
                List.of()
        );

        byte[] bytes = exporter.exportPdf(report);
        PdfReader reader = new PdfReader(bytes);
        String pageText = new PdfTextExtractor(reader).getTextFromPage(1);
        reader.close();
        assertTrue(pageText.contains("壹沓生活"));
        assertTrue(pageText.contains("宠物智能用品"));
        assertTrue(pageText.contains("品牌约束"));
    }

    static boolean hasChineseFont() {
        try {
            new ReportPdfFontResolver().baseFont();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
