package ru.mephi.orm_hibernate_demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AnswerOptionRequest {

    @NotBlank
    @Size(max = 500)
    private String text;

    private boolean correct;
}
