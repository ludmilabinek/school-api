package org.example.school.dto;

import org.example.school.model.Subject;
import org.example.school.model.Teacher;

import java.util.List;

public record TeacherResponse(Long id, String  firstName, String  lastName, Integer age, String email, Subject subject, List<StudentSummary> students) {
    public static TeacherResponse from(Teacher teacher) {
        return new TeacherResponse(
                teacher.getId(),
                teacher.getFirstName(),
                teacher.getLastName(),
                teacher.getAge(),
                teacher.getEmail(),
                teacher.getSubject(),
                teacher.getStudents().stream().map(StudentSummary::from).toList()
        );
    }

}
