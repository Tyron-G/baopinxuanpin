package com.oneaix.selection.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 报告推进动作状态白名单 2026-06-04 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AllowedActionStatusValidator.class)
public @interface AllowedActionStatus {
    String message() default "动作状态仅支持：待执行、待确认、进行中、已完成、已放弃";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
