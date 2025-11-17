package ru.mephi.orm_hibernate_demo.mapper;

import org.mapstruct.*;
import ru.mephi.orm_hibernate_demo.dto.nested.CourseInfo;
import ru.mephi.orm_hibernate_demo.dto.request.AssignmentRequest;
import ru.mephi.orm_hibernate_demo.dto.response.AssignmentResponse;
import ru.mephi.orm_hibernate_demo.entity.Assignment;
import ru.mephi.orm_hibernate_demo.entity.Course;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AssignmentMapper {

    @Mapping(target = "course", source = "courseId", qualifiedByName = "courseIdToCourse")
    Assignment toEntity(AssignmentRequest request);

    @Mapping(target = "course", source = "course", qualifiedByName = "courseToCourseInfo")
    AssignmentResponse toResponse(Assignment assignment);

    List<AssignmentResponse> toResponseList(List<Assignment> assignments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "course", ignore = true)
    void updateEntityFromRequest(AssignmentRequest request, @MappingTarget Assignment assignment);

    @Named("courseToCourseInfo")
    default CourseInfo courseToCourseInfo(Course course) {
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
    default Course courseIdToCourse(UUID courseId) {
        if (courseId == null) {
            return null;
        }
        Course course = new Course();
        course.setId(courseId);
        return course;
    }
}
