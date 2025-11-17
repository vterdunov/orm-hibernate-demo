package ru.mephi.orm_hibernate_demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mephi.orm_hibernate_demo.dto.request.CourseRequest;
import ru.mephi.orm_hibernate_demo.dto.request.LessonRequest;
import ru.mephi.orm_hibernate_demo.dto.response.CourseResponse;
import ru.mephi.orm_hibernate_demo.dto.response.CourseShortResponse;
import ru.mephi.orm_hibernate_demo.dto.response.LessonResponse;
import ru.mephi.orm_hibernate_demo.dto.response.TagShortResponse;
import ru.mephi.orm_hibernate_demo.service.CourseService;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseRequest request) {
        CourseResponse response = courseService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<CourseResponse> findAll() {
        return courseService.findAll();
    }

    @GetMapping("/short")
    public List<CourseShortResponse> findAllShort() {
        return courseService.findAllShort();
    }

    @GetMapping("/{id}")
    public CourseResponse findById(@PathVariable UUID id) {
        return courseService.findById(id);
    }

    @PutMapping("/{id}")
    public CourseResponse update(@PathVariable UUID id, @Valid @RequestBody CourseRequest request) {
        return courseService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        courseService.delete(id);
    }

    @PostMapping("/{courseId}/lessons")
    public ResponseEntity<LessonResponse> addLesson(@PathVariable UUID courseId,
                                                    @Valid @RequestBody LessonRequest request) {
        request.setCourseId(courseId);
        LessonResponse response = courseService.addLessonToCourse(courseId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{courseId}/lessons")
    public List<LessonResponse> getCourseLessons(@PathVariable UUID courseId) {
        return courseService.getCourseLessons(courseId);
    }

    @PostMapping("/{courseId}/tags/{tagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addTag(@PathVariable UUID courseId, @PathVariable UUID tagId) {
        courseService.addTagToCourse(courseId, tagId);
    }

    @DeleteMapping("/{courseId}/tags/{tagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeTag(@PathVariable UUID courseId, @PathVariable UUID tagId) {
        courseService.removeTagFromCourse(courseId, tagId);
    }

    @GetMapping("/{courseId}/tags")
    public List<TagShortResponse> getCourseTags(@PathVariable UUID courseId) {
        return courseService.getCourseTagsShort(courseId);
    }
}
