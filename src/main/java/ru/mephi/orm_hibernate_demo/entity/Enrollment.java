package ru.mephi.orm_hibernate_demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.mephi.orm_hibernate_demo.entity.person.Student;
import ru.mephi.orm_hibernate_demo.enums.EnrollmentRole;
import ru.mephi.orm_hibernate_demo.enums.EnrollmentStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "enrollments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"})
)
public class Enrollment extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private EnrollmentRole role;

    @Column(name = "enrolled_at", nullable = false)
    private LocalDateTime enrolledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EnrollmentStatus status;

    @Column(name = "final_grade")
    private Double finalGrade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
