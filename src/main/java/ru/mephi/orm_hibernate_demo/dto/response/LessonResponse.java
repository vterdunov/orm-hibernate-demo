package ru.mephi.orm_hibernate_demo.dto.response;

import lombok.Data;
import ru.mephi.orm_hibernate_demo.dto.nested.CourseInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class LessonResponse {

    private UUID id;
    private String title;
    private String syllabus;
    private List<String> resources = new ArrayList<>();
    private CourseInfo course;
    private LocalDateTime createdAt;
}
