package ru.mephi.orm_hibernate_demo.mapper;

import org.mapstruct.*;
import ru.mephi.orm_hibernate_demo.dto.request.TeacherRequest;
import ru.mephi.orm_hibernate_demo.dto.response.TeacherResponse;
import ru.mephi.orm_hibernate_demo.entity.person.Teacher;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeacherMapper {

    Teacher toEntity(TeacherRequest request);

    TeacherResponse toResponse(Teacher teacher);

    List<TeacherResponse> toResponseList(List<Teacher> teachers);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromRequest(TeacherRequest request, @MappingTarget Teacher teacher);
}
