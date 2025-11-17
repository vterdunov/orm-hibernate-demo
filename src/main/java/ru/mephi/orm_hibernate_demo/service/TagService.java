package ru.mephi.orm_hibernate_demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.orm_hibernate_demo.dto.request.TagRequest;
import ru.mephi.orm_hibernate_demo.dto.response.CourseShortResponse;
import ru.mephi.orm_hibernate_demo.dto.response.TagResponse;
import ru.mephi.orm_hibernate_demo.dto.response.TagShortResponse;
import ru.mephi.orm_hibernate_demo.entity.Tag;
import ru.mephi.orm_hibernate_demo.exception.AppException;
import ru.mephi.orm_hibernate_demo.mapper.TagMapper;
import ru.mephi.orm_hibernate_demo.repository.TagRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final CourseService courseService;

    @Transactional
    public TagResponse create(TagRequest request) {
        tagRepository.findBySlug(request.getSlug()).ifPresent(tag -> {
            throw new AppException("Tag with slug '" + request.getSlug() + "' already exists", 409);
        });

        Tag tag = tagMapper.toEntity(request);
        Tag saved = tagRepository.save(tag);
        return tagMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public TagResponse findById(UUID id) {
        Tag tag = tagRepository.findEntityById(id);
        return tagMapper.toResponse(tag);
    }

    @Transactional(readOnly = true)
    public TagResponse findBySlug(String slug) {
        Tag tag = tagRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException("Tag not found with slug: " + slug, 404));
        return tagMapper.toResponse(tag);
    }

    @Transactional(readOnly = true)
    public List<TagResponse> findAll() {
        List<Tag> tags = tagRepository.findAll();
        return tagMapper.toResponseList(tags);
    }

    @Transactional(readOnly = true)
    public List<TagShortResponse> findAllShort() {
        List<Tag> tags = tagRepository.findAll();
        return tagMapper.toShortResponseList(tags);
    }

    @Transactional(readOnly = true)
    public List<CourseShortResponse> getTagCourses(UUID tagId) {
        Tag tag = tagRepository.findEntityById(tagId);

        return tag.getCourses().stream()
                .map(course -> courseService.findById(course.getId()))
                .map(courseResponse -> {
                    CourseShortResponse shortResponse = new CourseShortResponse();
                    shortResponse.setId(courseResponse.getId());
                    shortResponse.setCode(courseResponse.getCode());
                    shortResponse.setTitle(courseResponse.getTitle());
                    shortResponse.setCredits(courseResponse.getCredits());
                    return shortResponse;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public TagResponse update(UUID id, TagRequest request) {
        Tag tag = tagRepository.findEntityById(id);
        tagMapper.updateEntityFromRequest(request, tag);

        Tag updated = tagRepository.save(tag);
        return tagMapper.toResponse(updated);
    }

    @Transactional
    public void delete(UUID id) {
        Tag tag = tagRepository.findEntityById(id);

        if (!tag.getCourses().isEmpty()) {
            throw new AppException("Cannot delete tag with associated courses. Found " + tag.getCourses().size() + " courses.", 409);
        }

        tagRepository.delete(id);
    }

    @Transactional
    public void forceDelete(UUID id) {
        Tag tag = tagRepository.findEntityById(id);

        tag.getCourses().forEach(course ->
                course.getTags().removeIf(existing -> existing.getId().equals(tag.getId()))
        );
        tag.getCourses().clear();

        tagRepository.detachTagRelations(id);
        tagRepository.delete(id);
    }
}
