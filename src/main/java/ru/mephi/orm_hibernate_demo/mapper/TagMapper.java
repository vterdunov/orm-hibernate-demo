package ru.mephi.orm_hibernate_demo.mapper;

import org.mapstruct.*;
import ru.mephi.orm_hibernate_demo.dto.request.TagRequest;
import ru.mephi.orm_hibernate_demo.dto.response.TagResponse;
import ru.mephi.orm_hibernate_demo.dto.response.TagShortResponse;
import ru.mephi.orm_hibernate_demo.entity.Tag;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    @Mapping(target = "courses", ignore = true)
    Tag toEntity(TagRequest request);

    TagResponse toResponse(Tag tag);

    TagShortResponse toShortResponse(Tag tag);

    List<TagResponse> toResponseList(List<Tag> tags);

    List<TagShortResponse> toShortResponseList(List<Tag> tags);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "courses", ignore = true)
    void updateEntityFromRequest(TagRequest request, @MappingTarget Tag tag);

    @AfterMapping
    default void afterToEntity(@MappingTarget Tag tag, TagRequest request) {
        if (tag.getSlug() == null || tag.getSlug().isEmpty()) {
            tag.setSlug(request.getName().toLowerCase().replaceAll("\\s+", "-"));
        }
    }
}
