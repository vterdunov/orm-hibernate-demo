package ru.mephi.orm_hibernate_demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mephi.orm_hibernate_demo.dto.request.QuizSubmissionRequest;
import ru.mephi.orm_hibernate_demo.dto.response.QuizSubmissionResponse;
import ru.mephi.orm_hibernate_demo.service.QuizSubmissionService;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuizSubmissionController {

    private final QuizSubmissionService quizSubmissionService;

    @PostMapping("/quizzes/{quizId}/submissions")
    public ResponseEntity<QuizSubmissionResponse> submit(@PathVariable UUID quizId,
                                                         @Valid @RequestBody QuizSubmissionRequest request) {
        QuizSubmissionResponse response = quizSubmissionService.submit(quizId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/quizzes/{quizId}/submissions")
    public List<QuizSubmissionResponse> findByQuiz(@PathVariable UUID quizId) {
        return quizSubmissionService.findByQuizId(quizId);
    }

    @GetMapping("/students/{studentId}/quiz-submissions")
    public List<QuizSubmissionResponse> findByStudent(@PathVariable UUID studentId) {
        return quizSubmissionService.findByStudentId(studentId);
    }
}
