package com.whales.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> build(HttpServletRequest req, HttpStatus status, String message) {
        return ResponseEntity.status(status).body(
                new ErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(), message, req.getRequestURI())
        );
    }

    // 1) 컨트롤러/서비스에서 명시적으로 던진 상태코드
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleRSE(ResponseStatusException ex, HttpServletRequest req) {
        return build(req, HttpStatus.valueOf(ex.getStatusCode().value()), ex.getReason());
    }

    // 2) @Valid 바인딩 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        FieldError fe = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String msg = (fe != null) ? (fe.getField() + " " + fe.getDefaultMessage()) : "Validation failed";
        return build(req, HttpStatus.BAD_REQUEST, msg);
    }

    // 3) 잘못된 JSON/타입
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build(req, HttpStatus.BAD_REQUEST, "Malformed JSON or wrong type");
    }

    // 4) 권한 없음 (인가 실패)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return build(req, HttpStatus.FORBIDDEN, "Access denied");
    }

    // 5) 일반적인 잘못된 요청
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIAE(IllegalArgumentException ex, HttpServletRequest req) {
        return build(req, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 6) 마지막 안전망
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAny(Exception ex, HttpServletRequest req) {
        log.error("Unhandled", ex);
        return build(req, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }
}