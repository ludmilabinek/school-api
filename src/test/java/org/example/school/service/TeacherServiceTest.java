package org.example.school.service;

import org.example.school.dto.*;
import org.example.school.exception.NotFoundException;
import org.example.school.model.Student;
import org.example.school.model.Subject;
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
class TeacherServiceTest {
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private StudentRepository studentRepository;
    @InjectMocks
    private TeacherService teacherService;

    private Teacher teacher() {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Nick");
        teacher.setLastName("Black");
        return teacher;
    }

    private Student student(String firstName, String lastName) {
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        return student;
    }

    private TeacherRequest teacherRequest() {
        return new TeacherRequest("John", "Snow", 53, "john.snow@example.com", Subject.MATEMATYKA);
    }

    @Test
    void addStudentTeacherDoesNotExistThrowNotFound() {
        //given
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());
        //when + then
        NotFoundException e = assertThrows(NotFoundException.class, () -> teacherService.addStudent(1L, 2L));
        assertThat(e.getMessage()).containsIgnoringCase("teacher");
    }

    @Test
    void addStudentStudentDoesNotExistThrowNotFound() {
        //given
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher()));
        when(studentRepository.findById(2L)).thenReturn(Optional.empty());
        //when + then
        NotFoundException e = assertThrows(NotFoundException.class, () -> teacherService.addStudent(1L, 2L));
        assertThat(e.getMessage()).containsIgnoringCase("student");
    }

    @Test
    void addStudentBothExistReturnsTeacherWithStudent() {
        //given
        Teacher teacher = teacher();
        Student student = student("John", "Doe");

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(studentRepository.findById(2L)).thenReturn(Optional.of(student));

        //when
        TeacherResponse teacherResponse = teacherService.addStudent(1L, 2L);
        //then
        assertThat(teacherResponse.students())
                .extracting(StudentSummary::lastName)
                .containsExactlyInAnyOrder("Doe");
    }

    @Test
    void removeTeacherTeacherDoesNotExistThrowNotFound() {
        //given
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());
        //when + then
        NotFoundException e = assertThrows(NotFoundException.class, () -> teacherService.removeTeacher(1L));
        assertThat(e.getMessage()).containsIgnoringCase("teacher");
    }

    @Test
    void removeTeacherTeacherExistsDeletesTeacher() {
        //given
        Teacher teacher = teacher();
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        //when
        teacherService.removeTeacher(1L);

        //then
        verify(teacherRepository).delete(teacher);
    }

    @Test
    void removeStudentTeacherDoesNotExistThrowNotFound() {
        //given
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        //when + then
        NotFoundException e = assertThrows(NotFoundException.class, () -> teacherService.removeStudent(1L, 2L));
        assertThat(e.getMessage()).containsIgnoringCase("teacher");
    }


    @Test
    void removeStudentStudentDoesNotExistThrowNotFound() {
        //given
        Teacher teacher = teacher();

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(studentRepository.findById(2L)).thenReturn(Optional.empty());

        //when + then
        NotFoundException e = assertThrows(NotFoundException.class, () -> teacherService.removeStudent(1L, 2L));
        assertThat(e.getMessage()).containsIgnoringCase("student");
    }

    @Test
    void removeStudentTwoAssignedRemovesOnlyGivenStudent() {
        //given
        Teacher teacher = teacher();
        Student student = student("John", "Doe");
        Student student2 = student("Jane", "Smith");
        teacher.addStudent(student);
        teacher.addStudent(student2);

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(studentRepository.findById(2L)).thenReturn(Optional.of(student));

        //when
        TeacherResponse teacherResponse = teacherService.removeStudent(1L, 2L);

        //then
        assertThat(teacherResponse.students())
                .extracting(StudentSummary::lastName)
                .containsExactlyInAnyOrder("Smith");
    }

    @Test
    void updateTeacherTeacherDoesNotExistThrowNotFound() {
        //given
        TeacherRequest request = teacherRequest();
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());
        //when + then
        NotFoundException e = assertThrows(NotFoundException.class, () -> teacherService.updateTeacher(1L, request));
        assertThat(e.getMessage()).containsIgnoringCase("teacher");
    }

    @Test
    void updateTeacherChangeFirstNameLastNameReturnsUpdatedTeacher() {
        //given
        TeacherRequest request = teacherRequest();
        Teacher teacher = teacher();
        Student student = student("John", "Doe");
        student.addTeacher(teacher);

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        assertThat(teacher.getFirstName()).isNotEqualTo(request.firstName());

        //when
        TeacherResponse editedTeacher = teacherService.updateTeacher(1L, request);
        //then
        assertThat(editedTeacher.firstName()).isEqualTo(request.firstName());
        assertThat(editedTeacher.lastName()).isEqualTo(request.lastName());
        assertThat(editedTeacher.students())
                .extracting(StudentSummary::lastName)
                .containsExactlyInAnyOrder("Doe");
    }
}
