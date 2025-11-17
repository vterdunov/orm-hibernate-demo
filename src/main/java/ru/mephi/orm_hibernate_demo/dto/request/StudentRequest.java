package ru.mephi.orm_hibernate_demo.dto.request;

import lombok.Data;
import ru.mephi.orm_hibernate_demo.enums.StudentStatus;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class StudentRequest {

    private String firstName;
    private String lastName;
    private String externalRef;
    private LocalDate birthDate;
    private String studentNo;
    private StudentStatus status;
    private Set<String> emails = new HashSet<>();
    private Set<String> phones = new HashSet<>();
}
