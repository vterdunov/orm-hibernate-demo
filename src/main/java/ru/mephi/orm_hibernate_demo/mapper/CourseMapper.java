package ru.mephi.orm_hibernate_demo.mapper;

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
public interface CourseMapper {


    @Mapping(target = "author", source = "authorId", qualifiedByName = "authorIdToTeacher")
    @Mapping(target = "featuredResource", source = "featuredResourceId", qualifiedByName = "resourceIdToResource")
    Course toEntity(CourseRequest request);

    @Mapping(target = "author", source = "author", qualifiedByName = "teacherToAuthorInfo")
    CourseResponse toResponse(Course course);

    CourseShortResponse toShortResponse(Course course);

    List<CourseResponse> toResponseList(List<Course> courses);

    List<CourseShortResponse> toShortResponseList(List<Course> courses);

    void updateEntityFromRequest(CourseRequest request, @MappingTarget Course course);

    @Named("teacherToAuthorInfo")
    default AuthorInfo teacherToAuthorInfo(Teacher teacher) {
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
    default Teacher authorIdToTeacher(UUID authorId) {
        if (authorId == null) {
            return null;
        }
        Teacher teacher = new Teacher();
        teacher.setId(authorId);
        return teacher;
    }

    @Named("resourceIdToResource")
    default LearningResource resourceIdToResource(UUID resourceId) {
        if (resourceId == null) {
            return null;
        }
        return null;
    }
}
