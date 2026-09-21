package org.example.school.dto;

import jakarta.validation.constraints.*;
import org.example.school.model.Student;

public record StudentRequest(
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

    @NotNull(message = "kierunek jest wymagany")
    String fieldOfStudy
)
{
    public Student toEntity() {
        Student student = new Student();
        student.setAge(this.age);
        student.setFirstName(this.firstName);
        student.setLastName(this.lastName);
        student.setEmail(this.email);
        student.setFieldOfStudy(this.fieldOfStudy);
        return student;
    }

    public Student applyTo(Student student) {
        student.setFirstName(this.firstName);
        student.setLastName(this.lastName);
        student.setEmail(this.email);
        student.setAge(this.age);
        student.setFieldOfStudy(this.fieldOfStudy);
        return student;
    }
}
