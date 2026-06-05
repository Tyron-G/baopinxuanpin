package com.oneaix.selection.util;

/** 报告导出文件名工具 2026-06-05 */
public final class ReportFileNames {

    private ReportFileNames() {
    }

    public static String baseName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "选品报告";
        }
        String normalized = fileName.trim();
        if (normalized.endsWith(".md")) {
            return normalized.substring(0, normalized.length() - 3);
        }
        if (normalized.endsWith(".pdf")) {
            return normalized.substring(0, normalized.length() - 4);
        }
        return normalized;
    }

    public static String pdfFileName(String fileName) {
        return baseName(fileName) + ".pdf";
    }

    public static String excelFileName(String fileName) {
        return baseName(fileName) + ".xlsx";
    }
}
