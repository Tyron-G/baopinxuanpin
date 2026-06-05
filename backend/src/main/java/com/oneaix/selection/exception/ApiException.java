package com.oneaix.selection.exception;

/** API 业务异常基类 2026-06-04 */
public abstract class ApiException extends RuntimeException {
    private final int httpStatus;
    private final String errorCode;

    protected ApiException(int httpStatus, String errorCode, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public int httpStatus() {
        return httpStatus;
    }

    public String errorCode() {
        return errorCode;
    }
}
