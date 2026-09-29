package org.example.school.repository;

import org.example.school.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void findsStudentByPartOfLastNameIgnoringCase() {
        //given
        Student nowak = new Student();
        nowak.setFirstName("Jan");
        nowak.setLastName("Nowak");
        nowak.setEmail("jan.nowak@example.com");
        nowak.setAge(25);
        nowak.setFieldOfStudy("Fizyka");

        Student wisniewski = new Student();
        wisniewski.setFirstName("Jan");
        wisniewski.setLastName("Wiśniewski");
        wisniewski.setEmail("jan.wisniewski@example.com");
        wisniewski.setAge(30);
        wisniewski.setFieldOfStudy("Biologia");

        em.persistAndFlush(nowak);
        em.persistAndFlush(wisniewski);
        em.clear();

        //when
        Page<Student> result = studentRepository
                .findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
                        "", "now", PageRequest.of(0, 10));

        //then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent())
                .extracting(Student::getLastName)
                .containsExactly("Nowak");
    }

    @Test
    void findsStudentByFirstNamePartOfLastNameIgnoringCase() {
        //given
        Student nowak = new Student();
        nowak.setFirstName("Jan");
        nowak.setLastName("Nowak");
        nowak.setEmail("jan.nowak@example.com");
        nowak.setAge(25);
        nowak.setFieldOfStudy("Fizyka");

        Student wisniewski = new Student();
        wisniewski.setFirstName("Jan");
        wisniewski.setLastName("Wiśniewski");
        wisniewski.setEmail("jan.wisniewski@example.com");
        wisniewski.setAge(30);
        wisniewski.setFieldOfStudy("Biologia");

        Student knowak = new Student();
        knowak.setFirstName("Katarzyna");
        knowak.setLastName("Nowak");
        knowak.setEmail("katarzyna.nowak@example.com");
        knowak.setAge(28);
        knowak.setFieldOfStudy("Matematyla");

        em.persistAndFlush(nowak);
        em.persistAndFlush(wisniewski);
        em.persistAndFlush(knowak);
        em.clear();

        //when
        Page<Student> result = studentRepository
                .findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
                        "jan", "now", PageRequest.of(0, 10));

        //then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent())
                .extracting(Student::getLastName)
                .containsExactly("Nowak");
    }
}
