package ru.mephi.orm_hibernate_demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.orm_hibernate_demo.dto.request.StudentRequest;
import ru.mephi.orm_hibernate_demo.dto.request.TeacherRequest;
import ru.mephi.orm_hibernate_demo.dto.response.PersonResponse;
import ru.mephi.orm_hibernate_demo.dto.response.StudentResponse;
import ru.mephi.orm_hibernate_demo.dto.response.TeacherResponse;
import ru.mephi.orm_hibernate_demo.entity.person.Person;
import ru.mephi.orm_hibernate_demo.entity.person.Student;
import ru.mephi.orm_hibernate_demo.entity.person.Teacher;
import ru.mephi.orm_hibernate_demo.enums.StudentStatus;
import ru.mephi.orm_hibernate_demo.exception.AppException;
import ru.mephi.orm_hibernate_demo.mapper.PersonMapper;
import ru.mephi.orm_hibernate_demo.mapper.StudentMapper;
import ru.mephi.orm_hibernate_demo.mapper.TeacherMapper;
import ru.mephi.orm_hibernate_demo.repository.PersonRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;
    private final PersonMapper personMapper;

    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        personRepository.findByStudentNo(request.getStudentNo()).ifPresent(s -> {
            throw new AppException("Student with studentNo '" + request.getStudentNo() + "' already exists", 409);
        });

        Student student = studentMapper.toEntity(request);
        Student saved = personRepository.save(student);
        return studentMapper.toResponse(saved);
    }

    @Transactional
    public TeacherResponse createTeacher(TeacherRequest request) {
        Teacher teacher = teacherMapper.toEntity(request);
        Teacher saved = personRepository.save(teacher);
        return teacherMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public StudentResponse findStudentById(UUID id) {
        Student student = personRepository.findStudentEntityById(id);
        return studentMapper.toResponse(student);
    }

    @Transactional(readOnly = true)
    public TeacherResponse findTeacherById(UUID id) {
        Teacher teacher = personRepository.findTeacherEntityById(id);
        return teacherMapper.toResponse(teacher);
    }

    @Transactional(readOnly = true)
    public PersonResponse findPersonById(UUID id) {
        Person person = personRepository.findEntityById(id);
        return personMapper.toResponse(person);
    }

    @Transactional(readOnly = true)
    public List<PersonResponse> findAllPersons() {
        List<Person> persons = personRepository.findAll();
        return personMapper.toResponseList(persons);
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> findAllStudents() {
        List<Student> students = personRepository.findAllStudents();
        return studentMapper.toResponseList(students);
    }

    @Transactional(readOnly = true)
    public List<TeacherResponse> findAllTeachers() {
        List<Teacher> teachers = personRepository.findAllTeachers();
        return teacherMapper.toResponseList(teachers);
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> findStudentsByStatus(StudentStatus status) {
        List<Student> students = personRepository.findByStatus(status);
        return studentMapper.toResponseList(students);
    }

    @Transactional
    public StudentResponse updateStudent(UUID id, StudentRequest request) {
        Student student = personRepository.findStudentEntityById(id);
        studentMapper.updateEntityFromRequest(request, student);

        Student updated = personRepository.save(student);
        return studentMapper.toResponse(updated);
    }

    @Transactional
    public TeacherResponse updateTeacher(UUID id, TeacherRequest request) {
        Teacher teacher = personRepository.findTeacherEntityById(id);
        teacherMapper.updateEntityFromRequest(request, teacher);

        Teacher updated = personRepository.save(teacher);
        return teacherMapper.toResponse(updated);
    }

    @Transactional
    public void addEmail(UUID studentId, String email) {
        Student student = personRepository.findStudentEntityById(studentId);
        student.getEmails().add(email);
    }

    @Transactional
    public void removeEmail(UUID studentId, String email) {
        Student student = personRepository.findStudentEntityById(studentId);
        student.getEmails().remove(email);
    }

    @Transactional
    public void setEmails(UUID studentId, Set<String> emails) {
        Student student = personRepository.findStudentEntityById(studentId);
        student.getEmails().clear();
        student.getEmails().addAll(emails);
    }

    @Transactional
    public void addPhone(UUID studentId, String phone) {
        Student student = personRepository.findStudentEntityById(studentId);
        student.getPhones().add(phone);
    }

    @Transactional
    public void removePhone(UUID studentId, String phone) {
        Student student = personRepository.findStudentEntityById(studentId);
        student.getPhones().remove(phone);
    }

    @Transactional
    public void setPhones(UUID studentId, Set<String> phones) {
        Student student = personRepository.findStudentEntityById(studentId);
        student.getPhones().clear();
        student.getPhones().addAll(phones);
    }

    @Transactional
    public void changeStudentStatus(UUID studentId, StudentStatus status) {
        Student student = personRepository.findStudentEntityById(studentId);
        student.setStatus(status);
    }

    @Transactional
    public void deleteStudent(UUID id) {
        personRepository.deleteStudent(id);
    }

    @Transactional
    public void deleteTeacher(UUID id) {
        personRepository.deleteTeacher(id);
    }

    @Transactional
    public void deletePerson(UUID id) {
        personRepository.delete(id);
    }
}
