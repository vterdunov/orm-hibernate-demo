package ru.mephi.orm_hibernate_demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mephi.orm_hibernate_demo.dto.request.CourseReviewRequest;
import ru.mephi.orm_hibernate_demo.dto.response.CourseReviewResponse;
import ru.mephi.orm_hibernate_demo.service.CourseReviewService;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/courses/{courseId}/reviews")
@RequiredArgsConstructor
public class CourseReviewController {

    private final CourseReviewService courseReviewService;

    @PostMapping
    public ResponseEntity<CourseReviewResponse> create(@PathVariable UUID courseId,
                                                       @Valid @RequestBody CourseReviewRequest request) {
        CourseReviewResponse response = courseReviewService.create(courseId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<CourseReviewResponse> findByCourse(@PathVariable UUID courseId) {
        return courseReviewService.findByCourse(courseId);
    }

    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID courseId, @PathVariable UUID reviewId) {
        courseReviewService.delete(reviewId);
    }
}
