package org.example.school.repository;

import org.example.school.model.Student;
import org.example.school.model.Subject;
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
        knowak.setFieldOfStudy("Matematyka");

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
        knowak.setFieldOfStudy("Matematyka");

        Teacher teacher = new Teacher();
        teacher.setFirstName("Krzysztof");
        teacher.setLastName("Staropolski");
        teacher.setSubject(Subject.MATEMATYKA);
        teacher.setEmail("krzysztof.staropolski@example.com");
        teacher.setAge(50);

        teacher.addStudent(nowak);
        teacher.addStudent(wisniewski);

        Teacher teacher2 = new Teacher();
        teacher2.setFirstName("Albert");
        teacher2.setLastName("Niewiadomski");
        teacher2.setSubject(Subject.GEOGRAFIA);
        teacher2.setEmail("albert.niewiadomski@example.com");
        teacher2.setAge(53);

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
