package ru.mephi.orm_hibernate_demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.orm_hibernate_demo.dto.request.GradeRequest;
import ru.mephi.orm_hibernate_demo.dto.request.SubmissionRequest;
import ru.mephi.orm_hibernate_demo.dto.response.GradeResponse;
import ru.mephi.orm_hibernate_demo.dto.response.SubmissionResponse;
import ru.mephi.orm_hibernate_demo.entity.Assignment;
import ru.mephi.orm_hibernate_demo.entity.Grade;
import ru.mephi.orm_hibernate_demo.entity.Submission;
import ru.mephi.orm_hibernate_demo.entity.person.Student;
import ru.mephi.orm_hibernate_demo.enums.EnrollmentStatus;
import ru.mephi.orm_hibernate_demo.enums.SubmissionStatus;
import ru.mephi.orm_hibernate_demo.exception.AppException;
import ru.mephi.orm_hibernate_demo.mapper.GradeMapper;
import ru.mephi.orm_hibernate_demo.mapper.SubmissionMapper;
import ru.mephi.orm_hibernate_demo.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final PersonRepository personRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;
    private final SubmissionMapper submissionMapper;
    private final GradeMapper gradeMapper;

    @Transactional
    public SubmissionResponse submit(UUID assignmentId, SubmissionRequest request) {
        Assignment assignment = assignmentRepository.findEntityById(assignmentId);
        Student student = personRepository.findStudentEntityById(request.getStudentId());

        enrollmentRepository.findByStudentAndCourse(student.getId(), assignment.getCourse().getId())
                .filter(enrollment -> enrollment.getStatus() == EnrollmentStatus.ENROLLED)
                .orElseThrow(() -> new AppException("Student is not enrolled in the course", 400));

        submissionRepository.findByAssignmentAndStudent(assignmentId, student.getId())
                .ifPresent(existing -> {
                    throw new AppException("Submission already exists for this assignment", 409);
                });

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setContentUrl(request.getContentUrl());
        submission.setAttemptNo(1);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStatus(determineStatus(assignment));

        Submission saved = submissionRepository.save(submission);
        return submissionMapper.toResponse(saved);
    }

    private SubmissionStatus determineStatus(Assignment assignment) {
        if (assignment.getDueDate() != null && LocalDateTime.now().isAfter(assignment.getDueDate())) {
            return SubmissionStatus.LATE;
        }
        return SubmissionStatus.PENDING;
    }

    @Transactional(readOnly = true)
    public SubmissionResponse findById(UUID id) {
        Submission submission = submissionRepository.findEntityById(id);
        return submissionMapper.toResponse(submission);
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponse> findByAssignmentId(UUID assignmentId) {
        assignmentRepository.findEntityById(assignmentId);
        List<Submission> submissions = submissionRepository.findByAssignmentId(assignmentId);
        return submissionMapper.toResponseList(submissions);
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponse> findByStudentId(UUID studentId) {
        personRepository.findStudentEntityById(studentId);
        List<Submission> submissions = submissionRepository.findByStudentId(studentId);
        return submissionMapper.toResponseList(submissions);
    }

    @Transactional
    public GradeResponse gradeSubmission(UUID submissionId, GradeRequest request) {
        Submission submission = submissionRepository.findEntityById(submissionId);

        Grade grade = submission.getGrade();
        if (grade == null) {
            grade = new Grade();
            grade.setSubmission(submission);
        }
        grade.setPoints(request.getPoints());
        grade.setComment(request.getComment());
        grade.setGradedAt(LocalDateTime.now());

        Grade saved = gradeRepository.save(grade);
        submission.setGrade(saved);
        submission.setStatus(SubmissionStatus.GRADED);

        return gradeMapper.toResponse(saved);
    }
}
