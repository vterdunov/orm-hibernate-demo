package ru.mephi.orm_hibernate_demo.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.*;
import ru.mephi.orm_hibernate_demo.dto.nested.CourseInfo;
import ru.mephi.orm_hibernate_demo.dto.request.LessonRequest;
import ru.mephi.orm_hibernate_demo.dto.response.LessonResponse;
import ru.mephi.orm_hibernate_demo.entity.Course;
import ru.mephi.orm_hibernate_demo.entity.Lesson;
import ru.mephi.orm_hibernate_demo.entity.learning_resources.LearningResource;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class LessonMapper {

    @PersistenceContext
    private EntityManager entityManager;

    @Mapping(target = "course", source = "courseId", qualifiedByName = "courseIdToCourse")
    @Mapping(target = "primaryResource", source = "primaryResourceId", qualifiedByName = "resourceIdToResource")
    public abstract Lesson toEntity(LessonRequest request);

    @Mapping(target = "course", source = "course", qualifiedByName = "courseToCourseInfo")
    public abstract LessonResponse toResponse(Lesson lesson);

    public abstract List<LessonResponse> toResponseList(List<Lesson> lessons);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "course", ignore = true)
    public abstract void updateEntityFromRequest(LessonRequest request, @MappingTarget Lesson lesson);

    @Named("courseToCourseInfo")
    protected CourseInfo courseToCourseInfo(Course course) {
        if (course == null) {
            return null;
        }
        CourseInfo info = new CourseInfo();
        info.setId(course.getId());
        info.setCode(course.getCode());
        info.setTitle(course.getTitle());
        return info;
    }

    @Named("courseIdToCourse")
    protected Course courseIdToCourse(UUID courseId) {
        if (courseId == null) {
            return null;
        }
        Course course = new Course();
        course.setId(courseId);
        return course;
    }

    @Named("resourceIdToResource")
    protected LearningResource resourceIdToResource(UUID resourceId) {
        if (resourceId == null) {
            return null;
        }
        return entityManager.getReference(LearningResource.class, resourceId);
    }
}
