package ru.mephi.orm_hibernate_demo.dto.response;

import lombok.Data;
import ru.mephi.orm_hibernate_demo.enums.StudentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class StudentResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String externalRef;
    private LocalDate birthDate;
    private String studentNo;
    private StudentStatus status;
    private Set<String> emails = new HashSet<>();
    private Set<String> phones = new HashSet<>();
    private LocalDateTime createdAt;
}
