package ru.hogwarts.springhogwars.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.springhogwars.exceptions.FacultyNotFoundException;
import ru.hogwarts.springhogwars.exceptions.StudentNotFoundException;
import ru.hogwarts.springhogwars.models.Faculty;
import ru.hogwarts.springhogwars.models.Student;
import ru.hogwarts.springhogwars.repositories.FacultyRepository;
import ru.hogwarts.springhogwars.repositories.StudentRepository;

import java.util.Collection;
import java.util.Collections;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;


    private Logger logger = LoggerFactory.getLogger(StudentService.class);


    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }


    public Student createStudent(Student student){
        logger.info("Creating student: {}", student);
        Faculty faculty = null;
        if(student.getFaculty() != null && student.getFaculty().getId() != null){
            faculty = facultyRepository.findById(student.getFaculty().getId())
                    .orElseThrow(() -> new FacultyNotFoundException(student.getFaculty().getId()));
        }
        student.setFaculty(faculty);
        student.setId(null);
        return studentRepository.save(student);
    }
    public void updateStudent(long id, Student student){
        logger.info("Updating student id = {}, student: {}", id, student);
        Student newStudent = studentRepository.findById(id).orElseThrow(
                () -> new StudentNotFoundException(id));
        Faculty faculty = null;
        if(student.getFaculty() != null && student.getFaculty().getId() != null){
            faculty = facultyRepository.findById(student.getFaculty().getId())
                    .orElseThrow(() -> new FacultyNotFoundException(student.getFaculty().getId()));
        }

        newStudent.setName(student.getName());
        newStudent.setAge(student.getAge());
        newStudent.setFaculty(faculty);
        studentRepository.save(newStudent);
    }
    public Student getStudent(long id){
        logger.info("Retrieving student id = {}", id);
        return studentRepository.findById(id).orElseThrow(
                () -> new StudentNotFoundException(id));
    }
    public Student removeStudent(long id){
        logger.info("Removing student id = {}", id);
        Student remoteStudent = studentRepository.findById(id).orElseThrow(
                () -> new StudentNotFoundException(id));
        studentRepository.delete(remoteStudent);
        return remoteStudent;

    }
    public Collection<Student> getAllStudent(){
        logger.info("Retrieving all students");
        return Collections.unmodifiableCollection(studentRepository.findAll()); // создаст неизменяемую копию листа
    }
    public Collection<Student> findByAgeBetween(int minAge, int maxAge){
        logger.info("Retrieving student by age between {} and {}", minAge, maxAge);
        return studentRepository.findByAgeBetween(minAge, maxAge);
    }
    public Faculty searchForFacultyByStudentId(long id){
        logger.info("Retrieving faculty by id = {}", id);
        Student student = studentRepository.findById(id).orElseThrow(() -> new FacultyNotFoundException(id));
        return student.getFaculty();
    }
    public Collection<Student> searchForStudentsByAge(int age){
        logger.info("Retrieving students by age {}", age);
        return studentRepository.findAllByAge(age);
    }
    public Integer getNumberOfStudents(){
        logger.info("Retrieving number of students");
        return studentRepository.getNumberOfStudents();
    }
    public Double getMiddleAged(){
        logger.info("Retrieving middle age");
        return studentRepository.getMiddleAged();
    }
    public Collection<Student> getNewFiveStudents(){
        logger.info("Retrieving new five students");
        return studentRepository.getNewFiveStudents();
    }

}