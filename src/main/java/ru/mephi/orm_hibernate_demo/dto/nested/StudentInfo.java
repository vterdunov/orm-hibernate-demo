package ru.mephi.orm_hibernate_demo.dto.nested;

import lombok.Data;

import java.util.UUID;

@Data
public class StudentInfo {

    private UUID id;
    private String firstName;
    private String lastName;
    private String studentNo;
}
