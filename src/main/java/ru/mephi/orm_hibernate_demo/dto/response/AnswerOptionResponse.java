package ru.mephi.orm_hibernate_demo.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class AnswerOptionResponse {

    private UUID id;
    private String text;
    private boolean correct;
}
