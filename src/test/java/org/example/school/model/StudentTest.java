package org.example.school.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StudentTest {

    @Test
    void addTeacherAddOneTeacherCheckIfLinkExistsInBothSides() {
        //given
        Student  student = new Student();
        student.setFirstName("Dave");
        student.setLastName("Michael");

        Teacher teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");

        //when
        student.addTeacher(teacher);

        //then
        assertThat(student.getTeachers()).containsExactly(teacher);
        assertThat(teacher.getStudents()).containsExactly(student);
    }

    @Test
    void removeTeacherRemoveOneTeacherFromTwoCheckIfLinkNotExistsInBothSides() {
        //given
        Student  student = new Student();
        student.setFirstName("Dave");
        student.setLastName("Michael");

        Teacher teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");

        Teacher teacher2 = new Teacher();
        teacher2.setFirstName("Mary");
        teacher2.setLastName("Smith");

        student.addTeacher(teacher);
        student.addTeacher(teacher2);

        //when
        student.removeTeacher(teacher);

        //then
        assertThat(student.getTeachers()).containsExactly(teacher2);
        assertThat(teacher.getStudents()).isEmpty();
        assertThat(teacher2.getStudents()).containsExactly(student);
    }
}
