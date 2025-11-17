package ru.mephi.orm_hibernate_demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tags")
public class Tag extends AbstractEntity {

    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;

    @Column(name = "slug", unique = true, nullable = false, length = 50)
    private String slug;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<Course> courses = new HashSet<>();
/**
 *     @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
 *     private Set<CourseTag> courseTags = new HashSet<>();
 **/
}

/*
@Getter
@Setter
@Entity
@Table(
        name = "course_tags",
        uniqueConstraints = @UniqueConstraint(name = "uk_course_tag", columnNames = {"course_id", "tag_id"})
)
@AllArgsConstructor @NoArgsConstructor
class CourseTag extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;
}
*/