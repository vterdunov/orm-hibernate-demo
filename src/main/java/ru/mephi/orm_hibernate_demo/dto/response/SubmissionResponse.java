package ru.mephi.orm_hibernate_demo.dto.response;

import lombok.Data;
import ru.mephi.orm_hibernate_demo.dto.nested.AssignmentInfo;
import ru.mephi.orm_hibernate_demo.dto.nested.StudentInfo;
import ru.mephi.orm_hibernate_demo.enums.SubmissionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SubmissionResponse {

    private UUID id;
    private LocalDateTime submittedAt;
    private String contentUrl;
    private SubmissionStatus status;
    private Integer attemptNo;
    private AssignmentInfo assignment;
    private StudentInfo student;
    private GradeResponse grade;
}
