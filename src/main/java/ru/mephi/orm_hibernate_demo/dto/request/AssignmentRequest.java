package ru.mephi.orm_hibernate_demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AssignmentRequest {

    @NotBlank
    @Size(max = 255)
    private String title;

    private LocalDateTime dueDate;

    @Min(0)
    private Integer maxPoints;

    private UUID courseId;
}
