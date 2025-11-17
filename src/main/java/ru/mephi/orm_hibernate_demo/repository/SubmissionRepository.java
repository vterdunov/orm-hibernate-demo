package ru.mephi.orm_hibernate_demo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.orm_hibernate_demo.entity.Submission;
import ru.mephi.orm_hibernate_demo.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SubmissionRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    public Submission findEntityById(UUID id) {
        Submission submission = entityManager.find(Submission.class, id);
        if (submission == null) {
            throw new NotFoundException("Submission not found with id: " + id);
        }
        return submission;
    }

    public List<Submission> findByAssignmentId(UUID assignmentId) {
        return entityManager.createQuery(
                        "SELECT s FROM Submission s WHERE s.assignment.id = :assignmentId", Submission.class)
                .setParameter("assignmentId", assignmentId)
                .getResultList();
    }

    public List<Submission> findByStudentId(UUID studentId) {
        return entityManager.createQuery(
                        "SELECT s FROM Submission s WHERE s.student.id = :studentId", Submission.class)
                .setParameter("studentId", studentId)
                .getResultList();
    }

    public Optional<Submission> findByAssignmentAndStudent(UUID assignmentId, UUID studentId) {
        List<Submission> results = entityManager.createQuery(
                        "SELECT s FROM Submission s WHERE s.assignment.id = :assignmentId AND s.student.id = :studentId",
                        Submission.class)
                .setParameter("assignmentId", assignmentId)
                .setParameter("studentId", studentId)
                .getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Transactional
    public Submission save(Submission submission) {
        if (submission.getId() == null) {
            entityManager.persist(submission);
            return submission;
        } else {
            return entityManager.merge(submission);
        }
    }
}
