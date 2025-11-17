package ru.mephi.orm_hibernate_demo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.orm_hibernate_demo.entity.Assignment;
import ru.mephi.orm_hibernate_demo.exception.NotFoundException;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AssignmentRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    public Assignment findEntityById(UUID id) {
        Assignment assignment = entityManager.find(Assignment.class, id);
        if (assignment == null) {
            throw new NotFoundException("Assignment not found with id: " + id);
        }
        return assignment;
    }

    public List<Assignment> findByCourseId(UUID courseId) {
        return entityManager.createQuery(
                        "SELECT a FROM Assignment a WHERE a.course.id = :courseId", Assignment.class)
                .setParameter("courseId", courseId)
                .getResultList();
    }

    @Transactional
    public Assignment save(Assignment assignment) {
        if (assignment.getId() == null) {
            entityManager.persist(assignment);
            return assignment;
        } else {
            return entityManager.merge(assignment);
        }
    }

    @Transactional
    public void delete(UUID id) {
        Assignment assignment = findEntityById(id);
        entityManager.remove(assignment);
    }
}
