package org.example.school.service;

import org.example.school.dto.StudentRequest;
import org.example.school.dto.StudentResponse;
import org.example.school.exception.NotFoundException;
import org.example.school.model.Student;
import org.example.school.model.Teacher;
import org.example.school.repository.StudentRepository;
import org.example.school.repository.TeacherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public StudentService(StudentRepository studentRepository, TeacherRepository teacherRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    private Teacher findTeacherById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Teacher with id " + id + " does not exist"));
    }

    private Student findStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student with id " + id + " does not exist"));
    }

    public StudentResponse save(StudentRequest studentRequest) {
        Student student = studentRepository.save(studentRequest.toEntity());
        return StudentResponse.from(student);
    }

    @Transactional
    public StudentResponse addTeacher(Long studentId, Long teacherId) {
        Student student = this.findStudentById(studentId);

        Teacher teacher = this.findTeacherById(teacherId);

        student.addTeacher(teacher);
        return StudentResponse.from(student);
    }

    @Transactional
    public StudentResponse removeTeacher(Long studentId, Long teacherId) {
        Student student = this.findStudentById(studentId);

        Teacher teacher = this.findTeacherById(teacherId);

        student.removeTeacher(teacher);
        return StudentResponse.from(student);
    }

    @Transactional
    public StudentResponse updateStudent(Long studentId, StudentRequest studentRequest) {
        Student updateStudent = this.findStudentById(studentId);
        return StudentResponse.from(studentRequest.applyTo(updateStudent));
    }

    @Transactional
    public void removeStudent(Long studentId) {
        Student student = this.findStudentById(studentId);
        HashSet<Teacher> teachers = new HashSet<>(student.getTeachers());
        for (Teacher teacher : teachers) {
            teacher.removeStudent(student);
        }
        studentRepository.delete(student);
    }

    @Transactional(readOnly = true)
    public Page<StudentResponse> findAllPageable(Pageable pageable) {
        return studentRepository.findAll(pageable).map(StudentResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<StudentResponse> findByFirstNameAndLastName(String firstName, String lastName, Pageable pageable) {
        return studentRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(firstName, lastName, pageable).map(StudentResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<StudentResponse> findByTeachers_Id(Long teacherId, Pageable pageable) {
        return studentRepository.findByTeachers_Id(teacherId,pageable).map(StudentResponse::from);
    }

}