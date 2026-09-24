package org.example.school.controller;

import org.example.school.dto.StudentRequest;
import org.example.school.dto.StudentResponse;
import org.example.school.exception.NotFoundException;
import org.example.school.service.StudentService;
import org.example.school.service.TeacherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@WebMvcTest(StudentController.class)
class StudentControllerTest {
    @MockitoBean
    private StudentService studentService;
    @MockitoBean
    private TeacherService teacherService;
    @Autowired
    private MockMvcTester mvc;

    @Test
    void addTeacherStudentNotFoundReturns404() {
        //given
        String message = "Student not found";
        when(studentService.addTeacher(1L, 2L)).thenThrow(new NotFoundException(message));

        //when
        MvcTestResult result = mvc.put()
                .uri("/api/students/{studentId}/teachers/{teacherId}", 1L, 2L)
                .exchange();

        //then
        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson()
                .extractingPath("$.message")
                .asString()
                .isEqualTo(message);
    }

    @Test
    void saveStudentInvalidAgeReturns400() {
        //given
        String body = """
                {
                  "firstName": "Jane",
                  "lastName": "Smith",
                  "age": 15,
                  "email": "jane.smith@example.com",
                  "fieldOfStudy": "Fizyka"
                }
                """;
        String message = "Musi mieć przynajmniej 19 lat";

        //when
        MvcTestResult result = mvc.post()
                .uri("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();

        //then
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.errors.age")
                .asString()
                .isEqualTo(message);
        verifyNoInteractions(studentService);
    }

    @Test
    void saveStudentCorrectDataReturns201AndJSONStudent() {
        //given
        String body = """
                {
                  "firstName": "Jane",
                  "lastName": "Smith",
                  "age": 25,
                  "email": "jane.smith@example.com",
                  "fieldOfStudy": "Fizyka"
                }
                """;

        StudentResponse response = new StudentResponse(
                1L, "Jane", "Smith", 25, "jane.smith@example.com", "Fizyka", List.of());

        when(studentService.save(any(StudentRequest.class))).thenReturn(response);

        //when
        MvcTestResult result = mvc.post()
                .uri("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();

        //then
        assertThat(result)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .isLenientlyEqualTo("""
                        {
                          "id": 1,
                          "firstName": "Jane",
                          "lastName": "Smith",
                          "age": 25,
                          "email": "jane.smith@example.com",
                          "fieldOfStudy": "Fizyka",
                          "teachers": []
                        }
                        """);

        verify(studentService).save(new StudentRequest("Jane", "Smith", 25, "jane.smith@example.com", "Fizyka"));
    }

    @Test
    void removeByIdReturns204() {
        //when
        MvcTestResult result = mvc.delete()
                .uri("/api/students/{studentId}", 1L)
                .exchange();

        //then
        assertThat(result)
                .hasStatus(HttpStatus.NO_CONTENT)
                .body()
                .isEmpty();

        verify(studentService).removeStudent(1L);
    }

    @Test
    void saveStudentInvalidJSONReturns400() {
        //given
        String body = """
                {
                  "firstName": "Jane",
                  "lastName": Smith,
                  "age": 25,
                  "email": "jane.smith@example.com",
                  "fieldOfStudy": "Fizyka"
                }
                """;
        String message = "Nieprawidłowy format żądania";

        //when
        MvcTestResult result = mvc.post()
                .uri("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();

        //then
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.message")
                .asString()
                .isEqualTo(message);
        verifyNoInteractions(studentService);
    }

    @Test
    void findAllPageableTwoStudentsReturns200AndJSONStudents() {
        //given
        StudentResponse jane = new StudentResponse(
                1L, "Jane", "Smith", 25, "jane.smith@example.com", "Fizyka", List.of());
        StudentResponse john = new StudentResponse(
                2L, "John", "Newton", 27, "john.newton@example.com", "Biologia", List.of());

        Page<StudentResponse> page = new PageImpl<>(
                List.of(jane, john),
                PageRequest.of(0, 2),
                5);

        when(studentService.findAllPageable(any())).thenReturn(page);

        //when
        MvcTestResult result = mvc.get()
                .uri("/api/students")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        //then
        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .extractingPath("$.content[0].firstName")
                .isEqualTo("Jane");

        assertThat(result)
                .bodyJson()
                .extractingPath("$.content[1].firstName")
                .isEqualTo("John");

        assertThat(result)
                .bodyJson()
                .extractingPath("$.totalElements")
                .isEqualTo(5);

        assertThat(result)
                .bodyJson()
                .extractingPath("$.totalPages")
                .isEqualTo(3);
    }

    @Test
    void findAllWithoutParamsPassesDefaultPageableToService() {
        //given
        when(studentService.findAllPageable(any())).thenReturn(Page.empty());

        //when
        MvcTestResult result = mvc.get()
                .uri("/api/students")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        //then
        assertThat(result).hasStatus(HttpStatus.OK);

        verify(studentService).findAllPageable(
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "lastName", "firstName")));
    }

    @Test
    void searchTwoSearchParamsReturns200() {
        //given
        when(studentService.findByFirstNameAndLastName(any(), any(), any())).thenReturn(Page.empty());

        //when
        MvcTestResult result = mvc.get()
                .uri("/api/students/search?firstName=Jane&lastName=Smith")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        //then
        assertThat(result).hasStatus(HttpStatus.OK);

        verify(studentService).findByFirstNameAndLastName("Jane", "Smith",
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "lastName", "firstName")));
    }

    @Test
    void searchDefaultParamsReturns200() {
        //given
        when(studentService.findByFirstNameAndLastName(any(), any(), any())).thenReturn(Page.empty());

        //when
        MvcTestResult result = mvc.get()
                .uri("/api/students/search")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        //then
        assertThat(result).hasStatus(HttpStatus.OK);

        verify(studentService).findByFirstNameAndLastName("", "",
                PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "lastName", "firstName")));
    }
}
