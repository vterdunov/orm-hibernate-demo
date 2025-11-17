package ru.mephi.orm_hibernate_demo.entity.learning_resources;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.mephi.orm_hibernate_demo.entity.AbstractEntity;
import ru.mephi.orm_hibernate_demo.enums.Complexity;

@Getter
@Setter
@Entity
@Table(name = "learning_resources")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "resource_type", discriminatorType = DiscriminatorType.STRING)
public abstract class LearningResource extends AbstractEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "complexity")
    private Complexity complexity;
}
