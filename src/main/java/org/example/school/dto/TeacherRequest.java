package org.example.school.dto;

import jakarta.validation.constraints.*;
import org.example.school.model.Subject;
import org.example.school.model.Teacher;

public record TeacherRequest(
        @NotBlank(message = "imie jest wymagane")
        @Size(min = 3, message = "imię powinno mieć minimalnie 3 znaki")
        String firstName,

        @NotBlank(message = "nazwisko jest wymagane")
        @Size(min = 3, message = "nazwisko powinno mieć minimalnie 3 znaki")
        String lastName,

        @NotNull(message = "wiek jest wymagany")
        @Min(value = 19, message = "Musi mieć przynajmniej 19 lat")
        Integer age,

        @NotBlank(message = "email jest wymagany")
        @Email(message = "niepoprawny format email")
        String email,

        @NotNull(message = "przedmiot jest wymagany")
        Subject subject
) {
        public  Teacher toEntity() {
                Teacher teacher = new Teacher();
                teacher.setAge(this.age);
                teacher.setFirstName(this.firstName);
                teacher.setLastName(this.lastName);
                teacher.setEmail(this.email);
                teacher.setSubject(this.subject);
                return teacher;
        }

        public Teacher applyTo(Teacher teacher) {
                teacher.setFirstName(this.firstName);
                teacher.setLastName(this.lastName);
                teacher.setEmail(this.email);
                teacher.setAge(this.age);
                teacher.setSubject(this.subject);
                return teacher;
        }
}
