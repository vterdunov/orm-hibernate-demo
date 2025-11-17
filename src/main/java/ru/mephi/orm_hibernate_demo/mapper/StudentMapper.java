package ru.mephi.orm_hibernate_demo.mapper;

import org.mapstruct.*;
import ru.mephi.orm_hibernate_demo.dto.request.StudentRequest;
import ru.mephi.orm_hibernate_demo.dto.response.StudentResponse;
import ru.mephi.orm_hibernate_demo.entity.person.Student;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {

    Student toEntity(StudentRequest request);

    StudentResponse toResponse(Student student);

    List<StudentResponse> toResponseList(List<Student> students);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    @Mapping(target = "submissions", ignore = true)
    void updateEntityFromRequest(StudentRequest request, @MappingTarget Student student);

    @BeforeMapping
    default void validateStudentNo(StudentRequest request) {
        if (request.getStudentNo() != null && !request.getStudentNo().matches("^[A-Z0-9]{5,20}$")) {
            throw new IllegalArgumentException("Invalid student number format");
        }
    }

    @AfterMapping
    default void normalizeCollections(@MappingTarget Student student) {
        if (student.getEmails() != null) {
            student.getEmails().removeIf(email -> email == null || email.trim().isEmpty());
        }
        if (student.getPhones() != null) {
            student.getPhones().removeIf(phone -> phone == null || phone.trim().isEmpty());
        }
    }
}
