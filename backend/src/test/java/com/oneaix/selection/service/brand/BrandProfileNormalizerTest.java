package com.oneaix.selection.service.brand;

import com.oneaix.selection.dto.BrandRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 2026-06-04 */
class BrandProfileNormalizerTest {

    private final BrandProfileNormalizer normalizer = new BrandProfileNormalizer();

    @Test
    void shouldDedupePlatformsAndExcludeCategories() {
        BrandRequest request = new BrandRequest(
                "品牌A",
                "消费品",
                null,
                false,
                null,
                List.of("天猫", "天猫", "抖音"),
                null,
                null,
                null,
                null,
                List.of("清洁", "清洁", "食品"),
                null
        );

        var normalized = normalizer.normalize(request);
        assertEquals("天猫,抖音", normalized.targetPlatforms());
        assertEquals("清洁,食品", normalized.excludeCategories());
    }
}
