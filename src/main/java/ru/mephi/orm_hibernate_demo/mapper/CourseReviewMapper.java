package ru.mephi.orm_hibernate_demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.mephi.orm_hibernate_demo.dto.nested.CourseInfo;
import ru.mephi.orm_hibernate_demo.dto.nested.StudentInfo;
import ru.mephi.orm_hibernate_demo.dto.response.CourseReviewResponse;
import ru.mephi.orm_hibernate_demo.entity.Course;
import ru.mephi.orm_hibernate_demo.entity.CourseReview;
import ru.mephi.orm_hibernate_demo.entity.person.Student;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseReviewMapper {

    @Mapping(target = "student", source = "student", qualifiedByName = "studentToInfo")
    @Mapping(target = "course", source = "course", qualifiedByName = "courseToInfo")
    CourseReviewResponse toResponse(CourseReview review);

    List<CourseReviewResponse> toResponseList(List<CourseReview> reviews);

    @Named("studentToInfo")
    default StudentInfo studentToInfo(Student student) {
        if (student == null) {
            return null;
        }
        StudentInfo info = new StudentInfo();
        info.setId(student.getId());
        info.setFirstName(student.getFirstName());
        info.setLastName(student.getLastName());
        info.setStudentNo(student.getStudentNo());
        return info;
    }

    @Named("courseToInfo")
    default CourseInfo courseToInfo(Course course) {
        if (course == null) {
            return null;
        }
        CourseInfo info = new CourseInfo();
        info.setId(course.getId());
        info.setCode(course.getCode());
        info.setTitle(course.getTitle());
        return info;
    }
}
