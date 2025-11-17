package ru.mephi.orm_hibernate_demo.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.mephi.orm_hibernate_demo.enums.QuestionType;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuestionRequest {

    @NotBlank
    @Size(max = 1000)
    private String text;

    @NotNull
    private QuestionType type;

    @NotEmpty
    @Valid
    private List<AnswerOptionRequest> options = new ArrayList<>();
}
