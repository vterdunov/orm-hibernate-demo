package ru.mephi.orm_hibernate_demo.dto.response;

import lombok.Data;
import ru.mephi.orm_hibernate_demo.dto.nested.CourseInfo;
import ru.mephi.orm_hibernate_demo.dto.nested.StudentInfo;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CourseReviewResponse {

    private UUID id;
    private int rating;
    private String comment;
    private StudentInfo student;
    private CourseInfo course;
    private LocalDateTime createdAt;
}
