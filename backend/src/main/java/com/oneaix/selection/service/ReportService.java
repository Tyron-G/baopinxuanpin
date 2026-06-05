package com.oneaix.selection.service;

import com.oneaix.selection.annotation.TrackedExecution;
import com.oneaix.selection.dto.BrandSelectionContext;
import com.oneaix.selection.dto.SelectionReport;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.service.report.ReportExportAssembler;
import org.springframework.stereotype.Service;

/** 选品报告导出编排（薄层）2026-06-04 */
@Service
public class ReportService {

    private final OpportunityService opportunityService;
    private final BrandSelectionContextLoader contextLoader;
    private final ReportExportAssembler exportAssembler;

    public ReportService(
            OpportunityService opportunityService,
            BrandSelectionContextLoader contextLoader,
            ReportExportAssembler exportAssembler
    ) {
        this.opportunityService = opportunityService;
        this.contextLoader = contextLoader;
        this.exportAssembler = exportAssembler;
    }

    @TrackedExecution(value = "report-export", domain = "report")
    public SelectionReport export(Long cardId, Long brandId) {
        return export(cardId, brandId, PlatformView.ALL.getLabel());
    }

    @TrackedExecution(value = "report-export-platform", domain = "report")
    public SelectionReport export(Long cardId, Long brandId, String platformView) {
        BrandSelectionContext context = contextLoader.load(brandId);
        var detail = opportunityService.detail(cardId, context, PlatformView.normalize(platformView).getLabel());
        return exportAssembler.assemble(context, detail, platformView);
    }
}
