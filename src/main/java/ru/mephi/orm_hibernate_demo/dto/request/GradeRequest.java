package ru.mephi.orm_hibernate_demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GradeRequest {

    @NotNull
    @PositiveOrZero
    private Double points;

    @Size(max = 1000)
    private String comment;
}
