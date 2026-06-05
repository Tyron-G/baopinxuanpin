package com.oneaix.selection.controller;

import com.oneaix.selection.constant.ApiConstants;
import jakarta.validation.constraints.Min;
import com.oneaix.selection.dto.CompetitorRequest;
import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.dto.CompetitorTimeline;
import com.oneaix.selection.service.CompetitorService;
import com.oneaix.selection.service.competitor.CompetitorDiscoveryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/competitor")
@Tag(name = "竞品监控")
public class CompetitorController {
    private final CompetitorService competitorService;
    private final CompetitorDiscoveryService discoveryService;

    public CompetitorController(
            CompetitorService competitorService,
            CompetitorDiscoveryService discoveryService
    ) {
        this.competitorService = competitorService;
        this.discoveryService = discoveryService;
    }

    @GetMapping
    public List<CompetitorShop> list(@RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId) {
        return competitorService.list(brandId);
    }

    @GetMapping("/timeline")
    public List<CompetitorTimeline> timelines(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = ApiConstants.DEFAULT_PLATFORM_VIEW) String platform
    ) {
        return competitorService.timelines(brandId, category, platform);
    }

    @PostMapping
    public CompetitorShop add(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @Valid @RequestBody CompetitorRequest request
    ) {
        return competitorService.add(brandId, request);
    }

    @GetMapping("/suggestions")
    public List<CompetitorShop> suggestions(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId
    ) {
        return discoveryService.suggest(brandId);
    }

    @PostMapping("/discover")
    public List<CompetitorShop> discover(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId
    ) {
        return discoveryService.discoverAndAdd(brandId);
    }
}
