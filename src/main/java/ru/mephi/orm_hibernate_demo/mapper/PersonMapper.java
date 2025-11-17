package ru.mephi.orm_hibernate_demo.mapper;

import org.mapstruct.*;
import ru.mephi.orm_hibernate_demo.dto.response.PersonResponse;
import ru.mephi.orm_hibernate_demo.entity.person.Person;
import ru.mephi.orm_hibernate_demo.entity.person.Student;
import ru.mephi.orm_hibernate_demo.entity.person.Teacher;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {

    @Mapping(target = "type", expression = "java(getPersonType(person))")
    PersonResponse toResponse(Person person);

    List<PersonResponse> toResponseList(List<Person> persons);

    default String getPersonType(Person person) {
        if (person instanceof Student) {
            return "STUDENT";
        } else if (person instanceof Teacher) {
            return "TEACHER";
        }
        return "UNKNOWN";
    }
}
