package org.example.school.config;

import org.example.school.model.Student;
import org.example.school.model.Subject;
import org.example.school.model.Teacher;
import org.example.school.repository.StudentRepository;
import org.example.school.repository.TeacherRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public DataInitializer(StudentRepository studentRepository, TeacherRepository teacherRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    public void run(String... args) {
        if (studentRepository.count() != 0 || teacherRepository.count() != 0) {
            return;
        }

        Student student1 = createStudent("Paweł", "Nowakowski", "pawel.nowakowski@example.com", 27, "Matematyka");
        Student student2 = createStudent("Andrzej", "Kowalski", "andrzej.kowalski@example.com", 25, "Geografia");
        Student student3 = createStudent("Joanna", "Nowak", "joanna.nowak@example.com", 26, "Fizyka");
        Student student4 = createStudent("Maria", "Staropolska", "maria.staropolska@example.com", 27, "Informatyka");
        Student student5 = createStudent("Zbigniew", "Wiśniewski", "zbigniew.wisniewski@example.com", 26, "Biologia");

        Teacher teacher1 = createTeacher("Mateusz", "Wójcik", "mateusz.wojcik@example.com", 43, Subject.MATEMATYKA);
        Teacher teacher2 = createTeacher("Anna", "Kowalczyk", "anna.kowalczyk@example.com", 39, Subject.GEOGRAFIA);
        Teacher teacher3 = createTeacher("Piotr", "Dziuba", "piotr.dziuba@example.com", 47, Subject.FIZYKA);

        studentRepository.save(student1);
        studentRepository.save(student2);
        studentRepository.save(student3);
        studentRepository.save(student4);
        studentRepository.save(student5);

        teacher1.addStudent(student1);
        teacher1.addStudent(student2);
        teacher2.addStudent(student1);
        teacher2.addStudent(student3);
        teacher2.addStudent(student4);

        teacherRepository.save(teacher1);
        teacherRepository.save(teacher2);
        teacherRepository.save(teacher3);
    }


    private Student createStudent(String firstName, String lastName, String email, Integer age, String fieldOfStudy) {
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        student.setAge(age);
        student.setFieldOfStudy(fieldOfStudy);
        return student;
    }

    private Teacher createTeacher(String firstName, String lastName, String email, Integer age, Subject subject) {
        Teacher teacher = new Teacher();
        teacher.setFirstName(firstName);
        teacher.setLastName(lastName);
        teacher.setEmail(email);
        teacher.setAge(age);
        teacher.setSubject(subject);
        return teacher;
    }
}
