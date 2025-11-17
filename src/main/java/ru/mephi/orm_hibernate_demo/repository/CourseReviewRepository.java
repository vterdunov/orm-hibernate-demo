package ru.mephi.orm_hibernate_demo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.orm_hibernate_demo.entity.CourseReview;
import ru.mephi.orm_hibernate_demo.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CourseReviewRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    public CourseReview findEntityById(UUID id) {
        CourseReview review = entityManager.find(CourseReview.class, id);
        if (review == null) {
            throw new NotFoundException("Course review not found with id: " + id);
        }
        return review;
    }

    public List<CourseReview> findByCourseId(UUID courseId) {
        return entityManager.createQuery(
                        "SELECT r FROM CourseReview r WHERE r.course.id = :courseId", CourseReview.class)
                .setParameter("courseId", courseId)
                .getResultList();
    }

    public Optional<CourseReview> findByCourseAndStudent(UUID courseId, UUID studentId) {
        List<CourseReview> results = entityManager.createQuery(
                        "SELECT r FROM CourseReview r WHERE r.course.id = :courseId AND r.student.id = :studentId",
                        CourseReview.class)
                .setParameter("courseId", courseId)
                .setParameter("studentId", studentId)
                .getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Transactional
    public CourseReview save(CourseReview review) {
        if (review.getId() == null) {
            entityManager.persist(review);
            return review;
        } else {
            return entityManager.merge(review);
        }
    }

    @Transactional
    public void delete(UUID id) {
        CourseReview review = findEntityById(id);
        entityManager.remove(review);
    }
}
