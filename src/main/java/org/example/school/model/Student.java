package org.example.school.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Integer age;
    private String fieldOfStudy;

    @ManyToMany(mappedBy = "students")
    private Set<Teacher> teachers = new HashSet<>();

    public Student() {
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getFieldOfStudy() {
        return fieldOfStudy;
    }

    public void setFieldOfStudy(String fieldOfStudy) {
        this.fieldOfStudy = fieldOfStudy;
    }

    public Set<Teacher> getTeachers() {
        return teachers;
    }

    public void addTeacher(Teacher teacher) {
        teacher.addStudent(this);
    }

    public void removeTeacher(Teacher teacher) {
        teacher.removeStudent(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student student)) return false;
        return id!=null && Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
