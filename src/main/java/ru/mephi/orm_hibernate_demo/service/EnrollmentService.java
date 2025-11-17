package ru.mephi.orm_hibernate_demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.orm_hibernate_demo.dto.request.EnrollmentRequest;
import ru.mephi.orm_hibernate_demo.dto.response.CourseShortResponse;
import ru.mephi.orm_hibernate_demo.dto.response.EnrollmentResponse;
import ru.mephi.orm_hibernate_demo.dto.response.StudentResponse;
import ru.mephi.orm_hibernate_demo.entity.Course;
import ru.mephi.orm_hibernate_demo.entity.Enrollment;
import ru.mephi.orm_hibernate_demo.entity.person.Student;
import ru.mephi.orm_hibernate_demo.enums.EnrollmentRole;
import ru.mephi.orm_hibernate_demo.enums.EnrollmentStatus;
import ru.mephi.orm_hibernate_demo.enums.StudentStatus;
import ru.mephi.orm_hibernate_demo.exception.AppException;
import ru.mephi.orm_hibernate_demo.mapper.EnrollmentMapper;
import ru.mephi.orm_hibernate_demo.repository.CourseRepository;
import ru.mephi.orm_hibernate_demo.repository.EnrollmentRepository;
import ru.mephi.orm_hibernate_demo.repository.PersonRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final PersonRepository personRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final PersonService personService;
    private final CourseService courseService;

    @Transactional
    public EnrollmentResponse enrollStudent(UUID studentId, UUID courseId, EnrollmentRole role) {
        StudentResponse studentResponse = personService.findStudentById(studentId);

        if (studentResponse.getStatus() != StudentStatus.ACTIVE) {
            throw new AppException("Student is not active", 400);
        }

        courseService.findById(courseId);

        enrollmentRepository.findByStudentAndCourse(studentId, courseId).ifPresent(e -> {
            throw new AppException("Student is already enrolled in this course", 409);
        });

        Student student = personRepository.findStudentEntityById(studentId);
        Course course = courseRepository.findEntityById(courseId);

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setRole(role);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setStatus(EnrollmentStatus.ENROLLED);

        Enrollment saved = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toResponse(saved);
    }

    @Transactional
    public EnrollmentResponse create(EnrollmentRequest request) {
        personService.findStudentById(request.getStudentId());
        courseService.findById(request.getCourseId());

        enrollmentRepository.findByStudentAndCourse(request.getStudentId(), request.getCourseId())
                .ifPresent(e -> {
                    throw new AppException("Enrollment already exists", 409);
                });

        Student student = personRepository.findStudentEntityById(request.getStudentId());
        Course course = courseRepository.findEntityById(request.getCourseId());

        Enrollment enrollment = enrollmentMapper.toEntity(request);
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setStatus(EnrollmentStatus.ENROLLED);

        Enrollment saved = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public EnrollmentResponse findById(UUID id) {
        Enrollment enrollment = enrollmentRepository.findEntityById(id);
        return enrollmentMapper.toResponse(enrollment);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> findAll() {
        List<Enrollment> enrollments = enrollmentRepository.findAll();
        return enrollmentMapper.toResponseList(enrollments);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> findByStudentId(UUID studentId) {
        personService.findStudentById(studentId);

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        return enrollmentMapper.toResponseList(enrollments);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> findByCourseId(UUID courseId) {
        courseService.findById(courseId);

        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        return enrollmentMapper.toResponseList(enrollments);
    }

    @Transactional(readOnly = true)
    public List<CourseShortResponse> getStudentCourses(UUID studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

        List<UUID> courseIds = enrollments.stream()
                .map(e -> e.getCourse().getId())
                .collect(Collectors.toList());

        return courseIds.stream()
                .map(courseService::findById)
                .map(courseResponse -> {
                    CourseShortResponse shortResponse = new CourseShortResponse();
                    shortResponse.setId(courseResponse.getId());
                    shortResponse.setCode(courseResponse.getCode());
                    shortResponse.setTitle(courseResponse.getTitle());
                    shortResponse.setCredits(courseResponse.getCredits());
                    return shortResponse;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getCourseStudents(UUID courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);

        List<UUID> studentIds = enrollments.stream()
                .map(e -> e.getStudent().getId())
                .collect(Collectors.toList());

        return studentIds.stream()
                .map(personService::findStudentById)
                .collect(Collectors.toList());
    }

    @Transactional
    public EnrollmentResponse update(UUID id, EnrollmentRequest request) {
        Enrollment enrollment = enrollmentRepository.findEntityById(id);
        enrollmentMapper.updateEntityFromRequest(request, enrollment);

        Enrollment updated = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toResponse(updated);
    }

    @Transactional
    public void completeEnrollment(UUID enrollmentId, Double finalGrade) {
        Enrollment enrollment = enrollmentRepository.findEntityById(enrollmentId);
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        enrollment.setFinalGrade(finalGrade);
    }

    @Transactional
    public void dropEnrollment(UUID enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findEntityById(enrollmentId);
        enrollment.setStatus(EnrollmentStatus.DROPPED);
    }

    @Transactional
    public void delete(UUID id) {
        enrollmentRepository.delete(id);
    }
}
