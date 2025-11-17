package ru.mephi.orm_hibernate_demo.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class GradeResponse {

    private UUID id;
    private Double points;
    private LocalDateTime gradedAt;
    private String comment;
}
