package com.oneaix.selection.exception;

import org.springframework.http.HttpStatus;

/** 请求参数或业务前置条件不满足 2026-06-04 */
public class BadRequestException extends ApiException {
    public BadRequestException(String message) {
        this("bad_request", message);
    }

    public BadRequestException(String errorCode, String message) {
        super(HttpStatus.BAD_REQUEST.value(), errorCode, message);
    }
}
