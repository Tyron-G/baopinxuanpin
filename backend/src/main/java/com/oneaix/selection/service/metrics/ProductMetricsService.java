package com.oneaix.selection.service.metrics;

import com.oneaix.selection.content.SampleProductMetricsCatalog;
import com.oneaix.selection.dto.ProductMetricsKpi;
import org.springframework.stereotype.Service;

/** 运营 KPI 样例 2026-06-04 */
@Service
public class ProductMetricsService {

    private final SampleProductMetricsCatalog catalog;

    public ProductMetricsService(SampleProductMetricsCatalog catalog) {
        this.catalog = catalog;
    }

    public ProductMetricsKpi snapshot() {
        return catalog.snapshot();
    }
}
