package ru.mephi.orm_hibernate_demo.entity.learning_resources;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import ru.mephi.orm_hibernate_demo.entity.learning_resources.LearningResource;

@Getter
@Setter
@Entity
@DiscriminatorValue("ARTICLE")
public class ArticleResource extends LearningResource {

    @Column(name = "content_url", length = 500)
    private String contentUrl;

    @Column(name = "reading_time_min")
    private Integer readingTimeMin;
}
