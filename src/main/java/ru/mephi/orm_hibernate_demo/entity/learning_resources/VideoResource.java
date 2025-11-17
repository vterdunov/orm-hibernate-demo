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
@DiscriminatorValue("VIDEO")
public class VideoResource extends LearningResource {

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "duration_sec")
    private Integer durationSec;

    @Column(name = "transcript_url", length = 500)
    private String transcriptUrl;
}
