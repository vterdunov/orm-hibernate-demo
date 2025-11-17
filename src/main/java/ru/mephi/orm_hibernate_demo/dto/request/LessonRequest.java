package ru.mephi.orm_hibernate_demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class LessonRequest {

    @NotBlank
    @Size(max = 255)
    private String title;

    @Size(max = 4000)
    private String syllabus;

    private List<String> resources = new ArrayList<>();

    private UUID courseId;

    private UUID primaryResourceId;
}
