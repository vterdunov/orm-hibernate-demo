package ru.mephi.orm_hibernate_demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.mephi.orm_hibernate_demo.dto.response.GradeResponse;
import ru.mephi.orm_hibernate_demo.entity.Grade;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GradeMapper {

    GradeResponse toResponse(Grade grade);
}
