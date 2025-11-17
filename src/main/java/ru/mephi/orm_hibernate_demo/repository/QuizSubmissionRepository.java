package ru.mephi.orm_hibernate_demo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.orm_hibernate_demo.entity.QuizSubmission;
import ru.mephi.orm_hibernate_demo.exception.NotFoundException;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class QuizSubmissionRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    public QuizSubmission findEntityById(UUID id) {
        QuizSubmission submission = entityManager.find(QuizSubmission.class, id);
        if (submission == null) {
            throw new NotFoundException("Quiz submission not found with id: " + id);
        }
        return submission;
    }

    public List<QuizSubmission> findByQuizId(UUID quizId) {
        return entityManager.createQuery(
                        "SELECT s FROM QuizSubmission s WHERE s.quiz.id = :quizId", QuizSubmission.class)
                .setParameter("quizId", quizId)
                .getResultList();
    }

    public List<QuizSubmission> findByStudentId(UUID studentId) {
        return entityManager.createQuery(
                        "SELECT s FROM QuizSubmission s WHERE s.student.id = :studentId", QuizSubmission.class)
                .setParameter("studentId", studentId)
                .getResultList();
    }

    public long countByQuizIdAndStudentId(UUID quizId, UUID studentId) {
        return entityManager.createQuery(
                        "SELECT COUNT(s) FROM QuizSubmission s WHERE s.quiz.id = :quizId AND s.student.id = :studentId",
                        Long.class)
                .setParameter("quizId", quizId)
                .setParameter("studentId", studentId)
                .getSingleResult();
    }

    @Transactional
    public QuizSubmission save(QuizSubmission submission) {
        if (submission.getId() == null) {
            entityManager.persist(submission);
            return submission;
        } else {
            return entityManager.merge(submission);
        }
    }
}
