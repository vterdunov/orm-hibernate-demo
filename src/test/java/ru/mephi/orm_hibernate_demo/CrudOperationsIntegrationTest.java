package ru.mephi.orm_hibernate_demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mephi.orm_hibernate_demo.dto.request.*;
import ru.mephi.orm_hibernate_demo.dto.response.*;
import ru.mephi.orm_hibernate_demo.enums.EnrollmentRole;
import ru.mephi.orm_hibernate_demo.enums.EnrollmentStatus;
import ru.mephi.orm_hibernate_demo.enums.StudentStatus;
import ru.mephi.orm_hibernate_demo.enums.SubmissionStatus;
import ru.mephi.orm_hibernate_demo.exception.AppException;
import ru.mephi.orm_hibernate_demo.repository.LessonRepository;
import ru.mephi.orm_hibernate_demo.service.*;
import ru.mephi.orm_hibernate_demo.support.PostgresIntegrationTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class CrudOperationsIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private PersonService personService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private AssignmentService assignmentService;
    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private TagService tagService;
    @Autowired
    private QuizService quizService;
    @Autowired
    private QuizSubmissionService quizSubmissionService;
    @Autowired
    private CourseReviewService courseReviewService;
    @Autowired
    private LessonRepository lessonRepository;

    @Test
    void courseUpdateChangesTitleAndKeywords() {
        TeacherResponse teacher = createTeacher();
        CourseResponse course = courseService.create(buildCourseRequest(teacher.getId()));

        CourseRequest update = buildCourseRequest(teacher.getId());
        update.setTitle("Advanced ORM");
        update.getKeywords().add("advanced");

        CourseResponse updated = courseService.update(course.getId(), update);
        assertThat(updated.getTitle()).isEqualTo("Advanced ORM");
        assertThat(updated.getKeywords()).contains("advanced");
    }

    @Test
    void deletingCourseRemovesLessons() {
        TeacherResponse teacher = createTeacher();
        CourseResponse course = courseService.create(buildCourseRequest(teacher.getId()));

        LessonRequest lesson = new LessonRequest();
        lesson.setCourseId(course.getId());
        lesson.setTitle("Lifecycle");
        lesson.setSyllabus("Explain persistence lifecycle");
        lesson.setResources(List.of("https://example.org/lifecycle"));
        lessonService.create(lesson);

        courseService.delete(course.getId());
        assertThatThrownBy(() -> courseService.findById(course.getId()))
                .isInstanceOf(AppException.class);
        assertThat(lessonRepository.findByCourseId(course.getId())).isEmpty();
    }

    @Test
    void lessonUpdateAndDelete() {
        TeacherResponse teacher = createTeacher();
        CourseResponse course = courseService.create(buildCourseRequest(teacher.getId()));

        LessonRequest lesson = new LessonRequest();
        lesson.setCourseId(course.getId());
        lesson.setTitle("Fetching");
        lesson.setSyllabus("Lazy vs eager");
        LessonResponse created = lessonService.create(lesson);

        lesson.setSyllabus("Updated syllabus");
        LessonResponse updated = lessonService.update(created.getId(), lesson);
        assertThat(updated.getSyllabus()).isEqualTo("Updated syllabus");

        lessonService.delete(created.getId());
        assertThatThrownBy(() -> lessonService.findById(created.getId()))
                .isInstanceOf(AppException.class);
    }

    @Test
    void assignmentUpdateAndDelete() {
        CourseResponse course = courseService.create(buildCourseRequest(createTeacher().getId()));

        AssignmentRequest request = new AssignmentRequest();
        request.setTitle("HW");
        request.setDueDate(LocalDateTime.now().plusDays(2));
        request.setMaxPoints(50);
        AssignmentResponse created = assignmentService.create(course.getId(), request);

        request.setMaxPoints(80);
        AssignmentResponse updated = assignmentService.update(created.getId(), request);
        assertThat(updated.getMaxPoints()).isEqualTo(80);

        assignmentService.delete(created.getId());
        assertThat(assignmentService.findByCourseId(course.getId())).isEmpty();
    }

    @Test
    void enrollmentCompletionAndDropChangeStatus() {
        StudentResponse student = createStudent();
        CourseResponse course = courseService.create(buildCourseRequest(createTeacher().getId()));

        EnrollmentRequest request = new EnrollmentRequest();
        request.setCourseId(course.getId());
        request.setStudentId(student.getId());
        request.setRole(EnrollmentRole.STUDENT);
        EnrollmentResponse enrollment = enrollmentService.create(request);

        enrollmentService.completeEnrollment(enrollment.getId(), 90.0);
        EnrollmentResponse completed = enrollmentService.findById(enrollment.getId());
        assertThat(completed.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
        assertThat(completed.getFinalGrade()).isEqualTo(90.0);

        enrollmentService.dropEnrollment(enrollment.getId());
        EnrollmentResponse dropped = enrollmentService.findById(enrollment.getId());
        assertThat(dropped.getStatus()).isEqualTo(EnrollmentStatus.DROPPED);
    }

    @Test
    void submissionCanBeGraded() {
        StudentResponse student = createStudent();
        TeacherResponse teacher = createTeacher();
        CourseResponse course = courseService.create(buildCourseRequest(teacher.getId()));
        enrollStudent(student, course);

        AssignmentRequest assignmentRequest = new AssignmentRequest();
        assignmentRequest.setTitle("Essay");
        assignmentRequest.setDueDate(LocalDateTime.now().plusDays(1));
        assignmentRequest.setMaxPoints(100);
        AssignmentResponse assignment = assignmentService.create(course.getId(), assignmentRequest);

        SubmissionRequest submissionRequest = new SubmissionRequest();
        submissionRequest.setStudentId(student.getId());
        submissionRequest.setContentUrl("https://storage.local/essay");
        SubmissionResponse submission = submissionService.submit(assignment.getId(), submissionRequest);

        GradeRequest gradeRequest = new GradeRequest();
        gradeRequest.setPoints(88.0);
        gradeRequest.setComment("Solid work");
        GradeResponse grade = submissionService.gradeSubmission(submission.getId(), gradeRequest);

        assertThat(grade.getPoints()).isEqualTo(88.0);
        SubmissionResponse refreshed = submissionService.findById(submission.getId());
        assertThat(refreshed.getStatus()).isEqualTo(SubmissionStatus.GRADED);
    }

    @Test
    void tagDeletionRequiresDetachingFromCourses() {
        TeacherResponse teacher = createTeacher();
        CourseResponse course = courseService.create(buildCourseRequest(teacher.getId()));

        TagRequest tagRequest = new TagRequest();
        tagRequest.setName("Data");
        tagRequest.setSlug("data-" + UUID.randomUUID());
        TagResponse tag = tagService.create(tagRequest);

        courseService.addTagToCourse(course.getId(), tag.getId());
        assertThat(courseService.getCourseTagsShort(course.getId())).hasSize(1);

        assertThatThrownBy(() -> tagService.delete(tag.getId()))
                .isInstanceOf(AppException.class);

        courseService.removeTagFromCourse(course.getId(), tag.getId());
        tagService.delete(tag.getId());
        assertThat(tagService.findAll()).noneMatch(t -> t.getId().equals(tag.getId()));
        assertThat(courseService.getCourseTagsShort(course.getId())).isEmpty();
    }

    @Test
    void quizCreationAndRetrievalWorks() {
        CourseResponse course = courseService.create(buildCourseRequest(createTeacher().getId()));
        QuizResponse quiz = quizService.create(course.getId(), buildQuizRequest());

        assertThat(quiz.getQuestions()).hasSize(2);
        assertThat(quizService.findByCourseId(course.getId())).extracting(QuizResponse::getId).contains(quiz.getId());
    }

    @Test
    void quizSubmissionSupportsMultipleAttempts() {
        StudentResponse student = createStudent();
        CourseResponse course = courseService.create(buildCourseRequest(createTeacher().getId()));
        enrollStudent(student, course);
        QuizResponse quiz = quizService.create(course.getId(), buildQuizRequest());

        QuizSubmissionRequest request = new QuizSubmissionRequest();
        request.setStudentId(student.getId());
        request.setAnswers(Map.of(
                quiz.getQuestions().get(0).getId(),
                List.of(quiz.getQuestions().get(0).getOptions().get(0).getId())
        ));
        QuizSubmissionResponse first = quizSubmissionService.submit(quiz.getId(), request);
        QuizSubmissionResponse second = quizSubmissionService.submit(quiz.getId(), request);
        assertThat(second.getAttemptNo()).isEqualTo(first.getAttemptNo() + 1);
    }

    @Test
    void quizSubmissionStoresSelectedOptions() {
        StudentResponse student = createStudent();
        CourseResponse course = courseService.create(buildCourseRequest(createTeacher().getId()));
        enrollStudent(student, course);
        QuizResponse quiz = quizService.create(course.getId(), buildQuizRequest());

        Map<UUID, List<UUID>> answers = quiz.getQuestions().stream()
                .collect(Collectors.toMap(
                        QuestionResponse::getId,
                        q -> List.of(q.getOptions().get(0).getId())
                ));
        QuizSubmissionRequest request = new QuizSubmissionRequest();
        request.setStudentId(student.getId());
        request.setAnswers(answers);

        QuizSubmissionResponse submission = quizSubmissionService.submit(quiz.getId(), request);
        assertThat(submission.getSelectedOptionIds()).hasSize(quiz.getQuestions().size());
    }

    @Test
    void courseReviewCreateAndDelete() {
        StudentResponse student = createStudent();
        CourseResponse course = courseService.create(buildCourseRequest(createTeacher().getId()));
        enrollStudent(student, course);

        CourseReviewRequest reviewRequest = new CourseReviewRequest();
        reviewRequest.setStudentId(student.getId());
        reviewRequest.setRating(4);
        reviewRequest.setComment("Clear explanations");
        CourseReviewResponse review = courseReviewService.create(course.getId(), reviewRequest);
        assertThat(courseReviewService.findByCourse(course.getId())).hasSize(1);

        courseReviewService.delete(review.getId());
        assertThat(courseReviewService.findByCourse(course.getId())).isEmpty();
    }

    @Test
    void studentEmailsCrudOperations() {
        StudentResponse student = createStudent();
        UUID studentId = student.getId();

        personService.addEmail(studentId, "student@example.org");
        personService.addPhone(studentId, "+1-555-1234");

        personService.removeEmail(studentId, "student@example.org");
        personService.setEmails(studentId, Set.of("primary@example.org"));
        personService.setPhones(studentId, Set.of("+1-555-0000"));

        StudentResponse refreshed = personService.findStudentById(studentId);
        assertThat(refreshed.getEmails()).containsExactly("primary@example.org");
        assertThat(refreshed.getPhones()).containsExactly("+1-555-0000");
    }

    private CourseRequest buildCourseRequest(UUID teacherId) {
        CourseRequest request = new CourseRequest();
        request.setCode("CRS-" + UUID.randomUUID().toString().substring(0, 4));
        request.setTitle("Intro to ORM");
        request.setDescription("Covers entity mappings and repositories");
        request.setCredits(5);
        request.setAuthorId(teacherId);
        request.getKeywords().add("orm");
        return request;
    }

    private TeacherResponse createTeacher() {
        TeacherRequest request = new TeacherRequest();
        request.setFirstName("Teacher");
        request.setLastName(UUID.randomUUID().toString().substring(0, 6));
        request.setBirthDate(LocalDate.of(1984, 1, 1));
        request.setAcademicTitle("Lecturer");
        return personService.createTeacher(request);
    }

    private StudentResponse createStudent() {
        StudentRequest request = new StudentRequest();
        request.setFirstName("Student");
        request.setLastName(UUID.randomUUID().toString().substring(0, 6));
        request.setBirthDate(LocalDate.of(2000, 1, 1));
        String suffix = Long.toString(System.nanoTime());
        request.setStudentNo("STU" + suffix.substring(suffix.length() - 5).toUpperCase());
        request.setStatus(StudentStatus.ACTIVE);
        return personService.createStudent(request);
    }

    private void enrollStudent(StudentResponse student, CourseResponse course) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
        enrollmentRequest.setCourseId(course.getId());
        enrollmentRequest.setStudentId(student.getId());
        enrollmentRequest.setRole(EnrollmentRole.STUDENT);
        enrollmentService.create(enrollmentRequest);
    }

    private QuizRequest buildQuizRequest() {
        QuizRequest quizRequest = new QuizRequest();
        quizRequest.setTitle("Warm up");
        quizRequest.setDescription("Two quick questions");
        quizRequest.setQuestions(List.of(
                buildQuestion("What is ORM?"),
                buildQuestion("Choose persistence frameworks")
        ));
        return quizRequest;
    }

    private QuestionRequest buildQuestion(String text) {
        QuestionRequest question = new QuestionRequest();
        question.setText(text);
        question.setType(text.startsWith("Choose") ? ru.mephi.orm_hibernate_demo.enums.QuestionType.MULTIPLE_CHOICE : ru.mephi.orm_hibernate_demo.enums.QuestionType.SINGLE_CHOICE);
        question.setOptions(List.of(
                buildOption("Hibernate", true),
                buildOption("Spring Data JPA", true)
        ));
        return question;
    }

    private AnswerOptionRequest buildOption(String text, boolean correct) {
        AnswerOptionRequest option = new AnswerOptionRequest();
        option.setText(text);
        option.setCorrect(correct);
        return option;
    }
}
