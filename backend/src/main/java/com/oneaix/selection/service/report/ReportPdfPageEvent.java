package com.oneaix.selection.service.report;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

/**
 * PDF 页脚页码 2026-06-05
 */
class ReportPdfPageEvent extends PdfPageEventHelper {

    private final Font footerFont;

    ReportPdfPageEvent(ReportPdfFontResolver fontResolver) {
        this.footerFont = fontResolver.font(9, Font.NORMAL);
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        Phrase footer = new Phrase("爆品选品雷达 · 选品报告    第 " + writer.getPageNumber() + " 页", footerFont);
        ColumnText.showTextAligned(
                writer.getDirectContent(),
                Element.ALIGN_CENTER,
                footer,
                (document.left() + document.right()) / 2,
                document.bottom() - 18,
                0
        );
    }
}
