package org.example.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.school.dto.StudentRequest;
import org.example.school.dto.StudentResponse;
import org.example.school.dto.TeacherResponse;
import org.example.school.service.StudentService;
import org.example.school.service.TeacherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Studenci", description = "Zarządzanie studentami i ich przypisaniami do nauczycieli")
@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService studentService;
    private final TeacherService teacherService;

    public StudentController(StudentService studentService, TeacherService teacherService) {
        this.studentService = studentService;
        this.teacherService = teacherService;
    }

    @Operation(summary = "Stwórz studenta", operationId = "saveStudent")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse save(@Valid @RequestBody StudentRequest studentRequest) {
        return studentService.save(studentRequest);
    }

    @Operation(summary = "Edytuj studenta", operationId = "updateStudent")
    @PutMapping("/{studentId}")
    @ResponseStatus(HttpStatus.OK)
    public StudentResponse update(@PathVariable Long studentId, @Valid @RequestBody StudentRequest studentRequest) {
        return studentService.updateStudent(studentId, studentRequest);
    }

    @Operation(
            summary = "Przypisz nauczyciela do studenta",
            description = "Idempotentne - ponowne przypisanie tego samego nauczyciela niczego nie zmienia.",
            operationId = "addTeacherToStudent")
    @PutMapping("/{studentId}/teachers/{teacherId}")
    @ResponseStatus(HttpStatus.OK)
    public StudentResponse addTeacher(@PathVariable Long studentId, @PathVariable Long teacherId) {
        return studentService.addTeacher(studentId, teacherId);
    }

    @Operation(
            summary = "Usuń przypisanie nauczyciela do studenta",
            description = "Usunięcie przypisania nauczyciela nie powoduje usunięcia studenta nawet jak nie ma już przypisanych nauczycieli.",
            operationId = "removeTeacherFromStudent")
    @DeleteMapping("/{studentId}/teachers/{teacherId}")
    @ResponseStatus(HttpStatus.OK)
    public StudentResponse removeTeacher(@PathVariable Long studentId, @PathVariable Long teacherId) {
        return studentService.removeTeacher(studentId, teacherId);
    }

    @Operation(summary = "Usuń studenta", operationId = "removeStudentById")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeById(@PathVariable Long id) {
        studentService.removeStudent(id);
    }

    @Operation(
            summary = "Wyświetl listę studentów",
            description = "Domyślne sortowanie po nazwisku i imieniu rosnąco. Domyślna wielkość strony 20.",
            operationId = "findAllStudents")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<StudentResponse> findAll(@PageableDefault(size = 20, sort = {"lastName", "firstName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return studentService.findAllPageable(pageable);
    }

    @Operation(
            summary = "Wyszukaj studentów",
            description = "Szukaj po fragmencie nazwiska oraz imienia bez uwzględnienia wielkości liter. " +
                    "Domyślna wartość pusta nazwiska albo imienia pasuje do wszystkich. Domyślne sortowanie po nazwisku i imieniu rosnąco. Domyślna wielkość strony 20.",
            operationId = "searchStudents")
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Page<StudentResponse> search( @RequestParam(defaultValue = "") String firstName,
                                         @RequestParam(defaultValue = "") String lastName,
                                         @PageableDefault(size = 20, sort = {"lastName", "firstName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return studentService.findByFirstNameAndLastName(firstName, lastName, pageable);
    }

    @Operation(
            summary = "Lista nauczycieli przypisanych do studenta",
            description = "Domyślne sortowanie po nazwisku i imieniu rosnąco. Domyślna wielkość strony 20.",
            operationId = "findTeachersAddedToStudent")
    @GetMapping("/{studentId}/teachers")
    @ResponseStatus(HttpStatus.OK)
    public Page<TeacherResponse> findByStudentId(@PathVariable Long studentId,
                                                       @PageableDefault(size = 20, sort = {"lastName", "firstName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return teacherService.findByStudents_Id(studentId,  pageable);
    }
}
