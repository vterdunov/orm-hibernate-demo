package ru.mephi.orm_hibernate_demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.orm_hibernate_demo.dto.request.AnswerOptionRequest;
import ru.mephi.orm_hibernate_demo.dto.request.QuestionRequest;
import ru.mephi.orm_hibernate_demo.dto.request.QuizRequest;
import ru.mephi.orm_hibernate_demo.dto.response.QuizResponse;
import ru.mephi.orm_hibernate_demo.entity.*;
import ru.mephi.orm_hibernate_demo.mapper.QuizMapper;
import ru.mephi.orm_hibernate_demo.repository.CourseRepository;
import ru.mephi.orm_hibernate_demo.repository.QuizRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final QuizMapper quizMapper;

    @Transactional
    public QuizResponse create(UUID courseId, QuizRequest request) {
        Course course = courseRepository.findEntityById(courseId);

        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setTimeLimitMinutes(request.getTimeLimitMinutes());
        quiz.setCourse(course);

        for (QuestionRequest questionRequest : request.getQuestions()) {
            Question question = new Question();
            question.setText(questionRequest.getText());
            question.setType(questionRequest.getType());
            question.setQuiz(quiz);

            for (AnswerOptionRequest optionRequest : questionRequest.getOptions()) {
                AnswerOption option = new AnswerOption();
                option.setText(optionRequest.getText());
                option.setCorrect(optionRequest.isCorrect());
                option.setQuestion(question);
                question.getOptions().add(option);
            }

            quiz.getQuestions().add(question);
        }

        Quiz saved = quizRepository.save(quiz);
        return quizMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public QuizResponse findById(UUID id) {
        Quiz quiz = quizRepository.findEntityById(id);
        return quizMapper.toResponse(quiz);
    }

    @Transactional(readOnly = true)
    public List<QuizResponse> findByCourseId(UUID courseId) {
        courseRepository.findEntityById(courseId);
        List<Quiz> quizzes = quizRepository.findByCourseId(courseId);
        return quizMapper.toResponseList(quizzes);
    }
}
