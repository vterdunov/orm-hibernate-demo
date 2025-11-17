package ru.mephi.orm_hibernate_demo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.orm_hibernate_demo.entity.Quiz;
import ru.mephi.orm_hibernate_demo.exception.NotFoundException;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class QuizRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    public Quiz findEntityById(UUID id) {
        Quiz quiz = entityManager.find(Quiz.class, id);
        if (quiz == null) {
            throw new NotFoundException("Quiz not found with id: " + id);
        }
        return quiz;
    }

    public List<Quiz> findByCourseId(UUID courseId) {
        return entityManager.createQuery(
                        "SELECT q FROM Quiz q WHERE q.course.id = :courseId", Quiz.class)
                .setParameter("courseId", courseId)
                .getResultList();
    }

    @Transactional
    public Quiz save(Quiz quiz) {
        if (quiz.getId() == null) {
            entityManager.persist(quiz);
            return quiz;
        } else {
            return entityManager.merge(quiz);
        }
    }
}
