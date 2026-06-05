package com.oneaix.selection.controller;

import com.oneaix.selection.constant.ApiConstants;
import jakarta.validation.constraints.Min;
import com.oneaix.selection.dto.CorePromiseMetrics;
import com.oneaix.selection.dto.DashboardSummary;
import com.oneaix.selection.dto.ProductMetricsKpi;
import com.oneaix.selection.dto.WorkflowProgress;
import com.oneaix.selection.service.DashboardService;
import com.oneaix.selection.service.metrics.CorePromiseMetricsService;
import com.oneaix.selection.service.metrics.ProductMetricsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "工作台")
public class DashboardController {
    private final DashboardService dashboardService;
    private final ProductMetricsService productMetricsService;
    private final CorePromiseMetricsService corePromiseMetricsService;

    public DashboardController(
            DashboardService dashboardService,
            ProductMetricsService productMetricsService,
            CorePromiseMetricsService corePromiseMetricsService
    ) {
        this.dashboardService = dashboardService;
        this.productMetricsService = productMetricsService;
        this.corePromiseMetricsService = corePromiseMetricsService;
    }

    @GetMapping
    public DashboardSummary summary(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam(required = false) String platform
    ) {
        return dashboardService.summary(brandId, platform);
    }

    @GetMapping("/workflow")
    public WorkflowProgress workflow(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam(required = false) String platform
    ) {
        return dashboardService.workflow(brandId, platform);
    }

    @GetMapping("/product-metrics")
    public ProductMetricsKpi productMetrics(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId
    ) {
        return productMetricsService.snapshot(brandId);
    }

    @GetMapping("/core-promise")
    public CorePromiseMetrics corePromise(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId
    ) {
        return corePromiseMetricsService.metrics(brandId);
    }
}
