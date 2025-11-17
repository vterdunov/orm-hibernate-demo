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
import ru.mephi.orm_hibernate_demo.enums.QuestionType;
import ru.mephi.orm_hibernate_demo.enums.StudentStatus;
import ru.mephi.orm_hibernate_demo.service.PersonService;
import ru.mephi.orm_hibernate_demo.support.PostgresIntegrationTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class QuizAndReviewIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonService personService;

    @Test
    void studentCanTakeQuizAndLeaveReview() throws Exception {
        TeacherResponse teacher = createTeacher();
        StudentResponse student = createStudent();
        CourseResponse course = createCourse(teacher.getId());

        EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
        enrollmentRequest.setCourseId(course.getId());
        enrollmentRequest.setStudentId(student.getId());
        enrollmentRequest.setRole(EnrollmentRole.STUDENT);

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enrollmentRequest)))
                .andExpect(status().isCreated());

        QuizRequest quizRequest = new QuizRequest();
        quizRequest.setTitle("Midterm");
        quizRequest.setDescription("Covers ORM basics");
        quizRequest.setQuestions(List.of(
                buildQuestion("What does ORM stand for?",
                        List.of(new OptionPair("Object Relational Mapping", true),
                                new OptionPair("Optimized Resource Machine", false))),
                buildQuestion("Choose persistence frameworks",
                        List.of(new OptionPair("Hibernate", true),
                                new OptionPair("Spring Data JPA", true),
                                new OptionPair("JUnit", false)))
        ));

        String quizJson = mockMvc.perform(post("/api/courses/{courseId}/quizzes", course.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        QuizResponse quizResponse = objectMapper.readValue(quizJson, QuizResponse.class);

        QuestionResponse singleQuestion = quizResponse.getQuestions().get(0);
        UUID correctOption = singleQuestion.getOptions().stream()
                .filter(AnswerOptionResponse::isCorrect)
                .findFirst()
                .orElseThrow()
                .getId();

        QuestionResponse multiQuestion = quizResponse.getQuestions().get(1);
        List<UUID> multiCorrect = multiQuestion.getOptions().stream()
                .filter(AnswerOptionResponse::isCorrect)
                .map(AnswerOptionResponse::getId)
                .toList();

        QuizSubmissionRequest submissionRequest = new QuizSubmissionRequest();
        submissionRequest.setStudentId(student.getId());
        submissionRequest.setAnswers(Map.of(
                singleQuestion.getId(), List.of(correctOption),
                multiQuestion.getId(), multiCorrect
        ));

        String submissionJson = mockMvc.perform(post("/api/quizzes/{quizId}/submissions", quizResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submissionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.score").value(100.0))
                .andReturn()
                .getResponse()
                .getContentAsString();
        QuizSubmissionResponse quizSubmission = objectMapper.readValue(submissionJson, QuizSubmissionResponse.class);
        assertThat(quizSubmission.getAttemptNo()).isEqualTo(1);

        CourseReviewRequest reviewRequest = new CourseReviewRequest();
        reviewRequest.setStudentId(student.getId());
        reviewRequest.setRating(5);
        reviewRequest.setComment("Loved the quiz!");

        mockMvc.perform(post("/api/courses/{courseId}/reviews", course.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(5));

        mockMvc.perform(get("/api/courses/{courseId}/reviews", course.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comment").value("Loved the quiz!"));
    }

    private QuestionRequest buildQuestion(String text, List<OptionPair> options) {
        QuestionRequest request = new QuestionRequest();
        request.setText(text);
        request.setType(options.size() > 2 ? QuestionType.MULTIPLE_CHOICE : QuestionType.SINGLE_CHOICE);
        request.setOptions(options.stream().map(pair -> {
            AnswerOptionRequest optionRequest = new AnswerOptionRequest();
            optionRequest.setText(pair.text());
            optionRequest.setCorrect(pair.correct());
            return optionRequest;
        }).toList());
        return request;
    }

    private record OptionPair(String text, boolean correct) {
    }

    private StudentResponse createStudent() {
        StudentRequest studentRequest = new StudentRequest();
        studentRequest.setFirstName("Dana");
        studentRequest.setLastName("Learner");
        studentRequest.setBirthDate(LocalDate.of(2002, 1, 1));
        studentRequest.setExternalRef("student-" + UUID.randomUUID());
        String studentNo = "STU" + String.format("%07d", Math.abs(System.nanoTime()) % 1_000_0000L);
        studentRequest.setStudentNo(studentNo);
        studentRequest.setStatus(StudentStatus.ACTIVE);
        return personService.createStudent(studentRequest);
    }

    private TeacherResponse createTeacher() {
        TeacherRequest teacherRequest = new TeacherRequest();
        teacherRequest.setFirstName("Eve");
        teacherRequest.setLastName("Coach");
        teacherRequest.setBirthDate(LocalDate.of(1982, 2, 2));
        teacherRequest.setExternalRef("teacher-" + UUID.randomUUID());
        teacherRequest.setAcademicTitle("Associate Professor");
        return personService.createTeacher(teacherRequest);
    }

    private CourseResponse createCourse(UUID teacherId) throws Exception {
        CourseRequest courseRequest = new CourseRequest();
        courseRequest.setCode("QA-" + UUID.randomUUID().toString().substring(0, 4));
        courseRequest.setTitle("Quality Assurance");
        courseRequest.setDescription("Testing quizzes and reviews");
        courseRequest.setCredits(3);
        courseRequest.setAuthorId(teacherId);
        String courseJson = mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(courseJson, CourseResponse.class);
    }
}
