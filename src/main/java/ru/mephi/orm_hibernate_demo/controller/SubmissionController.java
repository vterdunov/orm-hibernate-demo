package ru.mephi.orm_hibernate_demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mephi.orm_hibernate_demo.dto.request.GradeRequest;
import ru.mephi.orm_hibernate_demo.dto.request.SubmissionRequest;
import ru.mephi.orm_hibernate_demo.dto.response.GradeResponse;
import ru.mephi.orm_hibernate_demo.dto.response.SubmissionResponse;
import ru.mephi.orm_hibernate_demo.service.SubmissionService;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping("/assignments/{assignmentId}/submissions")
    public ResponseEntity<SubmissionResponse> submit(@PathVariable UUID assignmentId,
                                                     @Valid @RequestBody SubmissionRequest request) {
        SubmissionResponse response = submissionService.submit(assignmentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/assignments/{assignmentId}/submissions")
    public List<SubmissionResponse> findByAssignment(@PathVariable UUID assignmentId) {
        return submissionService.findByAssignmentId(assignmentId);
    }

    @GetMapping("/students/{studentId}/submissions")
    public List<SubmissionResponse> findByStudent(@PathVariable UUID studentId) {
        return submissionService.findByStudentId(studentId);
    }

    @GetMapping("/submissions/{id}")
    public SubmissionResponse findById(@PathVariable UUID id) {
        return submissionService.findById(id);
    }

    @PostMapping("/submissions/{id}/grade")
    public GradeResponse grade(@PathVariable UUID id, @Valid @RequestBody GradeRequest request) {
        return submissionService.gradeSubmission(id, request);
    }
}
