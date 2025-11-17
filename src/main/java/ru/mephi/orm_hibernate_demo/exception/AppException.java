package ru.mephi.orm_hibernate_demo.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final int httpCode;

    public AppException(String message, int httpCode) {
        super(message);
        this.httpCode = httpCode;
    }
}
