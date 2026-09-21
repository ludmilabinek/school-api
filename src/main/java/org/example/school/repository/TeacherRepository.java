package org.example.school.repository;

import org.example.school.model.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    @EntityGraph(attributePaths = "students")
    Page<Teacher> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "students")
    Page<Teacher> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(String firstName, String lastName, Pageable pageable);

    @EntityGraph(attributePaths = "students")
    Page<Teacher> findByStudents_Id(Long studentId, Pageable pageable);
}
