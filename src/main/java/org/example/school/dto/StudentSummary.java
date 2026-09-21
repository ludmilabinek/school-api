package org.example.school.dto;

import org.example.school.model.Student;

public record StudentSummary(Long id, String firstName, String lastName) {
    public static StudentSummary from(Student student) {
        return new StudentSummary(student.getId(), student.getFirstName(), student.getLastName());
    }
}
