package org.example.school.service;

import org.example.school.dto.TeacherRequest;
import org.example.school.dto.TeacherResponse;
import org.example.school.exception.NotFoundException;
import org.example.school.model.Student;
import org.example.school.model.Teacher;
import org.example.school.repository.StudentRepository;
import org.example.school.repository.TeacherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    public TeacherService(TeacherRepository teacherRepository, StudentRepository studentRepository) {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
    }

    private Teacher findTeacherById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Teacher with id " + id + " does not exist"));
    }

    private Student findStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student with id " + id + " does not exist"));
    }

    public TeacherResponse save(TeacherRequest teacherRequest) {
        Teacher teacher = teacherRepository.save(teacherRequest.toEntity());
        return TeacherResponse.from(teacher);
    }

    @Transactional
    public TeacherResponse addStudent(Long teacherId, Long studentId) {
        Teacher teacher = this.findTeacherById(teacherId);

        Student student = this.findStudentById(studentId);

        teacher.addStudent(student);
        return TeacherResponse.from(teacher);
    }

    @Transactional
    public TeacherResponse removeStudent(Long teacherId, Long studentId) {
        Teacher teacher = this.findTeacherById(teacherId);

        Student student = this.findStudentById(studentId);

        teacher.removeStudent(student);
        return TeacherResponse.from(teacher);
    }

    @Transactional
    public TeacherResponse updateTeacher(Long teacherId, TeacherRequest teacherRequest) {
        Teacher updateTeacher = this.findTeacherById(teacherId);
        return TeacherResponse.from(teacherRequest.applyTo(updateTeacher));
    }

    @Transactional
    public void removeTeacher(Long teacherId) {
        Teacher teacher = this.findTeacherById(teacherId);
        teacherRepository.delete(teacher);
    }

    @Transactional(readOnly = true)
    public Page<TeacherResponse> findAllPageable(Pageable pageable) {
        return teacherRepository.findAll(pageable).map(TeacherResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<TeacherResponse> findByFirstNameAndLastName(String firstName, String lastName, Pageable pageable) {
        return teacherRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(firstName, lastName, pageable).map(TeacherResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<TeacherResponse> findByStudents_Id(Long studentId, Pageable pageable) {
        return teacherRepository.findByStudents_Id(studentId, pageable).map(TeacherResponse::from);
    }
}
