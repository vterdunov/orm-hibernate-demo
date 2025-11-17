package ru.mephi.orm_hibernate_demo.dto.response;

import lombok.Data;
import ru.mephi.orm_hibernate_demo.dto.nested.AuthorInfo;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class CourseResponse {

    private UUID id;
    private String code;
    private String title;
    private String description;
    private Integer credits;
    private Set<String> keywords = new HashSet<>();
    private AuthorInfo author;
    private LocalDateTime createdAt;
}
