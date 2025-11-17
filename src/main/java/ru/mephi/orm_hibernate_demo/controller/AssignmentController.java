package ru.mephi.orm_hibernate_demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mephi.orm_hibernate_demo.dto.request.AssignmentRequest;
import ru.mephi.orm_hibernate_demo.dto.response.AssignmentResponse;
import ru.mephi.orm_hibernate_demo.service.AssignmentService;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping("/courses/{courseId}/assignments")
    public ResponseEntity<AssignmentResponse> create(@PathVariable UUID courseId,
                                                     @Valid @RequestBody AssignmentRequest request) {
        AssignmentResponse response = assignmentService.create(courseId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/courses/{courseId}/assignments")
    public List<AssignmentResponse> findByCourse(@PathVariable UUID courseId) {
        return assignmentService.findByCourseId(courseId);
    }

    @GetMapping("/assignments/{id}")
    public AssignmentResponse findById(@PathVariable UUID id) {
        return assignmentService.findById(id);
    }

    @PutMapping("/assignments/{id}")
    public AssignmentResponse update(@PathVariable UUID id, @Valid @RequestBody AssignmentRequest request) {
        return assignmentService.update(id, request);
    }

    @DeleteMapping("/assignments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        assignmentService.delete(id);
    }
}
