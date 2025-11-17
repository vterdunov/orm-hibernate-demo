package ru.mephi.orm_hibernate_demo.dto.response;

import lombok.Data;
import ru.mephi.orm_hibernate_demo.dto.nested.StudentInfo;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class QuizSubmissionResponse {

    private UUID id;
    private Double score;
    private Integer attemptNo;
    private LocalDateTime takenAt;
    private StudentInfo student;
    private UUID quizId;
    private Set<UUID> selectedOptionIds;
}
