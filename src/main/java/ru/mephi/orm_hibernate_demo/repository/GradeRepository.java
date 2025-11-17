package ru.mephi.orm_hibernate_demo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.orm_hibernate_demo.entity.Grade;

@Repository
@RequiredArgsConstructor
public class GradeRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public Grade save(Grade grade) {
        if (grade.getId() == null) {
            entityManager.persist(grade);
            return grade;
        } else {
            return entityManager.merge(grade);
        }
    }
}
