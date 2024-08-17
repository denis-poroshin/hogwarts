package ru.hogwarts.springhogwars.controllers;

import org.springframework.web.bind.annotation.*;
import ru.hogwarts.springhogwars.models.Faculty;
import ru.hogwarts.springhogwars.models.Student;
import ru.hogwarts.springhogwars.services.FacultyService;

import java.util.Collection;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }
    @PostMapping
    public Faculty addFaculty(@RequestBody Faculty faculty){
        return facultyService.createFaculty(faculty);
    }
    @PutMapping("/{id}")
    public void changeFaculty(@PathVariable long id, @RequestBody Faculty faculty){
        facultyService.updateFaculty(id, faculty);
    }
    @GetMapping("/{id}")
    public Faculty getFaculty(@PathVariable long id){
        return facultyService.getFaculty(id);
    }
    @DeleteMapping("/{id}")
    public Faculty removeFaculty(@PathVariable long id){
        return facultyService.removeFaculty(id);
    }
    @GetMapping
    public Collection<Faculty> getAllFaculty(){
        return facultyService.getAllFaculty();
    }
    @GetMapping("/")
    public Collection<Faculty> searchForStudentsByColor(@RequestParam("nameOfColor") String nameOfColor){
        return facultyService.findFacultyByNameOrColor(nameOfColor);
    }
    @GetMapping("/{id}/student")
    public Collection<Student> searchForAStudentByFaculty(@PathVariable long id){
        return facultyService.searchForAStudentByFaculty(id);
    }

}

