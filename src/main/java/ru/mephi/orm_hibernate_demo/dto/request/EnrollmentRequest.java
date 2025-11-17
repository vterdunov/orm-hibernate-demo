package ru.mephi.orm_hibernate_demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.mephi.orm_hibernate_demo.enums.EnrollmentRole;

import java.util.UUID;

@Data
public class EnrollmentRequest {

    @NotNull
    private EnrollmentRole role;

    @NotNull
    private UUID studentId;

    @NotNull
    private UUID courseId;
}
