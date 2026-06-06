package com.oneaix.selection.exception;

import com.oneaix.selection.api.ApiResult;
import com.oneaix.selection.monitoring.ExceptionMetricsRecorder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/** 全局异常处理 2026-06-04 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ExceptionMetricsRecorder exceptionMetricsRecorder;

    public GlobalExceptionHandler(ExceptionMetricsRecorder exceptionMetricsRecorder) {
        this.exceptionMetricsRecorder = exceptionMetricsRecorder;
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResult<Void>> handleApiException(ApiException ex, HttpServletRequest request) {
        exceptionMetricsRecorder.record(request.getRequestURI(), ex.httpStatus(), ex.errorCode(), ex);
        return ResponseEntity.status(ex.httpStatus())
                .body(ApiResult.fail(ex.httpStatus(), ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResult<Void>> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        String message = ex.getConstraintViolations().stream()
                .findFirst()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .orElse("参数校验失败");
        exceptionMetricsRecorder.record(request.getRequestURI(), 400, "constraint_violation", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResult.fail(400, message, request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Void>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("参数校验失败");
        exceptionMetricsRecorder.record(request.getRequestURI(), 400, "request_validation_failed", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResult.fail(400, message, request.getRequestURI()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResult<Void>> handleNoResource(NoResourceFoundException ex, HttpServletRequest request) {
        exceptionMetricsRecorder.record(request.getRequestURI(), 404, "static_resource_not_found", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResult.fail(404, "资源不存在", request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Void>> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("未处理异常 path={}", request.getRequestURI(), ex);
        exceptionMetricsRecorder.record(request.getRequestURI(), 500, "internal_server_error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResult.fail(500, "服务内部错误", request.getRequestURI()));
    }
}
