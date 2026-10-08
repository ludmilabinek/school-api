package org.example.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.school.dto.StudentResponse;
import org.example.school.dto.TeacherRequest;
import org.example.school.dto.TeacherResponse;
import org.example.school.service.StudentService;
import org.example.school.service.TeacherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Nauczyciele", description = "Zarządzanie nauczycielami i ich przypisaniami do studentów")
@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;
    private final StudentService studentService;

    public TeacherController(TeacherService teacherService, StudentService studentService) {
        this.teacherService = teacherService;
        this.studentService = studentService;
    }

    @Operation(summary = "Stwórz nauczyciela", operationId = "saveTeacher")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeacherResponse save(@Valid @RequestBody TeacherRequest teacherRequest) {
        return teacherService.save(teacherRequest);
    }

    @Operation(summary = "Edytuj nauczyciela", operationId = "updateTeacher")
    @PutMapping("/{teacherId}")
    @ResponseStatus(HttpStatus.OK)
    public TeacherResponse update(@PathVariable Long teacherId,  @Valid @RequestBody TeacherRequest teacherRequest) {
        return teacherService.updateTeacher(teacherId, teacherRequest);
    }


    @Operation(
            summary = "Przypisz studenta do nauczyciela",
            description = "Idempotentne - ponowne przypisanie tego samego studenta niczego nie zmienia.",
            operationId = "addStudentToTeacher")
    @PutMapping("/{teacherId}/students/{studentId}")
    @ResponseStatus(HttpStatus.OK)
    public TeacherResponse addStudent(@PathVariable Long teacherId, @PathVariable Long studentId) {
        return teacherService.addStudent(teacherId, studentId);
    }

    @Operation(
            summary = "Usuń przypisanie studenta do nauczyciela",
            description = "Usunięcie przypisania studenta nie powoduje usunięcia nauczyciela nawet jak nie ma już przypisanych studentów.",
            operationId = "removeStudentFromTeacher")
    @DeleteMapping("/{teacherId}/students/{studentId}")
    @ResponseStatus(HttpStatus.OK)
    public TeacherResponse removeStudent(@PathVariable Long teacherId, @PathVariable Long studentId) {
        return teacherService.removeStudent(teacherId, studentId);
    }

    @Operation(summary = "Usuń nauczyciela", operationId = "removeTeacherById")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeById(@PathVariable Long id) {
        teacherService.removeTeacher(id);
    }

    @Operation(
            summary = "Wyświetl listę nauczycieli",
            description = "Domyślne sortowanie po nazwisku i imieniu rosnąco. Domyślna wielkość strony 20.",
            operationId = "findAllTeachers")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<TeacherResponse> findAll(@PageableDefault(size = 20, sort = {"lastName", "firstName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return teacherService.findAllPageable(pageable);
    }

    @Operation(
            summary = "Wyszukaj nauczycieli",
            description = "Szukaj po fragmencie nazwiska oraz imienia bez uwzględnienia wielkości liter. " +
                    "Domyślna wartość pusta nazwiska albo imienia pasuje do wszystkich. Domyślne sortowanie po nazwisku i imieniu rosnąco. Domyślna wielkość strony 20.",
            operationId = "searchTeachers")
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Page<TeacherResponse> search(@RequestParam(defaultValue = "") String firstName,
                                        @RequestParam(defaultValue = "") String lastName,
                                        @PageableDefault(size = 20, sort = {"lastName", "firstName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return teacherService.findByFirstNameAndLastName(firstName, lastName, pageable);
    }

    @Operation(
            summary = "Lista studentów przypisanych do nauczyciela",
            description = "Domyślne sortowanie po nazwisku i imieniu rosnąco. Domyślna wielkość strony 20.",
            operationId = "findStudentsAddedToTeacher")
    @GetMapping("/{teacherId}/students")
    @ResponseStatus(HttpStatus.OK)
    public Page<StudentResponse> findByTeacherId(@PathVariable Long teacherId,
                                                 @PageableDefault(size = 20, sort = {"lastName", "firstName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return studentService.findByTeachers_Id(teacherId,  pageable);
    }
}
