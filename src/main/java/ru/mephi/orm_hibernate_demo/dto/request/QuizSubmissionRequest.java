package ru.mephi.orm_hibernate_demo.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class QuizSubmissionRequest {

    @NotNull
    private UUID studentId;

    @NotEmpty
    private Map<UUID, List<UUID>> answers = new HashMap<>();
}
