package com.oneaix.selection.controller;

import com.oneaix.selection.constant.ApiConstants;
import jakarta.validation.constraints.Min;
import com.oneaix.selection.dto.DashboardSummary;
import com.oneaix.selection.dto.ProductMetricsKpi;
import com.oneaix.selection.dto.WorkflowProgress;
import com.oneaix.selection.service.DashboardService;
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

    public DashboardController(DashboardService dashboardService, ProductMetricsService productMetricsService) {
        this.dashboardService = dashboardService;
        this.productMetricsService = productMetricsService;
    }

    @GetMapping
    public DashboardSummary summary(@RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId) {
        return dashboardService.summary(brandId);
    }

    @GetMapping("/workflow")
    public WorkflowProgress workflow(@RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId) {
        return dashboardService.workflow(brandId);
    }

    @GetMapping("/product-metrics")
    public ProductMetricsKpi productMetrics() {
        return productMetricsService.snapshot();
    }
}
