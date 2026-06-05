package com.oneaix.selection.service.competitor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 2026-06-05 BuiltinCompetitorCatalog */
class BuiltinCompetitorCatalogTest {

    @Test
    void shouldCoverTenCategoriesWithComplaints() {
        var shops = BuiltinCompetitorCatalog.shops();
        assertTrue(shops.size() >= 20);
        long categories = shops.stream().map(shop -> shop.focusCategory()).distinct().count();
        assertEquals(10, categories);
        assertTrue(shops.stream().allMatch(shop -> shop.complaintTopics() != null && !shop.complaintTopics().isEmpty()));
        assertTrue(shops.stream().allMatch(shop -> shop.growthSignal() != null && shop.growthSignal().contains("个月前")));
    }
}
