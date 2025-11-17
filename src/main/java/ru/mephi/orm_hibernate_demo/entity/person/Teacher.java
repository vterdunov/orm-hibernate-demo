package ru.mephi.orm_hibernate_demo.entity.person;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.mephi.orm_hibernate_demo.entity.Course;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "teachers")
public class Teacher extends Person {

    @Column(name = "academic_title", length = 100)
    private String academicTitle;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Course> courses = new ArrayList<>();
}
