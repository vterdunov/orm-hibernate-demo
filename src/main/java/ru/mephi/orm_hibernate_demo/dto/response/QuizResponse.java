package ru.mephi.orm_hibernate_demo.dto.response;

import lombok.Data;
import ru.mephi.orm_hibernate_demo.dto.nested.CourseInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class QuizResponse {

    private UUID id;
    private String title;
    private String description;
    private Integer timeLimitMinutes;
    private CourseInfo course;
    private List<QuestionResponse> questions = new ArrayList<>();
}
