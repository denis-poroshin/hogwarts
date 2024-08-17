package ru.hogwarts.springhogwars.controllers;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.springhogwars.models.Faculty;
import ru.hogwarts.springhogwars.models.Student;
import ru.hogwarts.springhogwars.services.AvatarService;
import ru.hogwarts.springhogwars.services.StudentService;

import java.util.Collection;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
    private final AvatarService avatarService;

    public StudentController(StudentService studentService, AvatarService avatarService) {
        this.studentService = studentService;
        this.avatarService = avatarService;
    }
    @PostMapping
    public Student addStudent(@RequestBody Student student){
        return studentService.createStudent(student);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable long id, @RequestBody Student student){
        studentService.updateStudent(id,student);
    }

    @GetMapping("/{id}")
    public Student getStudent(@PathVariable Long id){
        return studentService.getStudent(id);
    }

    @DeleteMapping("/{id}")
    public Student removeStudent(@PathVariable Long id){
        return studentService.removeStudent(id);
    }

    @GetMapping
    public Collection<Student> getAllStudent(){
        return studentService.getAllStudent();
    }
    @GetMapping(path = "/")
    public Collection<Student> searchForStudentsByAge(@RequestParam("age") int age){
        return studentService.searchForStudentsByAge(age);
    }

    @GetMapping(params = {"minAge", "maxAge"})
    public Collection<Student> findStudentsByAgeBetween(@RequestParam(name = "minAge") int minAge,
                                                        @RequestParam(name = "maxAge") int maxAge){
        return studentService.findByAgeBetween(minAge, maxAge);
    }

    @GetMapping("/{id}/faculty")
    public Faculty searchForFacultyByStudentId(@PathVariable Long id){
        return studentService.searchForFacultyByStudentId(id);
    }

    @GetMapping("/{id}/avatar-from-db")
    public ResponseEntity<byte[]> getAvatarFromDb(@PathVariable long id){
        return buildResponseEntity(avatarService.getAvatarFromDb(id));
    }

    @GetMapping("/{id}/avatar-from-fs")
    public ResponseEntity<byte[]> getAvatarFromFs(@PathVariable long id){
        return buildResponseEntity(avatarService.getAvatarFromFs(id));
    }

    private ResponseEntity<byte[]> buildResponseEntity(Pair<byte[], String> pair){
        byte[] data = pair.getFirst();
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(data.length)
                .contentType(MediaType.parseMediaType(pair.getSecond()))
                .body(data);
    }
    @GetMapping("/count")
    public Integer getNumberOfStudents(){
        return studentService.getNumberOfStudents();
    }
    @GetMapping("/avg")
    public Double getMiddleAged(){
        return studentService.getMiddleAged();
    }
    @GetMapping("/new-five-students")
    public Collection<Student> getNewFiveStudents(){
        return studentService.getNewFiveStudents();
    }
}
