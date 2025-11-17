package ru.mephi.orm_hibernate_demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class SubmissionRequest {

    @NotNull
    private UUID studentId;

    @NotBlank
    @Size(max = 500)
    private String contentUrl;
}
