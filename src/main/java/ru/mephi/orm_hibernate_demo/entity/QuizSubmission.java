package ru.mephi.orm_hibernate_demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.mephi.orm_hibernate_demo.entity.person.Student;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "quiz_submissions")
public class QuizSubmission extends AbstractEntity {

    @Column(name = "score", nullable = false)
    private Double score;

    @Column(name = "attempt_no", nullable = false)
    private Integer attemptNo;

    @Column(name = "taken_at", nullable = false)
    private LocalDateTime takenAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ElementCollection
    @CollectionTable(
            name = "quiz_submission_answers",
            joinColumns = @JoinColumn(name = "submission_id")
    )
    @Column(name = "answer_option_id", columnDefinition = "uuid")
    private Set<java.util.UUID> selectedOptionIds = new HashSet<>();
}
