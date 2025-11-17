package ru.mephi.orm_hibernate_demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.mephi.orm_hibernate_demo.dto.request.*;
import ru.mephi.orm_hibernate_demo.dto.response.*;
import ru.mephi.orm_hibernate_demo.enums.EnrollmentRole;
import ru.mephi.orm_hibernate_demo.enums.StudentStatus;
import ru.mephi.orm_hibernate_demo.service.PersonService;
import ru.mephi.orm_hibernate_demo.support.PostgresIntegrationTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LearningWorkflowIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonService personService;

    @Test
    void studentEnrollmentSubmissionAndGradingLifecycle() throws Exception {
        TeacherRequest teacherRequest = new TeacherRequest();
        teacherRequest.setFirstName("Bob");
        teacherRequest.setLastName("Mentor");
        teacherRequest.setAcademicTitle("Professor");
        teacherRequest.setBirthDate(LocalDate.of(1980, 3, 14));
        teacherRequest.setExternalRef("teacher-" + UUID.randomUUID());
        TeacherResponse teacher = personService.createTeacher(teacherRequest);

        StudentRequest studentRequest = new StudentRequest();
        studentRequest.setFirstName("Charlie");
        studentRequest.setLastName("Student");
        studentRequest.setBirthDate(LocalDate.of(2003, 10, 1));
        studentRequest.setExternalRef("student-" + UUID.randomUUID());
        studentRequest.setStudentNo("S" + System.currentTimeMillis());
        studentRequest.setStatus(StudentStatus.ACTIVE);
        StudentResponse student = personService.createStudent(studentRequest);

        CourseRequest courseRequest = new CourseRequest();
        courseRequest.setCode("DB-" + UUID.randomUUID().toString().substring(0, 4));
        courseRequest.setTitle("Database Systems");
        courseRequest.setDescription("Covers SQL and ORM.");
        courseRequest.setCredits(5);
        courseRequest.setAuthorId(teacher.getId());

        String courseJson = mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        CourseResponse course = objectMapper.readValue(courseJson, CourseResponse.class);

        EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
        enrollmentRequest.setCourseId(course.getId());
        enrollmentRequest.setStudentId(student.getId());
        enrollmentRequest.setRole(EnrollmentRole.STUDENT);

        String enrollmentJson = mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enrollmentRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        EnrollmentResponse enrollmentResponse = objectMapper.readValue(enrollmentJson, EnrollmentResponse.class);
        assertThat(enrollmentResponse.getCourse().getId()).isEqualTo(course.getId());

        AssignmentRequest assignmentRequest = new AssignmentRequest();
        assignmentRequest.setTitle("HW1");
        assignmentRequest.setDueDate(LocalDateTime.now().plusDays(3));
        assignmentRequest.setMaxPoints(100);

        String assignmentJson = mockMvc.perform(post("/api/courses/{courseId}/assignments", course.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignmentRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        AssignmentResponse assignmentResponse = objectMapper.readValue(assignmentJson, AssignmentResponse.class);

        SubmissionRequest submissionRequest = new SubmissionRequest();
        submissionRequest.setStudentId(student.getId());
        submissionRequest.setContentUrl("https://storage.local/homeworks/hw1");

        String submissionJson = mockMvc.perform(post("/api/assignments/{assignmentId}/submissions", assignmentResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submissionRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        SubmissionResponse submissionResponse = objectMapper.readValue(submissionJson, SubmissionResponse.class);

        GradeRequest gradeRequest = new GradeRequest();
        gradeRequest.setPoints(95.0);
        gradeRequest.setComment("Great job");

        String gradeJson = mockMvc.perform(post("/api/submissions/{submissionId}/grade", submissionResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gradeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points").value(95.0))
                .andReturn()
                .getResponse()
                .getContentAsString();
        GradeResponse gradeResponse = objectMapper.readValue(gradeJson, GradeResponse.class);
        assertThat(gradeResponse.getComment()).isEqualTo("Great job");

        mockMvc.perform(get("/api/courses/{courseId}/assignments", course.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("HW1"));

        mockMvc.perform(get("/api/assignments/{assignmentId}/submissions", assignmentResponse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].grade.points").value(95.0));

        SubmissionResponse refreshedSubmission = objectMapper.readValue(
                mockMvc.perform(get("/api/submissions/{id}", submissionResponse.getId()))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                SubmissionResponse.class
        );
        assertThat(refreshedSubmission.getGrade().getPoints()).isEqualTo(95.0);
    }
}
