package ru.mephi.orm_hibernate_demo.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TeacherRequest {

    private String firstName;
    private String lastName;
    private String externalRef;
    private LocalDate birthDate;
    private String academicTitle;
}
