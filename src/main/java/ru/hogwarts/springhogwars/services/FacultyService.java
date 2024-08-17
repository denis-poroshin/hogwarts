package ru.hogwarts.springhogwars.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.springhogwars.exceptions.FacultyNotFoundException;
import ru.hogwarts.springhogwars.models.Faculty;
import ru.hogwarts.springhogwars.models.Student;
import ru.hogwarts.springhogwars.repositories.FacultyRepository;
import ru.hogwarts.springhogwars.repositories.StudentRepository;

import java.util.Collection;
import java.util.Collections;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;

    private Logger logger = LoggerFactory.getLogger(FacultyService.class);


    public FacultyService(FacultyRepository facultyRepository, StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }


    public Faculty createFaculty(Faculty faculty){
        logger.info("Faculty created");
        return facultyRepository.save(faculty);
    }
    public void updateFaculty(long id, Faculty faculty){
        logger.info("Faculty updated");
        Faculty newFaculty = facultyRepository.findById(id).orElseThrow(
                () -> new FacultyNotFoundException(id));
        newFaculty.setName(faculty.getName());
        newFaculty.setColor(faculty.getColor());
        facultyRepository.save(newFaculty);
    }
    public Faculty getFaculty(long id){
        logger.info("Faculty found");
        return facultyRepository.findById(id).orElseThrow(
                () -> new FacultyNotFoundException(id));
    }
    public Faculty removeFaculty(long id){
        logger.info("Faculty removed");
        Faculty remoteFaculty = facultyRepository.findById(id).orElseThrow(
                () -> new FacultyNotFoundException(id));

        facultyRepository.delete(remoteFaculty);
        return remoteFaculty;

    }

    public Collection<Faculty> findFacultyByNameOrColor(String nameOfColor){
        logger.info("Faculty found");
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(nameOfColor , nameOfColor);
    }
    public Collection<Student> searchForAStudentByFaculty(long id) {
        logger.info("Student found");
        return studentRepository.findAllByFaculty_Id(id);
    }


    public Collection<Faculty> getAllFaculty(){
        logger.info("Faculty found");
        return Collections.unmodifiableCollection(facultyRepository.findAll()); // создаст неизменяемую копию коллекции
    }

}
