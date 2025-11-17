package ru.mephi.orm_hibernate_demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.mephi.orm_hibernate_demo.dto.request.TagRequest;
import ru.mephi.orm_hibernate_demo.dto.response.CourseShortResponse;
import ru.mephi.orm_hibernate_demo.dto.response.TagResponse;
import ru.mephi.orm_hibernate_demo.dto.response.TagShortResponse;
import ru.mephi.orm_hibernate_demo.service.TagService;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    public ResponseEntity<TagResponse> create(@Valid @RequestBody TagRequest request) {
        TagResponse response = tagService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<TagResponse> findAll() {
        return tagService.findAll();
    }

    @GetMapping("/short")
    public List<TagShortResponse> findAllShort() {
        return tagService.findAllShort();
    }

    @GetMapping("/{id}")
    public TagResponse findById(@PathVariable UUID id) {
        return tagService.findById(id);
    }

    @PutMapping("/{id}")
    public TagResponse update(@PathVariable UUID id, @Valid @RequestBody TagRequest request) {
        return tagService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id,
                       @RequestParam(name = "force", defaultValue = "false") boolean forceDelete) {
        if (forceDelete) {
            tagService.forceDelete(id);
        } else {
            tagService.delete(id);
        }
    }

    @GetMapping("/{id}/courses")
    public List<CourseShortResponse> getCourses(@PathVariable UUID id) {
        return tagService.getTagCourses(id);
    }
}
