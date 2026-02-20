package com.spring.boot.levelup.payment.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return new ApiError(
                Instant.now(),
                400,
                "BAD_REQUEST",
                ex.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIllegalState(
            IllegalStateException ex,
            HttpServletRequest request) {

        return new ApiError(
                Instant.now(),
                409,
                "INVALID_STATE",
                ex.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return new ApiError(
                Instant.now(),
                400,
                "VALIDATION_ERROR",
                message,
                request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        return new ApiError(
                Instant.now(),
                500,
                "INTERNAL_ERROR",
                "Something went wrong",
                request.getRequestURI());
    }
}
