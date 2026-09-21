package org.example.school.dto;

import org.example.school.model.Student;

import java.util.List;

public record StudentResponse(Long id, String firstName, String  lastName, Integer age, String email, String fieldOfStudy, List<TeacherSummary> teachers) {
    public static StudentResponse from(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getAge(),
                student.getEmail(),
                student.getFieldOfStudy(),
                student.getTeachers().stream().map(TeacherSummary::from).toList()
        );
    }
}
