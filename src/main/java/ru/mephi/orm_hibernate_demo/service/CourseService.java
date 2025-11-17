package ru.mephi.orm_hibernate_demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.orm_hibernate_demo.dto.request.CourseRequest;
import ru.mephi.orm_hibernate_demo.dto.request.LessonRequest;
import ru.mephi.orm_hibernate_demo.dto.response.CourseResponse;
import ru.mephi.orm_hibernate_demo.dto.response.CourseShortResponse;
import ru.mephi.orm_hibernate_demo.dto.response.LessonResponse;
import ru.mephi.orm_hibernate_demo.dto.response.TagShortResponse;
import ru.mephi.orm_hibernate_demo.entity.Course;
import ru.mephi.orm_hibernate_demo.entity.Tag;
import ru.mephi.orm_hibernate_demo.entity.person.Teacher;
import ru.mephi.orm_hibernate_demo.mapper.CourseMapper;
import ru.mephi.orm_hibernate_demo.repository.CourseRepository;
import ru.mephi.orm_hibernate_demo.repository.PersonRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final PersonRepository personRepository;
    private final CourseMapper courseMapper;
    @Autowired @Lazy private TagService tagService;
    private final LessonService lessonService;

    @Transactional
    public CourseResponse create(CourseRequest request) {
        Teacher author = personRepository.findTeacherEntityById(request.getAuthorId());

        Course course = courseMapper.toEntity(request);
        course.setAuthor(author);

        Course saved = courseRepository.save(course);
        return courseMapper.toResponse(saved);
    }

    @Transactional
    public CourseResponse createWithTags(CourseRequest request, List<UUID> tagIds) {
        CourseResponse courseResponse = create(request);

        for (UUID tagId : tagIds) {
            addTagToCourse(courseResponse.getId(), tagId);
        }

        return findById(courseResponse.getId());
    }

    @Transactional(readOnly = true)
    public CourseResponse findById(UUID id) {
        Course course = courseRepository.findEntityById(id);
        return courseMapper.toResponse(course);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> findAll() {
        List<Course> courses = courseRepository.findAll();
        return courseMapper.toResponseList(courses);
    }

    @Transactional(readOnly = true)
    public List<CourseShortResponse> findAllShort() {
        List<Course> courses = courseRepository.findAll();
        return courseMapper.toShortResponseList(courses);
    }

    @Transactional
    public CourseResponse update(UUID id, CourseRequest request) {
        Course course = courseRepository.findEntityById(id);

        if (request.getAuthorId() != null && !request.getAuthorId().equals(course.getAuthor().getId())) {
            Teacher newAuthor = personRepository.findTeacherEntityById(request.getAuthorId());
            course.setAuthor(newAuthor);
        }

        courseMapper.updateEntityFromRequest(request, course);

        Course updated = courseRepository.save(course);
        return courseMapper.toResponse(updated);
    }

    @Transactional
    public void addKeyword(UUID courseId, String keyword) {
        Course course = courseRepository.findEntityById(courseId);
        course.getKeywords().add(keyword);
    }

    @Transactional
    public void removeKeyword(UUID courseId, String keyword) {
        Course course = courseRepository.findEntityById(courseId);
        course.getKeywords().remove(keyword);
    }

    @Transactional
    public void setKeywords(UUID courseId, Set<String> keywords) {
        Course course = courseRepository.findEntityById(courseId);
        course.getKeywords().clear();
        course.getKeywords().addAll(keywords);
    }

    @Transactional
    public void addTagToCourse(UUID courseId, UUID tagId) {
        tagService.findById(tagId);

        Course course = courseRepository.findEntityById(courseId);
        Tag tag = new Tag();
        tag.setId(tagId);
        course.getTags().add(tag);
    }

    @Transactional
    public void addTagBySLug(UUID courseId, String slug) {
        tagService.findBySlug(slug);

        Course course = courseRepository.findEntityById(courseId);
        Tag tag = new Tag();
        tag.setSlug(slug);
        course.getTags().add(tag);
    }

    @Transactional
    public void removeTagFromCourse(UUID courseId, UUID tagId) {
        Course course = courseRepository.findEntityById(courseId);
        course.getTags().removeIf(tag -> tag.getId().equals(tagId));
    }

    @Transactional(readOnly = true)
    public List<TagShortResponse> getCourseTagsShort(UUID courseId) {
        Course course = courseRepository.findEntityById(courseId);
        List<UUID> tagIds = course.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toList());

        return tagIds.stream()
                .map(tagService::findById)
                .map(tagResponse -> {
                    TagShortResponse shortResponse = new TagShortResponse();
                    shortResponse.setId(tagResponse.getId());
                    shortResponse.setName(tagResponse.getName());
                    shortResponse.setSlug(tagResponse.getSlug());
                    return shortResponse;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public LessonResponse addLessonToCourse(UUID courseId, LessonRequest lessonRequest) {
        courseRepository.findEntityById(courseId);

        lessonRequest.setCourseId(courseId);
        return lessonService.create(lessonRequest);
    }

    @Transactional(readOnly = true)
    public List<LessonResponse> getCourseLessons(UUID courseId) {
        return lessonService.findByCourseId(courseId);
    }

    @Transactional
    public void delete(UUID id) {
        courseRepository.delete(id);
    }
}
