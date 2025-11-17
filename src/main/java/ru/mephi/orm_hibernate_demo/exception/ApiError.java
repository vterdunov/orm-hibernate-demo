package ru.mephi.orm_hibernate_demo.exception;

import lombok.Builder;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

@Builder
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> validationErrors
) {
    public static ApiError of(int status, String error, String message, String path) {
        return ApiError.builder()
                .timestamp(Instant.now())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .validationErrors(Collections.emptyMap())
                .build();
    }

    public static ApiError withValidation(int status, String error, String message, String path, Map<String, String> validationErrors) {
        return ApiError.builder()
                .timestamp(Instant.now())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .validationErrors(validationErrors)
                .build();
    }
}
