package ru.mephi.orm_hibernate_demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mephi.orm_hibernate_demo.dto.request.LessonRequest;
import ru.mephi.orm_hibernate_demo.dto.response.LessonResponse;
import ru.mephi.orm_hibernate_demo.service.LessonService;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping
    public List<LessonResponse> findAll() {
        return lessonService.findAll();
    }

    @GetMapping("/{id}")
    public LessonResponse findById(@PathVariable UUID id) {
        return lessonService.findById(id);
    }

    @PutMapping("/{id}")
    public LessonResponse update(@PathVariable UUID id, @Valid @RequestBody LessonRequest request) {
        return lessonService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        lessonService.delete(id);
    }
}
