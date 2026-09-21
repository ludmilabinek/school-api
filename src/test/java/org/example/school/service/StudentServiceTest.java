package org.example.school.service;

import org.example.school.dto.StudentRequest;
import org.example.school.dto.StudentResponse;
import org.example.school.dto.TeacherSummary;
import org.example.school.exception.NotFoundException;
import org.example.school.model.Student;
import org.example.school.model.Teacher;
import org.example.school.repository.StudentRepository;
import org.example.school.repository.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @InjectMocks
    private StudentService studentService;

    private Teacher teacher(String firstName, String lastName) {
        Teacher teacher = new Teacher();
        teacher.setFirstName(firstName);
        teacher.setLastName(lastName);
        return teacher;
    }

    private Student student() {
        Student student = new Student();
        student.setFirstName("John");
        student.setLastName("Doe");
        return student;
    }

    private StudentRequest studentRequest() {
        return new StudentRequest("Jane", "Smith", 25, "jane.smith@example.com", "Fizyka");
    }

    @Test
    void addTeacherStudentDoesNotExistThrowNotFound() {
        //given
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());
        //when + then
        NotFoundException e = assertThrows(NotFoundException.class, () -> studentService.addTeacher(1L, 2L));
        assertThat(e.getMessage()).containsIgnoringCase("student");
    }

    @Test
    void addTeacherTeacherDoesNotExistThrowNotFound() {
        //given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student()));
        when(teacherRepository.findById(2L)).thenReturn(Optional.empty());
        //when + then
        NotFoundException e = assertThrows(NotFoundException.class, () -> studentService.addTeacher(1L, 2L));
        assertThat(e.getMessage()).containsIgnoringCase("teacher");
    }

    @Test
    void addTeacherBothExistReturnsStudentWithTeacher() {
        //given
        Student student =  student();
        Teacher teacher = teacher("Nick", "Black");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(teacherRepository.findById(2L)).thenReturn(Optional.of(teacher));
        //when
        StudentResponse studentResponse = studentService.addTeacher(1L, 2L);
        //then
        assertThat(studentResponse.teachers())
                .extracting(TeacherSummary::lastName)
                .containsExactlyInAnyOrder("Black");
    }

    @Test
    void removeStudentStudentDoesNotExistThrowNotFound() {
        //given
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());
        //when + then
        NotFoundException e = assertThrows(NotFoundException.class, () -> studentService.removeStudent(1L));
        assertThat(e.getMessage()).containsIgnoringCase("student");
    }

    @Test
    void removeStudentFromTwoTeachersDetachesFromAllTeachersAndDeletes() {
        //given
        Student student =  student();
        Teacher teacher = teacher("Nick", "Black");
        Teacher teacher2 = teacher("Tom", "Novak");
        teacher.addStudent(student);
        teacher2.addStudent(student);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        //when
        studentService.removeStudent(1L);

        //then
        verify(studentRepository).delete(student);
        assertThat(teacher.getStudents()).isEmpty();
        assertThat(teacher2.getStudents()).isEmpty();
        assertThat(student.getTeachers()).isEmpty();
    }

    @Test
    void updateStudentStudentDoesNotExistThrowNotFound() {
        //given
        StudentRequest request = studentRequest();
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());
        //when + then
        NotFoundException e = assertThrows(NotFoundException.class, () -> studentService.updateStudent(1L, request));
        assertThat(e.getMessage()).containsIgnoringCase("student");
    }

    @Test
    void updateStudentChangeFirstNameLastNameReturnsUpdatedStudent() {
        //given
        StudentRequest request = studentRequest();
        Student student = student();
        Teacher teacher = teacher("Nick", "Black");
        teacher.addStudent(student);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        assertThat(student.getFirstName()).isNotEqualTo(request.firstName());

        //when
        StudentResponse editedStudent = studentService.updateStudent(1L, request);
        //then
        assertThat(editedStudent.firstName()).isEqualTo(request.firstName());
        assertThat(editedStudent.lastName()).isEqualTo(request.lastName());
        assertThat(editedStudent.teachers())
                .extracting(TeacherSummary::lastName)
                .containsExactlyInAnyOrder("Black");
    }
}
