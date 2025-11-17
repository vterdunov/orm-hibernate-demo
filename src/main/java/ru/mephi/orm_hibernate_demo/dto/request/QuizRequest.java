package ru.mephi.orm_hibernate_demo.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuizRequest {

    @NotBlank
    @Size(max = 255)
    private String title;

    @Size(max = 2000)
    private String description;

    private Integer timeLimitMinutes;

    @NotEmpty
    @Valid
    private List<QuestionRequest> questions = new ArrayList<>();
}
