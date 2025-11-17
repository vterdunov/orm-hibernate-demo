package ru.mephi.orm_hibernate_demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mephi.orm_hibernate_demo.dto.request.EnrollmentRequest;
import ru.mephi.orm_hibernate_demo.dto.response.CourseShortResponse;
import ru.mephi.orm_hibernate_demo.dto.response.EnrollmentResponse;
import ru.mephi.orm_hibernate_demo.dto.response.StudentResponse;
import ru.mephi.orm_hibernate_demo.enums.EnrollmentRole;
import ru.mephi.orm_hibernate_demo.service.EnrollmentService;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentResponse> create(@Valid @RequestBody EnrollmentRequest request) {
        EnrollmentResponse response = enrollmentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/students/{studentId}/courses/{courseId}")
    public ResponseEntity<EnrollmentResponse> enrollStudent(@PathVariable UUID studentId,
                                                            @PathVariable UUID courseId,
                                                            @RequestParam(defaultValue = "STUDENT") EnrollmentRole role) {
        EnrollmentResponse response = enrollmentService.enrollStudent(studentId, courseId, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<EnrollmentResponse> findAll() {
        return enrollmentService.findAll();
    }

    @GetMapping("/{id}")
    public EnrollmentResponse findById(@PathVariable UUID id) {
        return enrollmentService.findById(id);
    }

    @GetMapping("/students/{studentId}")
    public List<EnrollmentResponse> findByStudent(@PathVariable UUID studentId) {
        return enrollmentService.findByStudentId(studentId);
    }

    @GetMapping("/courses/{courseId}")
    public List<EnrollmentResponse> findByCourse(@PathVariable UUID courseId) {
        return enrollmentService.findByCourseId(courseId);
    }

    @GetMapping("/students/{studentId}/courses")
    public List<CourseShortResponse> getStudentCourses(@PathVariable UUID studentId) {
        return enrollmentService.getStudentCourses(studentId);
    }

    @GetMapping("/courses/{courseId}/students")
    public List<StudentResponse> getCourseStudents(@PathVariable UUID courseId) {
        return enrollmentService.getCourseStudents(courseId);
    }

    @PostMapping("/{id}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void complete(@PathVariable UUID id, @RequestParam Double finalGrade) {
        enrollmentService.completeEnrollment(id, finalGrade);
    }

    @PostMapping("/{id}/drop")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void drop(@PathVariable UUID id) {
        enrollmentService.dropEnrollment(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        enrollmentService.delete(id);
    }
}
