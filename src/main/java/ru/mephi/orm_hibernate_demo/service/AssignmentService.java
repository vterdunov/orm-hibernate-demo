package ru.mephi.orm_hibernate_demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.orm_hibernate_demo.dto.request.AssignmentRequest;
import ru.mephi.orm_hibernate_demo.dto.response.AssignmentResponse;
import ru.mephi.orm_hibernate_demo.entity.Assignment;
import ru.mephi.orm_hibernate_demo.entity.Course;
import ru.mephi.orm_hibernate_demo.mapper.AssignmentMapper;
import ru.mephi.orm_hibernate_demo.repository.AssignmentRepository;
import ru.mephi.orm_hibernate_demo.repository.CourseRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final AssignmentMapper assignmentMapper;

    @Transactional
    public AssignmentResponse create(UUID courseId, AssignmentRequest request) {
        Course course = courseRepository.findEntityById(courseId);

        Assignment assignment = assignmentMapper.toEntity(request);
        assignment.setCourse(course);

        Assignment saved = assignmentRepository.save(assignment);
        return assignmentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public AssignmentResponse findById(UUID id) {
        Assignment assignment = assignmentRepository.findEntityById(id);
        return assignmentMapper.toResponse(assignment);
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponse> findByCourseId(UUID courseId) {
        courseRepository.findEntityById(courseId);
        List<Assignment> assignments = assignmentRepository.findByCourseId(courseId);
        return assignmentMapper.toResponseList(assignments);
    }

    @Transactional
    public AssignmentResponse update(UUID assignmentId, AssignmentRequest request) {
        Assignment assignment = assignmentRepository.findEntityById(assignmentId);
        assignmentMapper.updateEntityFromRequest(request, assignment);
        Assignment updated = assignmentRepository.save(assignment);
        return assignmentMapper.toResponse(updated);
    }

    @Transactional
    public void delete(UUID assignmentId) {
        assignmentRepository.delete(assignmentId);
    }
}
