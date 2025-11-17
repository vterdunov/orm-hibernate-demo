package ru.mephi.orm_hibernate_demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.orm_hibernate_demo.dto.request.QuizSubmissionRequest;
import ru.mephi.orm_hibernate_demo.dto.response.QuizSubmissionResponse;
import ru.mephi.orm_hibernate_demo.entity.*;
import ru.mephi.orm_hibernate_demo.entity.person.Student;
import ru.mephi.orm_hibernate_demo.enums.EnrollmentStatus;
import ru.mephi.orm_hibernate_demo.exception.AppException;
import ru.mephi.orm_hibernate_demo.mapper.QuizSubmissionMapper;
import ru.mephi.orm_hibernate_demo.repository.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizSubmissionService {

    private final QuizRepository quizRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final PersonRepository personRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final QuizSubmissionMapper quizSubmissionMapper;

    @Transactional
    public QuizSubmissionResponse submit(UUID quizId, QuizSubmissionRequest request) {
        Quiz quiz = quizRepository.findEntityById(quizId);
        if (quiz.getQuestions().isEmpty()) {
            throw new AppException("Quiz has no questions", 400);
        }

        Student student = personRepository.findStudentEntityById(request.getStudentId());

        enrollmentRepository.findByStudentAndCourse(student.getId(), quiz.getCourse().getId())
                .filter(enrollment -> enrollment.getStatus() == EnrollmentStatus.ENROLLED
                        || enrollment.getStatus() == EnrollmentStatus.COMPLETED)
                .orElseThrow(() -> new AppException("Student is not enrolled in the course", 400));

        long attemptNo = quizSubmissionRepository.countByQuizIdAndStudentId(quizId, student.getId()) + 1;

        double score = evaluateScore(quiz, request.getAnswers());

        QuizSubmission submission = new QuizSubmission();
        submission.setQuiz(quiz);
        submission.setStudent(student);
        submission.setAttemptNo((int) attemptNo);
        submission.setScore(score);
        submission.setTakenAt(LocalDateTime.now());

        Set<UUID> selected = request.getAnswers()
                .values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        submission.setSelectedOptionIds(selected);

        QuizSubmission saved = quizSubmissionRepository.save(submission);
        return quizSubmissionMapper.toResponse(saved);
    }

    private double evaluateScore(Quiz quiz, Map<UUID, List<UUID>> answers) {
        int correctAnswers = 0;
        for (Question question : quiz.getQuestions()) {
            List<UUID> provided = answers.getOrDefault(question.getId(), List.of());
            Set<UUID> providedSet = new HashSet<>(provided);
            Set<UUID> correctSet = question.getOptions().stream()
                    .filter(AnswerOption::isCorrect)
                    .map(AnswerOption::getId)
                    .collect(Collectors.toSet());
            if (!providedSet.isEmpty() && providedSet.equals(correctSet)) {
                correctAnswers++;
            }
        }
        return Math.round(((double) correctAnswers / quiz.getQuestions().size()) * 1000.0) / 10.0;
    }

    @Transactional(readOnly = true)
    public List<QuizSubmissionResponse> findByQuizId(UUID quizId) {
        quizRepository.findEntityById(quizId);
        List<QuizSubmission> submissions = quizSubmissionRepository.findByQuizId(quizId);
        return quizSubmissionMapper.toResponseList(submissions);
    }

    @Transactional(readOnly = true)
    public List<QuizSubmissionResponse> findByStudentId(UUID studentId) {
        personRepository.findStudentEntityById(studentId);
        List<QuizSubmission> submissions = quizSubmissionRepository.findByStudentId(studentId);
        return quizSubmissionMapper.toResponseList(submissions);
    }
}
