package ru.mephi.orm_hibernate_demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.mephi.orm_hibernate_demo.dto.nested.StudentInfo;
import ru.mephi.orm_hibernate_demo.dto.response.QuizSubmissionResponse;
import ru.mephi.orm_hibernate_demo.entity.QuizSubmission;
import ru.mephi.orm_hibernate_demo.entity.person.Student;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QuizSubmissionMapper {

    @Mapping(target = "student", source = "student", qualifiedByName = "studentToInfo")
    @Mapping(target = "quizId", source = "quiz.id")
    QuizSubmissionResponse toResponse(QuizSubmission submission);

    List<QuizSubmissionResponse> toResponseList(List<QuizSubmission> submissions);

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
