package org.example.school.controller;

import org.example.school.dto.TeacherRequest;
import org.example.school.dto.TeacherResponse;
import org.example.school.exception.NotFoundException;
import org.example.school.model.Subject;
import org.example.school.service.StudentService;
import org.example.school.service.TeacherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@WebMvcTest(TeacherController.class)
class TeacherControllerTest {
    @MockitoBean
    private TeacherService teacherService;
    @MockitoBean
    private StudentService studentService;
    @Autowired
    private MockMvcTester mvc;

    @Test
    void addStudentTeacherNotFoundReturns404() {
        //given
        String message = "Teacher not found";
        when(teacherService.addStudent(1L, 2L)).thenThrow(new NotFoundException(message));

        //when
        MvcTestResult result = mvc.put()
                .uri("/api/teachers/{teacherId}/students/{studentId}", 1L, 2L)
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
    void saveTeacherInvalidAgeReturns400() {
        //given
        String body = """
        {
          "firstName": "Jane",
          "lastName": "Smith",
          "age": 15,
          "email": "jane.smith@example.com",
          "subject": "MATEMATYKA"
        }
        """;
        String message = "Musi mieć przynajmniej 19 lat";

        //when
        MvcTestResult result = mvc.post()
                .uri("/api/teachers")
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
        verifyNoInteractions(teacherService);
    }

    @Test
    void saveTeacherInvalidSubjectReturns400() {
        //given
        String body = """
        {
          "firstName": "Jane",
          "lastName": "Smith",
          "age": 45,
          "email": "jane.smith@example.com",
          "subject": "ASTROLOGIA"
        }
        """;
        String message = "Nieprawidłowy format żądania";

        //when
        MvcTestResult result = mvc.post()
                .uri("/api/teachers")
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
        verifyNoInteractions(teacherService);
    }

    @Test
    void saveTeacherCorrectDataReturns201AndJSONTeacher() {
        //given
        String body = """
        {
          "firstName": "Jane",
          "lastName": "Smith",
          "age": 45,
          "email": "jane.smith@example.com",
          "subject": "MATEMATYKA"
        }
        """;

        TeacherResponse response = new TeacherResponse(
                1L, "Jane", "Smith", 45, "jane.smith@example.com", Subject.MATEMATYKA, List.of());

        when(teacherService.save(any(TeacherRequest.class))).thenReturn(response);

        //when
        MvcTestResult result = mvc.post()
                .uri("/api/teachers")
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
                  "age": 45,
                  "email": "jane.smith@example.com",
                  "subject": "MATEMATYKA",
                  "students": []
                }
                """);

        verify(teacherService).save(new TeacherRequest("Jane", "Smith", 45, "jane.smith@example.com", Subject.MATEMATYKA));
    }

    @Test
    void removeByIdReturns204() {
        //when
        MvcTestResult result = mvc.delete()
                .uri("/api/teachers/{teacherId}", 1L)
                .exchange();

        //then
        assertThat(result)
                .hasStatus(HttpStatus.NO_CONTENT)
                .body()
                .isEmpty();

        verify(teacherService).removeTeacher(1L);
    }

    @Test
    void saveTeacherInvalidJSONReturns400() {
        //given
        String body = """
        {
          "firstName": "Jane",
          "lastName": Smith,
          "age": 45,
          "email": "jane.smith@example.com",
          "subject": "MATEMATYKA"
        }
        """;
        String message = "Nieprawidłowy format żądania";

        //when
        MvcTestResult result = mvc.post()
                .uri("/api/teachers")
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
        verifyNoInteractions(teacherService);
    }
}
