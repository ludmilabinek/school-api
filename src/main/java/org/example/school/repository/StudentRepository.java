package org.example.school.repository;

import org.example.school.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @EntityGraph(attributePaths = "teachers")
    Page<Student> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "teachers")
    Page<Student> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(String firstName, String lastName, Pageable pageable);

    @EntityGraph(attributePaths = "teachers")
    Page<Student> findByTeachers_Id(Long teacherId, Pageable pageable);
}
