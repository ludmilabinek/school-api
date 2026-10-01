package org.example.school.repository;

import org.example.school.model.Student;
import org.example.school.model.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
class TeacherRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    void findsTeacherByPartOfLastNameIgnoringCase() {
        //given
        Teacher nowak = TestEntities.teacher("Jan", "Nowak");
        Teacher wisniewski = TestEntities.teacher("Jan", "Wiśniewski");

        em.persistAndFlush(nowak);
        em.persistAndFlush(wisniewski);
        em.clear();

        //when
        Page<Teacher> result = teacherRepository
                .findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
                        "", "now", PageRequest.of(0, 10));

        //then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent())
                .extracting(Teacher::getLastName)
                .containsExactly("Nowak");
    }

    @Test
    void findsTeacherByFirstNamePartOfLastNameIgnoringCase() {
        //given
        Teacher nowak = TestEntities.teacher("Jan", "Nowak");
        Teacher wisniewski = TestEntities.teacher("Jan", "Wiśniewski");
        Teacher knowak = TestEntities.teacher("Katarzyna", "Nowak");

        em.persistAndFlush(nowak);
        em.persistAndFlush(wisniewski);
        em.persistAndFlush(knowak);
        em.clear();

        //when
        Page<Teacher> result = teacherRepository
                .findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
                        "jan", "now", PageRequest.of(0, 10));

        //then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent())
                .extracting(Teacher::getFirstName, Teacher::getLastName)
                .containsExactly(tuple("Jan", "Nowak"));

    }

    @Test
    void findByStudent_Id() {
        //given
        Teacher nowak = TestEntities.teacher("Jan", "Nowak");
        Teacher wisniewski = TestEntities.teacher("Jan", "Wiśniewski");
        Teacher knowak = TestEntities.teacher("Katarzyna", "Nowak");
        Student student = TestEntities.student("Krzysztof", "Staropolski");
        Student student2 = TestEntities.student("Albert", "Niewiadomski");

        nowak.addStudent(student);
        wisniewski.addStudent(student);
        knowak.addStudent(student2);

        em.persistAndFlush(student);
        em.persistAndFlush(student2);
        em.persistAndFlush(nowak);
        em.persistAndFlush(wisniewski);
        em.persistAndFlush(knowak);

        em.clear();

        //when
        Page<Teacher> result = teacherRepository
                .findByStudents_Id(
                        student.getId(), PageRequest.of(0, 10));

        //then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(Teacher::getFirstName, Teacher::getLastName)
                .containsExactlyInAnyOrder(tuple("Jan", "Nowak"), tuple("Jan", "Wiśniewski"));

    }

    @Test
    void deletingTeacherRemovesLinksAndKeepsStudents() {
        //given
        Teacher nowak = TestEntities.teacher("Jan", "Nowak");

        Student student = TestEntities.student("Krzysztof", "Staropolski");
        Student student2 = TestEntities.student("Albert", "Niewiadomski");

        nowak.addStudent(student);
        nowak.addStudent(student2);

        em.persistAndFlush(student);
        em.persistAndFlush(student2);
        em.persistAndFlush(nowak);
        em.clear();

        Integer studentCountBeforeDelete = jdbc.queryForObject("select count(*) from students", Integer.class);
        Integer linkCountBefore = jdbc.queryForObject("select count(*) from teachers_students", Integer.class);

        //when
        teacherRepository.delete(nowak);
        em.flush();

        //then
        Integer studentCountAfterDelete = jdbc.queryForObject("select count(*) from students", Integer.class);
        Integer linkCountAfter = jdbc.queryForObject("select count(*) from teachers_students", Integer.class);

        assertThat(studentCountBeforeDelete).isEqualTo(2);
        assertThat(studentCountAfterDelete).isEqualTo(2);
        assertThat(linkCountBefore).isEqualTo(2);
        assertThat(linkCountAfter).isEqualTo(0);


    }
}
