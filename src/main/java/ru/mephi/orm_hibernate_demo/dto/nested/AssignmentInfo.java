package ru.mephi.orm_hibernate_demo.dto.nested;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AssignmentInfo {

    private UUID id;
    private String title;
    private LocalDateTime dueDate;
    private UUID courseId;
}
