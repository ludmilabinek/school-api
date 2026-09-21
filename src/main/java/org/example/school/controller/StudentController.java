package org.example.school.controller;

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


@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService studentService;
    private final TeacherService teacherService;

    public StudentController(StudentService studentService, TeacherService teacherService) {
        this.studentService = studentService;
        this.teacherService = teacherService;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse save(@Valid @RequestBody StudentRequest studentRequest) {
        return studentService.save(studentRequest);
    }

    @PutMapping("/{studentId}")
    @ResponseStatus(HttpStatus.OK)
    public StudentResponse update(@PathVariable Long studentId, @Valid @RequestBody StudentRequest studentRequest) {
        return studentService.updateStudent(studentId, studentRequest);
    }

    @PutMapping("/{studentId}/teachers/{teacherId}")
    @ResponseStatus(HttpStatus.OK)
    public StudentResponse addTeacher(@PathVariable Long studentId, @PathVariable Long teacherId) {
        return studentService.addTeacher(studentId, teacherId);
    }

    @DeleteMapping("/{studentId}/teachers/{teacherId}")
    @ResponseStatus(HttpStatus.OK)
    public StudentResponse removeTeacher(@PathVariable Long studentId, @PathVariable Long teacherId) {
        return studentService.removeTeacher(studentId, teacherId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeById(@PathVariable Long id) {
        studentService.removeStudent(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<StudentResponse> findAll(@PageableDefault(size = 20, sort = {"lastName", "firstName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return studentService.findAllPageable(pageable);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Page<StudentResponse> search( @RequestParam(defaultValue = "") String firstName,
                                         @RequestParam(defaultValue = "") String lastName,
                                         @PageableDefault(size = 20, sort = {"lastName", "firstName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return studentService.findByFirstNameAndLastName(firstName, lastName, pageable);
    }

    @GetMapping("/{studentId}/teachers")
    @ResponseStatus(HttpStatus.OK)
    public Page<TeacherResponse> findByStudentId(@PathVariable Long studentId,
                                                       @PageableDefault(size = 20, sort = {"lastName", "firstName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return teacherService.findByStudents_Id(studentId,  pageable);
    }
}
