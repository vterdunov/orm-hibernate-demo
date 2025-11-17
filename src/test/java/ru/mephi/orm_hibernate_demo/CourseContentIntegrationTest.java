package ru.mephi.orm_hibernate_demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.mephi.orm_hibernate_demo.dto.request.CourseRequest;
import ru.mephi.orm_hibernate_demo.dto.request.LessonRequest;
import ru.mephi.orm_hibernate_demo.dto.request.TagRequest;
import ru.mephi.orm_hibernate_demo.dto.request.TeacherRequest;
import ru.mephi.orm_hibernate_demo.dto.response.CourseResponse;
import ru.mephi.orm_hibernate_demo.dto.response.LessonResponse;
import ru.mephi.orm_hibernate_demo.dto.response.TagResponse;
import ru.mephi.orm_hibernate_demo.dto.response.TeacherResponse;
import ru.mephi.orm_hibernate_demo.service.PersonService;
import ru.mephi.orm_hibernate_demo.support.PostgresIntegrationTest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CourseContentIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonService personService;

    @Test
    void shouldCreateCourseAttachTagAndLessonAndExposeViaApi() throws Exception {
        TeacherRequest teacherRequest = new TeacherRequest();
        teacherRequest.setFirstName("Alice");
        teacherRequest.setLastName("Instructor");
        teacherRequest.setAcademicTitle("Senior Lecturer");
        teacherRequest.setBirthDate(LocalDate.of(1985, 5, 20));
        teacherRequest.setExternalRef("ext-" + UUID.randomUUID());

        TeacherResponse teacher = personService.createTeacher(teacherRequest);

        TagRequest tagRequest = new TagRequest();
        tagRequest.setName("Persistence");
        String slug = "persistence-" + UUID.randomUUID().toString().substring(0, 8);
        tagRequest.setSlug(slug);

        String tagBody = objectMapper.writeValueAsString(tagRequest);
        String tagResponseJson = mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tagBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value(slug))
                .andReturn()
                .getResponse()
                .getContentAsString();
        TagResponse tagResponse = objectMapper.readValue(tagResponseJson, TagResponse.class);

        CourseRequest courseRequest = new CourseRequest();
        courseRequest.setCode("ORM-" + UUID.randomUUID().toString().substring(0, 4));
        courseRequest.setTitle("ORM Fundamentals");
        courseRequest.setDescription("Course that covers Hibernate and repositories.");
        courseRequest.setCredits(4);
        courseRequest.getKeywords().addAll(List.of("orm", "hibernate"));
        courseRequest.setAuthorId(teacher.getId());

        String courseResponseJson = mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.author.id").value(teacher.getId().toString()))
                .andReturn()
                .getResponse()
                .getContentAsString();
        CourseResponse courseResponse = objectMapper.readValue(courseResponseJson, CourseResponse.class);

        mockMvc.perform(post("/api/courses/{courseId}/tags/{tagId}", courseResponse.getId(), tagResponse.getId()))
                .andExpect(status().isNoContent());

        LessonRequest lessonRequest = new LessonRequest();
        lessonRequest.setTitle("Introduction to ORM");
        lessonRequest.setSyllabus("Discuss goals of ORM and how to model aggregates.");
        lessonRequest.setResources(List.of("https://example.org/orm/intro"));

        String lessonResponseJson = mockMvc.perform(post("/api/courses/{courseId}/lessons", courseResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lessonRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        LessonResponse lessonResponse = objectMapper.readValue(lessonResponseJson, LessonResponse.class);

        mockMvc.perform(get("/api/courses/{id}", courseResponse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("ORM Fundamentals"))
                .andExpect(jsonPath("$.keywords", hasItem("orm")));

        mockMvc.perform(get("/api/courses/{courseId}/tags", courseResponse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value(slug));

        mockMvc.perform(get("/api/courses/{courseId}/lessons", courseResponse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(lessonResponse.getId().toString()))
                .andExpect(jsonPath("$[0].title").value("Introduction to ORM"));

        mockMvc.perform(get("/api/tags/{id}/courses", tagResponse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(courseResponse.getId().toString()));

        assertThat(lessonResponse.getCourse()).isNotNull();
        assertThat(lessonResponse.getCourse().getId()).isEqualTo(courseResponse.getId());
    }
}
