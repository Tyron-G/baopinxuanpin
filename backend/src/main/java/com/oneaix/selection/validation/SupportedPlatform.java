package com.oneaix.selection.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 单个目标平台白名单（竞品添加等）2026-06-04 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SupportedPlatformValidator.class)
public @interface SupportedPlatform {
    String message() default "平台仅支持：天猫、淘宝、抖音、小红书";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
