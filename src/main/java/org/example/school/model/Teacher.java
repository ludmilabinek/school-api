package org.example.school.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "teachers")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Integer age;
    @Enumerated(EnumType.STRING)
    private Subject subject;

    @ManyToMany()
    @JoinTable(
            name = "teachers_students",
            joinColumns = @JoinColumn(name="teacher_id"),
            inverseJoinColumns = @JoinColumn(name="student_id")
    )
    private Set<Student> students = new HashSet<>();

    public Teacher() {
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAge() {
        return age;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public void addStudent(Student student) {
        this.students.add(student);
        student.getTeachers().add(this);

    }

    public void removeStudent(Student student) {
        this.students.remove(student);
        student.getTeachers().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Teacher teacher)) return false;
        return id!=null && Objects.equals(id, teacher.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}