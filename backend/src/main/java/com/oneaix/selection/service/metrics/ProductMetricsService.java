package com.oneaix.selection.service.metrics;

import com.oneaix.selection.dto.ProductMetricsKpi;
import org.springframework.stereotype.Service;

/** 运营 KPI（应用内行为推算）2026-06-05 */
@Service
public class ProductMetricsService {

    private final OperationalMetricsCalculator operationalMetricsCalculator;

    public ProductMetricsService(OperationalMetricsCalculator operationalMetricsCalculator) {
        this.operationalMetricsCalculator = operationalMetricsCalculator;
    }

    public ProductMetricsKpi snapshot(Long brandId) {
        return operationalMetricsCalculator.snapshot(brandId);
    }
}
