package com.oneaix.selection.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

/** 2026-06-04 */
public class AllowedActionStatusValidator implements ConstraintValidator<AllowedActionStatus, String> {

    static final Set<String> ALLOWED = Set.of("待执行", "待确认", "进行中", "已完成", "已放弃");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && ALLOWED.contains(value.trim());
    }
}
