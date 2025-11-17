package ru.mephi.orm_hibernate_demo.dto.nested;

import lombok.Data;

import java.util.UUID;

@Data
public class CourseInfo {

    private UUID id;
    private String code;
    private String title;
}
