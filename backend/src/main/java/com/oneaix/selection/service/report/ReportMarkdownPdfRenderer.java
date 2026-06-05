package com.oneaix.selection.service.report;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.oneaix.selection.dto.SelectionReport;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 将选品报告 Markdown 渲染为结构化 PDF 段落（中文排版）2026-06-05
 */
@Component
public class ReportMarkdownPdfRenderer {

    private static final Pattern BOLD_PATTERN = Pattern.compile("\\*\\*(.+?)\\*\\*");
    private static final Color TABLE_HEADER_BG = new Color(239, 246, 255);
    private static final Color TABLE_BORDER = new Color(203, 213, 225);

    private final ReportPdfFontResolver fontResolver;

    public ReportMarkdownPdfRenderer(ReportPdfFontResolver fontResolver) {
        this.fontResolver = fontResolver;
    }

    public void render(Document document, SelectionReport report) throws Exception {
        Font titleFont = fontResolver.font(18, Font.BOLD);
        Font metaFont = fontResolver.font(10, Font.NORMAL);
        Font h1Font = fontResolver.font(15, Font.BOLD);
        Font h2Font = fontResolver.font(13, Font.BOLD);
        Font h3Font = fontResolver.font(12, Font.BOLD);
        Font bodyFont = fontResolver.font(10.5f, Font.NORMAL);
        Font quoteFont = fontResolver.font(10, Font.ITALIC);
        Font tableFont = fontResolver.font(9.5f, Font.NORMAL);
        Font tableHeaderFont = fontResolver.font(9.5f, Font.BOLD);

        Paragraph title = new Paragraph(report.title(), titleFont);
        title.setSpacingAfter(8f);
        document.add(title);

        Paragraph meta = new Paragraph(
                "生成时间：" + nullToEmpty(report.generatedAt())
                        + "    平台视角：" + nullToEmpty(report.platformView()),
                metaFont
        );
        meta.setSpacingAfter(6f);
        document.add(meta);

        if (report.brandSummary() != null && !report.brandSummary().isBlank()) {
            Paragraph brand = new Paragraph(report.brandSummary(), bodyFont);
            brand.setSpacingAfter(8f);
            document.add(brand);
        }

        if (report.decisionSummary() != null) {
            Paragraph decision = new Paragraph(
                    "决策结论：" + report.decisionSummary().decision()
                            + "（置信度 " + report.decisionSummary().confidence() + "%）",
                    bodyFont
            );
            decision.setSpacingAfter(12f);
            document.add(decision);
        }

        String content = report.content() == null ? "" : report.content();
        List<String> lines = content.lines().toList();
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                document.add(spacer(6f));
                continue;
            }
            if (isTableSeparator(trimmed)) {
                continue;
            }
            if (trimmed.startsWith("|") && trimmed.contains("|")) {
                List<String> tableLines = new ArrayList<>();
                tableLines.add(trimmed);
                while (index + 1 < lines.size()) {
                    String next = lines.get(index + 1).trim();
                    if (!next.startsWith("|") || isTableSeparator(next)) {
                        if (isTableSeparator(next)) {
                            index++;
                        }
                        break;
                    }
                    tableLines.add(next);
                    index++;
                }
                document.add(buildTable(tableLines, tableFont, tableHeaderFont));
                document.add(spacer(6f));
                continue;
            }
            if (trimmed.startsWith("### ")) {
                document.add(heading(trimmed.substring(4), h3Font, 10f));
                continue;
            }
            if (trimmed.startsWith("## ")) {
                document.add(heading(trimmed.substring(3), h2Font, 12f));
                continue;
            }
            if (trimmed.startsWith("# ")) {
                document.add(heading(trimmed.substring(2), h1Font, 14f));
                continue;
            }
            if (trimmed.startsWith("> ")) {
                Paragraph quote = paragraphWithInline(trimmed.substring(2), quoteFont, quoteFont);
                quote.setIndentationLeft(18f);
                quote.setSpacingAfter(4f);
                document.add(quote);
                continue;
            }
            if (trimmed.startsWith("- ") || trimmed.startsWith("* ")) {
                Paragraph bullet = paragraphWithInline("• " + trimmed.substring(2), bodyFont, bodyFont);
                bullet.setIndentationLeft(14f);
                bullet.setSpacingAfter(3f);
                document.add(bullet);
                continue;
            }
            if (trimmed.matches("^-{3,}$")) {
                document.add(spacer(8f));
                continue;
            }
            Paragraph paragraph = paragraphWithInline(trimmed, bodyFont, bodyFont);
            paragraph.setSpacingAfter(4f);
            document.add(paragraph);
        }
    }

    private Paragraph paragraphWithInline(String text, Font normalFont, Font emphasisFont) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(parseInline(text, normalFont, emphasisFont));
        return paragraph;
    }

    private Paragraph heading(String text, Font font, float spacingBefore) {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setSpacingBefore(spacingBefore);
        paragraph.setSpacingAfter(6f);
        return paragraph;
    }

    private Paragraph spacer(float height) {
        Paragraph paragraph = new Paragraph(" ");
        paragraph.setSpacingAfter(height);
        return paragraph;
    }

    private Phrase parseInline(String text, Font normalFont, Font emphasisFont) {
        Phrase phrase = new Phrase();
        Matcher matcher = BOLD_PATTERN.matcher(text);
        int cursor = 0;
        while (matcher.find()) {
            if (matcher.start() > cursor) {
                phrase.add(new Chunk(text.substring(cursor, matcher.start()), normalFont));
            }
            int emphasisStyle = (emphasisFont.getStyle() & Font.ITALIC) != 0
                    ? Font.BOLD | Font.ITALIC
                    : Font.BOLD;
            phrase.add(new Chunk(matcher.group(1), fontResolver.font(emphasisFont.getSize(), emphasisStyle)));
            cursor = matcher.end();
        }
        if (cursor < text.length()) {
            phrase.add(new Chunk(text.substring(cursor), normalFont));
        }
        if (phrase.isEmpty()) {
            phrase.add(new Chunk(text, normalFont));
        }
        return phrase;
    }

    private PdfPTable buildTable(List<String> rows, Font bodyFont, Font headerFont) throws Exception {
        List<String[]> cells = rows.stream()
                .map(this::splitTableRow)
                .filter(parts -> parts.length > 0)
                .toList();
        if (cells.isEmpty()) {
            return new PdfPTable(1);
        }
        int columns = cells.stream().mapToInt(row -> row.length).max().orElse(1);
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(4f);
        table.setSpacingAfter(4f);
        for (int rowIndex = 0; rowIndex < cells.size(); rowIndex++) {
            String[] row = cells.get(rowIndex);
            Font font = rowIndex == 0 ? headerFont : bodyFont;
            for (int columnIndex = 0; columnIndex < columns; columnIndex++) {
                String value = columnIndex < row.length ? row[columnIndex] : "";
                PdfPCell cell = new PdfPCell(new Phrase(value, font));
                cell.setPadding(6f);
                cell.setBorderColor(TABLE_BORDER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                if (rowIndex == 0) {
                    cell.setBackgroundColor(TABLE_HEADER_BG);
                }
                table.addCell(cell);
            }
        }
        return table;
    }

    private String[] splitTableRow(String line) {
        String trimmed = line.trim();
        if (trimmed.startsWith("|")) {
            trimmed = trimmed.substring(1);
        }
        if (trimmed.endsWith("|")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return java.util.Arrays.stream(trimmed.split("\\|"))
                .map(String::trim)
                .toArray(String[]::new);
    }

    private boolean isTableSeparator(String line) {
        return line.replace("|", "").replace(":", "").replace("-", "").trim().isEmpty();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
