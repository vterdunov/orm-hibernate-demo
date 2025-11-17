package ru.mephi.orm_hibernate_demo.dto.response;

import lombok.Data;
import ru.mephi.orm_hibernate_demo.enums.QuestionType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class QuestionResponse {

    private UUID id;
    private String text;
    private QuestionType type;
    private List<AnswerOptionResponse> options = new ArrayList<>();
}
