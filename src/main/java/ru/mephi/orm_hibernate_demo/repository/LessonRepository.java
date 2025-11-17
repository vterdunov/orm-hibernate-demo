package ru.mephi.orm_hibernate_demo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.orm_hibernate_demo.entity.Lesson;
import ru.mephi.orm_hibernate_demo.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LessonRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    public Lesson findEntityById(UUID id) {
        Lesson lesson = entityManager.find(Lesson.class, id);
        if (lesson == null) {
            throw new NotFoundException("Lesson not found with id: " + id);
        }
        return lesson;
    }

    public List<Lesson> findAll() {
        return entityManager.createQuery("SELECT l FROM Lesson l", Lesson.class)
                .getResultList();
    }

    public List<Lesson> findByCourseId(UUID courseId) {
        return entityManager.createQuery(
                "SELECT l FROM Lesson l WHERE l.course.id = :courseId", Lesson.class)
                .setParameter("courseId", courseId)
                .getResultList();
    }

    @Transactional
    public Lesson save(Lesson lesson) {
        if (lesson.getId() == null) {
            entityManager.persist(lesson);
            return lesson;
        } else {
            return entityManager.merge(lesson);
        }
    }

    @Transactional
    public void delete(UUID id) {
        Lesson lesson = findEntityById(id);
        entityManager.remove(lesson);
    }
}
