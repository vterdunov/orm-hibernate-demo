package ru.mephi.orm_hibernate_demo.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.*;
import ru.mephi.orm_hibernate_demo.dto.nested.AuthorInfo;
import ru.mephi.orm_hibernate_demo.dto.request.CourseRequest;
import ru.mephi.orm_hibernate_demo.dto.response.CourseResponse;
import ru.mephi.orm_hibernate_demo.dto.response.CourseShortResponse;
import ru.mephi.orm_hibernate_demo.entity.Course;
import ru.mephi.orm_hibernate_demo.entity.learning_resources.LearningResource;
import ru.mephi.orm_hibernate_demo.entity.person.Teacher;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CourseMapper {

    @PersistenceContext
    private EntityManager entityManager;

    @Mapping(target = "author", source = "authorId", qualifiedByName = "authorIdToTeacher")
    @Mapping(target = "featuredResource", source = "featuredResourceId", qualifiedByName = "resourceIdToResource")
    public abstract Course toEntity(CourseRequest request);

    @Mapping(target = "author", source = "author", qualifiedByName = "teacherToAuthorInfo")
    public abstract CourseResponse toResponse(Course course);

    public abstract CourseShortResponse toShortResponse(Course course);

    public abstract List<CourseResponse> toResponseList(List<Course> courses);

    public abstract List<CourseShortResponse> toShortResponseList(List<Course> courses);

    public abstract void updateEntityFromRequest(CourseRequest request, @MappingTarget Course course);

    @Named("teacherToAuthorInfo")
    protected AuthorInfo teacherToAuthorInfo(Teacher teacher) {
        if (teacher == null) {
            return null;
        }
        AuthorInfo info = new AuthorInfo();
        info.setId(teacher.getId());
        info.setFirstName(teacher.getFirstName());
        info.setLastName(teacher.getLastName());
        info.setAcademicTitle(teacher.getAcademicTitle());
        return info;
    }

    @Named("authorIdToTeacher")
    protected Teacher authorIdToTeacher(UUID authorId) {
        if (authorId == null) {
            return null;
        }
        Teacher teacher = new Teacher();
        teacher.setId(authorId);
        return teacher;
    }

    @Named("resourceIdToResource")
    protected LearningResource resourceIdToResource(UUID resourceId) {
        if (resourceId == null) {
            return null;
        }
        return entityManager.getReference(LearningResource.class, resourceId);
    }
}
