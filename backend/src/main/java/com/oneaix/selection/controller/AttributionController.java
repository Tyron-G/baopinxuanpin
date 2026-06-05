package com.oneaix.selection.controller;

import com.oneaix.selection.constant.ApiConstants;
import com.oneaix.selection.dto.SelectionAttributionReport;
import com.oneaix.selection.service.attribution.SelectionAttributionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 选品归因 2026-06-04 */
@Validated
@RestController
@RequestMapping("/api/attribution")
@Tag(name = "选品归因")
public class AttributionController {

    private final SelectionAttributionService attributionService;

    public AttributionController(SelectionAttributionService attributionService) {
        this.attributionService = attributionService;
    }

    @GetMapping("/report")
    public SelectionAttributionReport report(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId
    ) {
        return attributionService.report(brandId);
    }
}
