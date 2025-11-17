package ru.mephi.orm_hibernate_demo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.orm_hibernate_demo.entity.Enrollment;
import ru.mephi.orm_hibernate_demo.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class EnrollmentRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    public Enrollment findEntityById(UUID id) {
        Enrollment enrollment = entityManager.find(Enrollment.class, id);
        if (enrollment == null) {
            throw new NotFoundException("Enrollment not found with id: " + id);
        }
        return enrollment;
    }

    public List<Enrollment> findAll() {
        return entityManager.createQuery("SELECT e FROM Enrollment e", Enrollment.class)
                .getResultList();
    }

    public List<Enrollment> findByStudentId(UUID studentId) {
        return entityManager.createQuery(
                "SELECT e FROM Enrollment e WHERE e.student.id = :studentId", Enrollment.class)
                .setParameter("studentId", studentId)
                .getResultList();
    }

    public List<Enrollment> findByCourseId(UUID courseId) {
        return entityManager.createQuery(
                "SELECT e FROM Enrollment e WHERE e.course.id = :courseId", Enrollment.class)
                .setParameter("courseId", courseId)
                .getResultList();
    }

    public Optional<Enrollment> findByStudentAndCourse(UUID studentId, UUID courseId) {
        List<Enrollment> results = entityManager.createQuery(
                "SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.course.id = :courseId",
                Enrollment.class)
                .setParameter("studentId", studentId)
                .setParameter("courseId", courseId)
                .getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Transactional
    public Enrollment save(Enrollment enrollment) {
        if (enrollment.getId() == null) {
            entityManager.persist(enrollment);
            return enrollment;
        } else {
            return entityManager.merge(enrollment);
        }
    }

    @Transactional
    public void delete(UUID id) {
        Enrollment enrollment = findEntityById(id);
        entityManager.remove(enrollment);
    }
}
