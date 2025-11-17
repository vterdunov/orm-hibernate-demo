package ru.mephi.orm_hibernate_demo.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CourseReviewRequest {

    @NotNull
    private UUID studentId;

    @Min(1)
    @Max(5)
    private int rating;

    @NotBlank
    @Size(max = 2000)
    private String comment;
}
