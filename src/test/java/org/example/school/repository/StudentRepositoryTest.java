package org.example.school.repository;

import org.example.school.model.Student;
import org.example.school.model.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void findsStudentByPartOfLastNameIgnoringCase() {
        //given
        Student nowak = TestEntities.student("Jan", "Nowak");
        Student wisniewski = TestEntities.student("Jan", "Wiśniewski");

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
        Student nowak = TestEntities.student("Jan", "Nowak");
        Student wisniewski = TestEntities.student("Jan", "Wiśniewski");
        Student knowak = TestEntities.student("Katarzyna", "Nowak");

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
                .extracting(Student::getFirstName, Student::getLastName)
                .containsExactly(tuple("Jan", "Nowak"));

    }

    @Test
    void findByTeacher_Id() {
        //given
        Student nowak = TestEntities.student("Jan", "Nowak");
        Student wisniewski = TestEntities.student("Jan", "Wiśniewski");
        Student knowak = TestEntities.student("Katarzyna", "Nowak");
        Teacher teacher = TestEntities.teacher("Krzysztof", "Staropolski");
        Teacher teacher2 = TestEntities.teacher("Albert", "Niewiadomski");

        teacher.addStudent(nowak);
        teacher.addStudent(wisniewski);
        teacher2.addStudent(knowak);

        em.persistAndFlush(nowak);
        em.persistAndFlush(wisniewski);
        em.persistAndFlush(knowak);
        em.persistAndFlush(teacher);
        em.persistAndFlush(teacher2);
        em.clear();

        //when
        Page<Student> result = studentRepository
                .findByTeachers_Id(
                        teacher.getId(), PageRequest.of(0, 10));

        //then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(Student::getFirstName, Student::getLastName)
                .containsExactlyInAnyOrder(tuple("Jan", "Nowak"), tuple("Jan", "Wiśniewski"));

    }
}
