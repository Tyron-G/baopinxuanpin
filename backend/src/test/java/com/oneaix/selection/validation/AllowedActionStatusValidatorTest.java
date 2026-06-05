package com.oneaix.selection.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 2026-06-04 */
class AllowedActionStatusValidatorTest {

    private final AllowedActionStatusValidator validator = new AllowedActionStatusValidator();

    @Test
    void shouldAcceptKnownStatus() {
        assertTrue(validator.isValid("进行中", null));
    }

    @Test
    void shouldRejectUnknownStatus() {
        assertFalse(validator.isValid("随便写", null));
    }
}
