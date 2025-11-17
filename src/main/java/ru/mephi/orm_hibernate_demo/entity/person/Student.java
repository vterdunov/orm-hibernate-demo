package ru.mephi.orm_hibernate_demo.entity.person;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.mephi.orm_hibernate_demo.entity.CourseReview;
import ru.mephi.orm_hibernate_demo.entity.Enrollment;
import ru.mephi.orm_hibernate_demo.entity.QuizSubmission;
import ru.mephi.orm_hibernate_demo.entity.Submission;
import ru.mephi.orm_hibernate_demo.enums.StudentStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "students")
public class Student extends Person {

    @Column(name = "student_no", unique = true, nullable = false, length = 20)
    private String studentNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StudentStatus status;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "student_emails",
            joinColumns = @JoinColumn(name = "student_id")
    )
    @Column(name = "email")
    private Set<String> emails = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "student_phones",
            joinColumns = @JoinColumn(name = "student_id")
    )
    @Column(name = "phone")
    private Set<String> phones = new HashSet<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<Submission> submissions = new ArrayList<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<QuizSubmission> quizSubmissions = new ArrayList<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<CourseReview> courseReviews = new ArrayList<>();
}
