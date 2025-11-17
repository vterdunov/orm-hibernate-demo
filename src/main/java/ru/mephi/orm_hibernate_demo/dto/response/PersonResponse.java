package ru.mephi.orm_hibernate_demo.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PersonResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String externalRef;
    private LocalDate birthDate;
    private String type;
    private LocalDateTime createdAt;
}
