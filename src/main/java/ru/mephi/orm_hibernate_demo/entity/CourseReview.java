package ru.mephi.orm_hibernate_demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.mephi.orm_hibernate_demo.entity.person.Student;

@Getter
@Setter
@Entity
@Table(
        name = "course_reviews",
        uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "student_id"})
)
public class CourseReview extends AbstractEntity {

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment", length = 2000)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
}
