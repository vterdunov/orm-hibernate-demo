package ru.mephi.orm_hibernate_demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.orm_hibernate_demo.dto.request.CourseReviewRequest;
import ru.mephi.orm_hibernate_demo.dto.response.CourseReviewResponse;
import ru.mephi.orm_hibernate_demo.entity.Course;
import ru.mephi.orm_hibernate_demo.entity.CourseReview;
import ru.mephi.orm_hibernate_demo.entity.person.Student;
import ru.mephi.orm_hibernate_demo.enums.EnrollmentStatus;
import ru.mephi.orm_hibernate_demo.exception.AppException;
import ru.mephi.orm_hibernate_demo.mapper.CourseReviewMapper;
import ru.mephi.orm_hibernate_demo.repository.CourseRepository;
import ru.mephi.orm_hibernate_demo.repository.CourseReviewRepository;
import ru.mephi.orm_hibernate_demo.repository.EnrollmentRepository;
import ru.mephi.orm_hibernate_demo.repository.PersonRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseReviewService {

    private final CourseReviewRepository courseReviewRepository;
    private final CourseRepository courseRepository;
    private final PersonRepository personRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseReviewMapper courseReviewMapper;

    @Transactional
    public CourseReviewResponse create(UUID courseId, CourseReviewRequest request) {
        Course course = courseRepository.findEntityById(courseId);
        Student student = personRepository.findStudentEntityById(request.getStudentId());

        enrollmentRepository.findByStudentAndCourse(student.getId(), courseId)
                .filter(enrollment -> enrollment.getStatus() == EnrollmentStatus.COMPLETED
                        || enrollment.getStatus() == EnrollmentStatus.ENROLLED)
                .orElseThrow(() -> new AppException("Student must be enrolled to leave a review", 400));

        courseReviewRepository.findByCourseAndStudent(courseId, student.getId())
                .ifPresent(existing -> {
                    throw new AppException("Review already exists for this course and student", 409);
                });

        CourseReview review = new CourseReview();
        review.setCourse(course);
        review.setStudent(student);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        CourseReview saved = courseReviewRepository.save(review);
        return courseReviewMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CourseReviewResponse> findByCourse(UUID courseId) {
        courseRepository.findEntityById(courseId);
        List<CourseReview> reviews = courseReviewRepository.findByCourseId(courseId);
        return courseReviewMapper.toResponseList(reviews);
    }

    @Transactional
    public void delete(UUID reviewId) {
        courseReviewRepository.delete(reviewId);
    }
}
