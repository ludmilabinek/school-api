package org.example.school.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TeacherTest {

    @Test
    void addStudentAddOneStudentCheckIfLinkExistsInBothSides() {
        //given
        Teacher teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");

        Student  student = new Student();
        student.setFirstName("Dave");
        student.setLastName("Michael");

        //when
        teacher.addStudent(student);

        //then
        assertThat(teacher.getStudents()).containsExactly(student);
        assertThat(student.getTeachers()).containsExactly(teacher);
    }

    @Test
    void removeStudentRemoveOneStudentFromTwoCheckIfLinkNotExistsInBothSides() {
        //given
        Teacher teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");

        Student student = new Student();
        student.setFirstName("Dave");
        student.setLastName("Michael");

        Student  student2 = new Student();
        student2.setFirstName("Mary");
        student2.setLastName("Smith");

        teacher.addStudent(student);
        teacher.addStudent(student2);

        //when
        teacher.removeStudent(student);

        //then
        assertThat(teacher.getStudents()).containsExactly(student2);
        assertThat(student.getTeachers()).isEmpty();
        assertThat(student2.getTeachers()).containsExactly(teacher);
    }
}
