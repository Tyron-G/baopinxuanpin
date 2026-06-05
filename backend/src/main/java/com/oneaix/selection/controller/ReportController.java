package com.oneaix.selection.controller;

import com.oneaix.selection.constant.ApiConstants;
import com.oneaix.selection.dto.SelectionReport;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.service.ReportService;
import com.oneaix.selection.service.report.ReportBinaryExporter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 选品报告导出（Markdown + Excel/PDF）2026-06-04 */
@Validated
@RestController
@RequestMapping("/api/report")
@Tag(name = "选品报告")
public class ReportController {

    private final ReportService reportService;
    private final ReportBinaryExporter binaryExporter;

    public ReportController(ReportService reportService, ReportBinaryExporter binaryExporter) {
        this.reportService = reportService;
        this.binaryExporter = binaryExporter;
    }

    @GetMapping("/{cardId}")
    public SelectionReport export(
            @PathVariable @Min(1) Long cardId,
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam(defaultValue = ApiConstants.DEFAULT_PLATFORM_VIEW) String platform
    ) {
        return reportService.export(cardId, brandId, PlatformView.normalize(platform).getLabel());
    }

    @GetMapping("/{cardId}/export/excel")
    public ResponseEntity<byte[]> exportExcel(
            @PathVariable @Min(1) Long cardId,
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam(defaultValue = ApiConstants.DEFAULT_PLATFORM_VIEW) String platform
    ) {
        SelectionReport report = reportService.export(cardId, brandId, PlatformView.normalize(platform).getLabel());
        byte[] bytes = binaryExporter.exportExcel(report);
        String fileName = report.fileName() + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }

    @GetMapping("/{cardId}/export/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @PathVariable @Min(1) Long cardId,
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam(defaultValue = ApiConstants.DEFAULT_PLATFORM_VIEW) String platform
    ) {
        SelectionReport report = reportService.export(cardId, brandId, PlatformView.normalize(platform).getLabel());
        byte[] bytes = binaryExporter.exportPdf(report);
        String fileName = report.fileName() + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }
}
