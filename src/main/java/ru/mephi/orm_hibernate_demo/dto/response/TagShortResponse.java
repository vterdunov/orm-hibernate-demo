package ru.mephi.orm_hibernate_demo.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class TagShortResponse {

    private UUID id;
    private String name;
    private String slug;
}
