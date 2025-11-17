package ru.mephi.orm_hibernate_demo.exception;

public class NotFoundException extends AppException {

    public NotFoundException(String message) {
        super(message, 404);
    }
}
