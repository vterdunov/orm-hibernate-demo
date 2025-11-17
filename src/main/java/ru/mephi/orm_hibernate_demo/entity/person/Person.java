package ru.mephi.orm_hibernate_demo.entity.person;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.mephi.orm_hibernate_demo.entity.AbstractEntity;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "persons")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person extends AbstractEntity {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "external_ref", length = 100)
    private String externalRef;

    @Column(name = "birth_date")
    private LocalDate birthDate;
}
