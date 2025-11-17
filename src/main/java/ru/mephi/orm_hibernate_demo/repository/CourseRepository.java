package ru.mephi.orm_hibernate_demo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.orm_hibernate_demo.entity.Course;
import ru.mephi.orm_hibernate_demo.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CourseRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    public Course findEntityById(UUID id) {
        Course course = entityManager.find(Course.class, id);
        if (course == null) {
            throw new NotFoundException("Course not found with id: " + id);
        }
        return course;
    }

    public List<Course> findAll() {
        return entityManager.createQuery("SELECT c FROM Course c", Course.class)
                .getResultList();
    }

    public Optional<Course> findByCode(String code) {
        List<Course> results = entityManager.createQuery(
                "SELECT c FROM Course c WHERE c.code = :code", Course.class)
                .setParameter("code", code)
                .getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Transactional
    public Course save(Course course) {
        if (course.getId() == null) {
            entityManager.persist(course);
            return course;
        } else {
            return entityManager.merge(course);
        }
    }

    @Transactional
    public void delete(UUID id) {
        Course course = findEntityById(id);
        entityManager.remove(course);
    }
}
