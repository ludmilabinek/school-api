package org.example.school.controller;

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


@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;
    private final StudentService studentService;

    public TeacherController(TeacherService teacherService, StudentService studentService) {
        this.teacherService = teacherService;
        this.studentService = studentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeacherResponse save(@Valid @RequestBody TeacherRequest teacherRequest) {
        return teacherService.save(teacherRequest);
    }

    @PutMapping("/{teacherId}")
    @ResponseStatus(HttpStatus.OK)
    public TeacherResponse update(@PathVariable Long teacherId,  @Valid @RequestBody TeacherRequest teacherRequest) {
        return teacherService.updateTeacher(teacherId, teacherRequest);
    }


    @PutMapping("/{teacherId}/students/{studentId}")
    @ResponseStatus(HttpStatus.OK)
    public TeacherResponse addStudent(@PathVariable Long teacherId, @PathVariable Long studentId) {
        return teacherService.addStudent(teacherId, studentId);
    }

    @DeleteMapping("/{teacherId}/students/{studentId}")
    @ResponseStatus(HttpStatus.OK)
    public TeacherResponse removeStudent(@PathVariable Long teacherId, @PathVariable Long studentId) {
        return teacherService.removeStudent(teacherId, studentId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeById(@PathVariable Long id) {
        teacherService.removeTeacher(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<TeacherResponse> findAll(@PageableDefault(size = 20, sort = {"lastName", "firstName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return teacherService.findAllPageable(pageable);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Page<TeacherResponse> search(@RequestParam(defaultValue = "") String firstName,
                                        @RequestParam(defaultValue = "") String lastName,
                                        @PageableDefault(size = 20, sort = {"lastName", "firstName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return teacherService.findByFirstNameAndLastName(firstName, lastName, pageable);
    }

    @GetMapping("/{teacherId}/students")
    @ResponseStatus(HttpStatus.OK)
    public Page<StudentResponse> findByTeacherId(@PathVariable Long teacherId,
                                                 @PageableDefault(size = 20, sort = {"lastName", "firstName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return studentService.findByTeachers_Id(teacherId,  pageable);
    }
}
