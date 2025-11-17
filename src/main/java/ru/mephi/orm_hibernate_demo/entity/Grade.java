package ru.mephi.orm_hibernate_demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "grades")
public class Grade extends AbstractEntity {

    @Column(name = "points", nullable = false)
    private Double points;

    @Column(name = "graded_at", nullable = false)
    private LocalDateTime gradedAt;

    @Column(name = "comment", length = 1000)
    private String comment;

    @Version
    @Column(name = "version")
    private Long version;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Submission submission;
}
