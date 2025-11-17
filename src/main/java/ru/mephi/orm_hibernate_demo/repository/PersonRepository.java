package ru.mephi.orm_hibernate_demo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.orm_hibernate_demo.entity.person.Person;
import ru.mephi.orm_hibernate_demo.entity.person.Student;
import ru.mephi.orm_hibernate_demo.entity.person.Teacher;
import ru.mephi.orm_hibernate_demo.enums.StudentStatus;
import ru.mephi.orm_hibernate_demo.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PersonRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    public Person findEntityById(UUID id) {
        Person person = entityManager.find(Person.class, id);
        if (person == null) {
            throw new NotFoundException("Person not found with id: " + id);
        }
        return person;
    }

    public Optional<Person> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(Person.class, id));
    }

    public List<Person> findAll() {
        return entityManager.createQuery("SELECT p FROM Person p", Person.class)
                .getResultList();
    }

    public List<Student> findAllStudents() {
        return entityManager.createQuery("SELECT s FROM Student s", Student.class)
                .getResultList();
    }

    public List<Teacher> findAllTeachers() {
        return entityManager.createQuery("SELECT t FROM Teacher t", Teacher.class)
                .getResultList();
    }

    public <T extends Person> List<T> findByType(Class<T> type) {
        return entityManager.createQuery("SELECT p FROM " + type.getSimpleName() + " p", type)
                .getResultList();
    }

    public Student findStudentEntityById(UUID id) {
        Student student = entityManager.find(Student.class, id);
        if (student == null) {
            throw new NotFoundException("Student not found with id: " + id);
        }
        return student;
    }

    public Teacher findTeacherEntityById(UUID id) {
        Teacher teacher = entityManager.find(Teacher.class, id);
        if (teacher == null) {
            throw new NotFoundException("Teacher not found with id: " + id);
        }
        return teacher;
    }

    public Optional<Student> findByStudentNo(String studentNo) {
        List<Student> results = entityManager.createQuery(
                "SELECT s FROM Student s WHERE s.studentNo = :studentNo", Student.class)
                .setParameter("studentNo", studentNo)
                .getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<Student> findByStatus(StudentStatus status) {
        return entityManager.createQuery(
                "SELECT s FROM Student s WHERE s.status = :status", Student.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Teacher> findByAcademicTitle(String academicTitle) {
        return entityManager.createQuery(
                "SELECT t FROM Teacher t WHERE t.academicTitle = :title", Teacher.class)
                .setParameter("title", academicTitle)
                .getResultList();
    }

    public List<Person> findByLastName(String lastName) {
        return entityManager.createQuery(
                "SELECT p FROM Person p WHERE p.lastName = :lastName", Person.class)
                .setParameter("lastName", lastName)
                .getResultList();
    }

    public long countByType(Class<? extends Person> type) {
        return entityManager.createQuery(
                "SELECT COUNT(p) FROM " + type.getSimpleName() + " p", Long.class)
                .getSingleResult();
    }

    @Transactional
    public <T extends Person> T save(T person) {
        if (person.getId() == null) {
            entityManager.persist(person);
            return person;
        } else {
            return entityManager.merge(person);
        }
    }

    @Transactional
    public void delete(UUID id) {
        Person person = findEntityById(id);
        entityManager.remove(person);
    }

    @Transactional
    public void deleteStudent(UUID id) {
        Student student = findStudentEntityById(id);
        entityManager.remove(student);
    }

    @Transactional
    public void deleteTeacher(UUID id) {
        Teacher teacher = findTeacherEntityById(id);
        entityManager.remove(teacher);
    }
}
