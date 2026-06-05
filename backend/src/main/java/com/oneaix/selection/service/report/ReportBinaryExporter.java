package com.oneaix.selection.service.report;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.oneaix.selection.dto.SelectionReport;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/** Excel/PDF 报告导出（迭代1）2026-06-04 */
@Component
public class ReportBinaryExporter {

    public byte[] exportExcel(SelectionReport report) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("选品报告");
            int rowIndex = 0;
            rowIndex = writeRow(sheet, rowIndex, "标题", report.title());
            rowIndex = writeRow(sheet, rowIndex, "生成时间", report.generatedAt());
            rowIndex = writeRow(sheet, rowIndex, "平台视角", report.platformView());
            rowIndex = writeRow(sheet, rowIndex, "品牌摘要", report.brandSummary());
            if (report.decisionSummary() != null) {
                rowIndex = writeRow(sheet, rowIndex, "决策结论", report.decisionSummary().decision());
                rowIndex = writeRow(sheet, rowIndex, "置信度", String.valueOf(report.decisionSummary().confidence()));
            }
            if (report.competitionReport() != null) {
                rowIndex = writeRow(sheet, rowIndex, "竞争格局", report.competitionReport().summary());
            }
            if (report.profitAnalysis() != null) {
                rowIndex = writeRow(sheet, rowIndex, "利润分析", report.profitAnalysis().summary());
            }
            rowIndex = writeRow(sheet, rowIndex, "报告正文", report.content());
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Excel 导出失败", ex);
        }
    }

    public byte[] exportPdf(SelectionReport report) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            document.add(new Paragraph(report.title(), titleFont));
            document.add(new Paragraph("生成时间：" + report.generatedAt(), bodyFont));
            document.add(new Paragraph("平台视角：" + report.platformView(), bodyFont));
            document.add(new Paragraph(" "));
            if (report.brandSummary() != null) {
                document.add(new Paragraph(report.brandSummary(), bodyFont));
                document.add(new Paragraph(" "));
            }
            if (report.decisionSummary() != null) {
                document.add(new Paragraph(
                        "决策结论：" + report.decisionSummary().decision()
                                + "（置信度 " + report.decisionSummary().confidence() + "%）",
                        bodyFont
                ));
                document.add(new Paragraph(" "));
            }
            String content = report.content() == null ? "" : report.content();
            for (String line : content.split("\n")) {
                document.add(new Paragraph(line, bodyFont));
            }
            document.close();
            return out.toByteArray();
        } catch (Exception ex) {
            return ("选品报告\n" + report.title() + "\n\n" + report.content())
                    .getBytes(StandardCharsets.UTF_8);
        }
    }

    private int writeRow(Sheet sheet, int rowIndex, String key, String value) {
        Row row = sheet.createRow(rowIndex++);
        row.createCell(0).setCellValue(key);
        row.createCell(1).setCellValue(value == null ? "" : value);
        return rowIndex;
    }
}
