package ru.mephi.orm_hibernate_demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.mephi.orm_hibernate_demo.entity.learning_resources.LearningResource;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "lessons")
public class Lesson extends AbstractEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "syllabus", columnDefinition = "TEXT")
    private String syllabus;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "lesson_resources",
            joinColumns = @JoinColumn(name = "lesson_id")
    )
    @OrderColumn(name = "resource_order")
    @Column(name = "link", length = 500)
    private List<String> resources = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_resource_id")
    private LearningResource primaryResource;
}
