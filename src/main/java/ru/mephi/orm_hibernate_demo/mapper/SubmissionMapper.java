package ru.mephi.orm_hibernate_demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.mephi.orm_hibernate_demo.dto.nested.AssignmentInfo;
import ru.mephi.orm_hibernate_demo.dto.nested.StudentInfo;
import ru.mephi.orm_hibernate_demo.dto.response.SubmissionResponse;
import ru.mephi.orm_hibernate_demo.entity.Assignment;
import ru.mephi.orm_hibernate_demo.entity.Submission;
import ru.mephi.orm_hibernate_demo.entity.person.Student;

import java.util.List;

@Mapper(componentModel = "spring", uses = GradeMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubmissionMapper {

    @Mapping(target = "assignment", source = "assignment", qualifiedByName = "assignmentToInfo")
    @Mapping(target = "student", source = "student", qualifiedByName = "studentToInfo")
    SubmissionResponse toResponse(Submission submission);

    List<SubmissionResponse> toResponseList(List<Submission> submissions);

    @Named("assignmentToInfo")
    default AssignmentInfo assignmentToInfo(Assignment assignment) {
        if (assignment == null) {
            return null;
        }
        AssignmentInfo info = new AssignmentInfo();
        info.setId(assignment.getId());
        info.setTitle(assignment.getTitle());
        info.setDueDate(assignment.getDueDate());
        if (assignment.getCourse() != null) {
            info.setCourseId(assignment.getCourse().getId());
        }
        return info;
    }

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
}
