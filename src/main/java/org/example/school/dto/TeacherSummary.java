package org.example.school.dto;

import org.example.school.model.Teacher;

public record TeacherSummary(Long id, String firstName, String lastName) {
    public static  TeacherSummary from(Teacher teacher) {
        return new TeacherSummary(teacher.getId(), teacher.getFirstName(), teacher.getLastName());
    }
}
