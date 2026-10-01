package org.example.school.repository;

import org.example.school.model.Student;
import org.example.school.model.Subject;
import org.example.school.model.Teacher;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

public class TestEntities {

    private TestEntities() {}

    public static Teacher teacher(String firstName, String lastName) {
        Teacher teacher = new Teacher();
        teacher.setFirstName(firstName);
        teacher.setLastName(lastName);
        teacher.setEmail("adres@example.com");
        teacher.setAge(57);
        teacher.setSubject(Subject.MATEMATYKA);
        return teacher;
    }

    public static Student student(String firstName, String lastName) {
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail("adres@example.com");
        student.setAge(25);
        student.setFieldOfStudy("Informatyka");
        return student;
    }
}
