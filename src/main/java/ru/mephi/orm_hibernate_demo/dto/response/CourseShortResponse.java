package ru.mephi.orm_hibernate_demo.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class CourseShortResponse {

    private UUID id;
    private String code;
    private String title;
    private Integer credits;
}
