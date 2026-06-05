package com.oneaix.selection.controller;

import com.oneaix.selection.constant.ApiConstants;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.InsightSummary;
import com.oneaix.selection.dto.OpportunityDetail;
import com.oneaix.selection.dto.OpportunityRankingPage;
import com.oneaix.selection.dto.SelectionReport;
import com.oneaix.selection.dto.SignalItem;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.service.BrandSelectionContextLoader;
import com.oneaix.selection.service.InsightService;
import com.oneaix.selection.service.OpportunityService;
import com.oneaix.selection.service.ReportService;
import com.oneaix.selection.service.SignalRadarService;
import com.oneaix.selection.service.ranking.OpportunityRankingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 开放数据接口（迭代2 API 开放）2026-06-04 */
@Validated
@RestController
@RequestMapping("/api/open/v1")
@Tag(name = "开放API")
public class OpenApiController {

    private final SignalRadarService signalRadarService;
    private final BrandSelectionContextLoader contextLoader;
    private final OpportunityRankingService rankingService;
    private final InsightService insightService;
    private final OpportunityService opportunityService;
    private final ReportService reportService;

    public OpenApiController(
            SignalRadarService signalRadarService,
            BrandSelectionContextLoader contextLoader,
            OpportunityRankingService rankingService,
            InsightService insightService,
            OpportunityService opportunityService,
            ReportService reportService
    ) {
        this.signalRadarService = signalRadarService;
        this.contextLoader = contextLoader;
        this.rankingService = rankingService;
        this.insightService = insightService;
        this.opportunityService = opportunityService;
        this.reportService = reportService;
    }

    @GetMapping("/radar/signals")
    public List<SignalItem> signals(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId
    ) {
        return signalRadarService.signals(brandId);
    }

    @GetMapping("/ranking/top50")
    public OpportunityRankingPage ranking(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId
    ) {
        return rankingService.top50(contextLoader.load(brandId), 1, 50);
    }

    @GetMapping("/insight/cards")
    public List<InsightCardView> insightCards(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId
    ) {
        return insightService.cards(brandId);
    }

    @GetMapping("/insight/summary")
    public InsightSummary insightSummary(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId
    ) {
        return insightService.summary(brandId);
    }

    @GetMapping("/opportunity/{cardId}")
    public OpportunityDetail opportunity(
            @PathVariable @Min(1) Long cardId,
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam(defaultValue = ApiConstants.DEFAULT_PLATFORM_VIEW) String platform
    ) {
        return opportunityService.detail(cardId, brandId, platform);
    }

    @GetMapping("/report/{cardId}")
    public SelectionReport report(
            @PathVariable @Min(1) Long cardId,
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam(defaultValue = ApiConstants.DEFAULT_PLATFORM_VIEW) String platform
    ) {
        return reportService.export(cardId, brandId, PlatformView.normalize(platform).getLabel());
    }
}
