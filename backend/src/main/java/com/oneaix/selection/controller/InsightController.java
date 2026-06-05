package com.oneaix.selection.controller;

import com.oneaix.selection.constant.ApiConstants;
import jakarta.validation.constraints.Min;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.InsightSummary;
import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.entity.CompetitionData;
import com.oneaix.selection.entity.SupplyDemand;
import com.oneaix.selection.service.InsightService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/insight")
@Tag(name = "洞察发现")
public class InsightController {
    private final InsightService insightService;

    public InsightController(InsightService insightService) {
        this.insightService = insightService;
    }

    @GetMapping("/trend")
    public List<CategoryTrend> trends(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam(required = false) String platform
    ) {
        return insightService.trends(brandId, platform);
    }

    @GetMapping("/competition")
    public List<CompetitionData> competition(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam(required = false) String platform
    ) {
        return insightService.competition(brandId, platform);
    }

    @GetMapping("/supply-demand")
    public List<SupplyDemand> supplyDemand(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam(required = false) String platform
    ) {
        return insightService.supplyDemand(brandId, platform);
    }

    @GetMapping("/cards")
    public List<InsightCardView> cards(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam(required = false) String platform
    ) {
        return insightService.cards(brandId, platform);
    }

    @GetMapping("/summary")
    public InsightSummary summary(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam(required = false) String platform
    ) {
        return insightService.summary(brandId, platform);
    }
}
