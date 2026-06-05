package com.oneaix.selection.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 2026-06-04 */
class SupportedPlatformsValidatorTest {

    private final SupportedPlatformsValidator validator = new SupportedPlatformsValidator();
    private final ConstraintValidatorContext context = null;

    @Test
    void shouldAcceptSupportedPlatforms() {
        assertTrue(validator.isValid(List.of("天猫", "抖音"), context));
    }

    @Test
    void shouldAcceptCrossBorderPlatforms() {
        assertTrue(validator.isValid(List.of("亚马逊", "Shopee", "TikTok Shop"), context));
    }

    @Test
    void shouldRejectUnknownPlatform() {
        assertFalse(validator.isValid(List.of("拼多多"), context));
    }

    @Test
    void shouldRejectAllPlatformLabel() {
        assertFalse(validator.isValid(List.of("全平台"), context));
    }
}
