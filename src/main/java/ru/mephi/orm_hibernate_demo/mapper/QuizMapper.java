package ru.mephi.orm_hibernate_demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.mephi.orm_hibernate_demo.dto.nested.CourseInfo;
import ru.mephi.orm_hibernate_demo.dto.response.AnswerOptionResponse;
import ru.mephi.orm_hibernate_demo.dto.response.QuestionResponse;
import ru.mephi.orm_hibernate_demo.dto.response.QuizResponse;
import ru.mephi.orm_hibernate_demo.entity.AnswerOption;
import ru.mephi.orm_hibernate_demo.entity.Course;
import ru.mephi.orm_hibernate_demo.entity.Question;
import ru.mephi.orm_hibernate_demo.entity.Quiz;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QuizMapper {

    @Mapping(target = "course", source = "course", qualifiedByName = "courseToCourseInfo")
    @Mapping(target = "questions", source = "questions")
    QuizResponse toResponse(Quiz quiz);

    List<QuizResponse> toResponseList(List<Quiz> quizzes);

    @Mapping(target = "options", source = "options")
    QuestionResponse questionToResponse(Question question);

    AnswerOptionResponse optionToResponse(AnswerOption option);

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
}
