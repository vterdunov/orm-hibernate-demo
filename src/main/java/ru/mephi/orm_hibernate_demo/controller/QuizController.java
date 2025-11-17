package ru.mephi.orm_hibernate_demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mephi.orm_hibernate_demo.dto.request.QuizRequest;
import ru.mephi.orm_hibernate_demo.dto.response.QuizResponse;
import ru.mephi.orm_hibernate_demo.service.QuizService;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping("/courses/{courseId}/quizzes")
    public ResponseEntity<QuizResponse> create(@PathVariable UUID courseId,
                                               @Valid @RequestBody QuizRequest request) {
        QuizResponse response = quizService.create(courseId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/courses/{courseId}/quizzes")
    public List<QuizResponse> findByCourse(@PathVariable UUID courseId) {
        return quizService.findByCourseId(courseId);
    }

    @GetMapping("/quizzes/{id}")
    public QuizResponse findById(@PathVariable UUID id) {
        return quizService.findById(id);
    }
}
