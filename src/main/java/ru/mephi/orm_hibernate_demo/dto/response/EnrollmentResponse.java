package ru.mephi.orm_hibernate_demo.dto.response;

import lombok.Data;
import ru.mephi.orm_hibernate_demo.dto.nested.CourseInfo;
import ru.mephi.orm_hibernate_demo.dto.nested.StudentInfo;
import ru.mephi.orm_hibernate_demo.enums.EnrollmentRole;
import ru.mephi.orm_hibernate_demo.enums.EnrollmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class EnrollmentResponse {

    private UUID id;
    private EnrollmentRole role;
    private LocalDateTime enrolledAt;
    private EnrollmentStatus status;
    private Double finalGrade;
    private StudentInfo student;
    private CourseInfo course;
    private LocalDateTime createdAt;
}
