package ru.mephi.orm_hibernate_demo.dto.response;

import lombok.Data;
import ru.mephi.orm_hibernate_demo.dto.nested.CourseInfo;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AssignmentResponse {

    private UUID id;
    private String title;
    private LocalDateTime dueDate;
    private Integer maxPoints;
    private CourseInfo course;
    private LocalDateTime createdAt;
}
