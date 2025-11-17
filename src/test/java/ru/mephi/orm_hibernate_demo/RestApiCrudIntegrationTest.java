package ru.mephi.orm_hibernate_demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.mephi.orm_hibernate_demo.dto.request.CourseRequest;
import ru.mephi.orm_hibernate_demo.dto.request.LessonRequest;
import ru.mephi.orm_hibernate_demo.dto.request.TagRequest;
import ru.mephi.orm_hibernate_demo.dto.request.TeacherRequest;
import ru.mephi.orm_hibernate_demo.dto.response.CourseResponse;
import ru.mephi.orm_hibernate_demo.dto.response.LessonResponse;
import ru.mephi.orm_hibernate_demo.dto.response.TagResponse;
import ru.mephi.orm_hibernate_demo.dto.response.TeacherResponse;
import ru.mephi.orm_hibernate_demo.service.CourseService;
import ru.mephi.orm_hibernate_demo.service.PersonService;
import ru.mephi.orm_hibernate_demo.support.PostgresIntegrationTest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RestApiCrudIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonService personService;

    @Autowired
    private CourseService courseService;

    @Test
    void courseCrudFlowThroughRestApi() throws Exception {
        UUID teacherId = createTeacher().getId();

        CourseRequest request = baseCourseRequest(teacherId);
        request.setCode("API-" + UUID.randomUUID().toString().substring(0, 4));

        MvcResult createResult = mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.author.id").value(teacherId.toString()))
                .andReturn();

        CourseResponse created = objectMapper.readValue(createResult.getResponse().getContentAsByteArray(), CourseResponse.class);

        request.setTitle("Updated via API");
        mockMvc.perform(put("/api/courses/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated via API"));

        mockMvc.perform(get("/api/courses/{id}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated via API"));

        mockMvc.perform(delete("/api/courses/{id}", created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/courses/{id}", created.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void invalidCoursePayloadReturnsValidationError() throws Exception {
        UUID teacherId = createTeacher().getId();
        CourseRequest request = new CourseRequest();
        request.setAuthorId(teacherId);

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.code").exists())
                .andExpect(jsonPath("$.validationErrors.title").exists());
    }

    @Test
    void lessonEndpointsAllowCreateListAndDelete() throws Exception {
        UUID teacherId = createTeacher().getId();
        CourseResponse course = createCourseViaService(teacherId);

        LessonRequest lessonRequest = new LessonRequest();
        lessonRequest.setTitle("REST lesson");
        lessonRequest.setSyllabus("Explain REST controllers");
        lessonRequest.setResources(List.of("https://example.org/rest"));

        MvcResult lessonResult = mockMvc.perform(post("/api/courses/{courseId}/lessons", course.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(lessonRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        LessonResponse lesson = objectMapper.readValue(lessonResult.getResponse().getContentAsByteArray(), LessonResponse.class);

        mockMvc.perform(get("/api/courses/{courseId}/lessons", course.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("REST lesson"));

        mockMvc.perform(delete("/api/lessons/{id}", lesson.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/courses/{courseId}/lessons", course.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void tagEndpointsAttachCourseAndRemove() throws Exception {
        UUID teacherId = createTeacher().getId();
        CourseResponse course = createCourseViaService(teacherId);

        TagRequest tagRequest = new TagRequest();
        tagRequest.setName("API");
        tagRequest.setSlug("api-" + UUID.randomUUID().toString().substring(0, 8));

        MvcResult tagResult = mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(tagRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        TagResponse tag = objectMapper.readValue(tagResult.getResponse().getContentAsByteArray(), TagResponse.class);

        mockMvc.perform(post("/api/courses/{courseId}/tags/{tagId}", course.getId(), tag.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/courses/{courseId}/tags", course.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value(tag.getSlug()));

        mockMvc.perform(delete("/api/courses/{courseId}/tags/{tagId}", course.getId(), tag.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/courses/{courseId}/tags", course.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private TeacherResponse createTeacher() {
        TeacherRequest request = new TeacherRequest();
        request.setFirstName("HTTP");
        request.setLastName("Teacher");
        request.setBirthDate(LocalDate.of(1985, 3, 3));
        request.setAcademicTitle("Assistant Professor");
        return personService.createTeacher(request);
    }

    private CourseResponse createCourseViaService(UUID teacherId) {
        CourseRequest request = baseCourseRequest(teacherId);
        request.setCode("CRS-" + UUID.randomUUID().toString().substring(0, 4));
        return courseService.create(request);
    }

    private CourseRequest baseCourseRequest(UUID teacherId) {
        CourseRequest request = new CourseRequest();
        request.setAuthorId(teacherId);
        request.setCode("REST-" + UUID.randomUUID().toString().substring(0, 4));
        request.setTitle("REST API Course");
        request.setDescription("Covers controllers and MockMvc");
        request.setCredits(3);
        request.getKeywords().add("rest");
        return request;
    }
}
