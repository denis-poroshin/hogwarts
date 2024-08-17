package ru.hogwarts.springhogwars.testRestTemplate.Controllers;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.springhogwars.controllers.FacultyController;
import ru.hogwarts.springhogwars.models.Faculty;
import ru.hogwarts.springhogwars.models.Student;
import ru.hogwarts.springhogwars.repositories.FacultyRepository;
import ru.hogwarts.springhogwars.repositories.StudentRepository;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private FacultyRepository facultyRepository;
    @Autowired
    private StudentRepository studentRepository;

    private Faculty faculty1;
    private Faculty faculty2;
    private Student student1;
    private Student student2;

    @AfterEach
    public void afterEach(){
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @BeforeEach
    public void beforeEach(){
        faculty1 = new Faculty(1L, "Гриффиндор", "Красный");
        faculty2 = new Faculty(2L, "Слизарин", "Синий");

        facultyRepository.saveAll(List.of(faculty1, faculty2));

        student1 = new Student(1L, "Гарри", 11, faculty1);
        student2 = new Student(2L, "Рон", 13, faculty1);

        studentRepository.saveAll(List.of(student1, student2));


    }

    @Test
    public void contextLoads(){
        Assertions.assertThat(facultyController).isNotNull();
    }

    @Test
    public void createFacultyTest(){
        Faculty faculty = new Faculty(3L, "Пуфиндуй", "Голубой");

        ResponseEntity<Faculty> studentResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/faculty", faculty, Faculty.class);

        Assertions.assertThat(studentResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(Objects.requireNonNull(studentResponseEntity.getBody()).getId()).isEqualTo(faculty.getId());
        Assertions.assertThat(studentResponseEntity.getBody()).isEqualTo(faculty);
    }

    @Test
    public void updateFacultyTestPositive(){
        Faculty faculty = new Faculty(1L, "Пуфиндуй", "Голубой");


        testRestTemplate.put("http://localhost:" + port + "/faculty/" + faculty1.getId() , faculty, Faculty.class);
        ResponseEntity<Faculty> facultyResponseEntity = testRestTemplate.getForEntity("http://localhost:" + port + "/faculty/" + faculty1.getId(), Faculty.class);


        Assertions.assertThat(facultyResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(facultyResponseEntity.getBody()).isEqualTo(faculty);

    }
    @Test
    public void getFacultyTestPositive(){
        ResponseEntity<Faculty> facultyResponseEntity = testRestTemplate.getForEntity("http://localhost:" + port + "/faculty/" + faculty1.getId(), Faculty.class);
        Assertions.assertThat(facultyResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(facultyResponseEntity.getBody()).isEqualTo(faculty1);

    }
    @Test
    public void getFacultyTestNegative(){
        Faculty faculty = new Faculty(-1L, "Пуфиндуй", "Голубой");

        ResponseEntity<String> facultyResponseEntity = testRestTemplate.getForEntity("http://localhost:" + port + "/faculty/" + faculty.getId(), String.class);
        Assertions.assertThat(facultyResponseEntity.getBody()).isEqualTo("Факультет с id = %d не найден".formatted(-1));

    }
    @Test
    public void removeFacultyTestPositive() {
        List<Faculty> expectedFacultyList = new ArrayList<>(List.of(faculty1));


        ResponseEntity<Faculty> responseEntityDelete = testRestTemplate.exchange("http://localhost:" + port + "/faculty/" + faculty2.getId(), HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<>() {
                });
        ResponseEntity<List<Faculty>> responseEntity = testRestTemplate.exchange("http://localhost:" + port + "/faculty", HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        List<Faculty> actualFacultyList = responseEntity.getBody();


        Assertions.assertThat(responseEntityDelete.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntityDelete.getBody()).isEqualTo(faculty2);
        Assertions.assertThat(actualFacultyList).usingRecursiveComparison()
                .ignoringFields()
                .isEqualTo(expectedFacultyList);
    }
    @Test
    public void removeFacultyTestNegative() {
        Faculty faculty = new Faculty(-1L, "Пуфиндуй", "Голубой");


        ResponseEntity<String> responseEntity = testRestTemplate.exchange("http://localhost:" + port + "/faculty/" + faculty.getId(), HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<>() {
                });


        Assertions.assertThat(responseEntity.getBody()).isEqualTo("Факультет с id = %d не найден".formatted(-1));
    }
    @Test
    public void getAllFacultyTestPositive() {
        List<Faculty> expectedFacultyList = new ArrayList<>(List.of(faculty1, faculty2));


        ResponseEntity<List<Faculty>> responseEntity = testRestTemplate.exchange("http://localhost:" + port + "/faculty", HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        List<Faculty> actualFacultyList = responseEntity.getBody();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(actualFacultyList).usingRecursiveComparison()
                .ignoringFields()
                .isEqualTo(expectedFacultyList);
    }
    @Test
    public void searchForStudentsByColorTestPositive() {
        String color = "Синий";
        List<Faculty> faculties = new ArrayList<>(List.of(faculty1, faculty2));
        List<Faculty> expectedFaculty = faculties.stream()
                .filter(faculty -> faculty.getColor().equals(color)).toList();


        ResponseEntity<List<Faculty>> responseEntity = testRestTemplate.exchange("http://localhost:" + port + "/faculty/?nameOfColor=" + color, HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                Map.of("nameOfColor", color));
        List<Faculty> actualFacultyList = responseEntity.getBody();


        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(actualFacultyList).usingRecursiveComparison()
                .ignoringFields()
                .isEqualTo(expectedFaculty);
    }
    @Test
    public void searchForAStudentByFacultyTestPositive() {
        List<Student> expectedStudents = new ArrayList<>(List.of(student1, student2));


        ResponseEntity<List<Student>> responseEntity = testRestTemplate.exchange("http://localhost:" + port + "/faculty/" + faculty1.getId() + "/student", HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        List<Student> actualStudentList = responseEntity.getBody();


        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(actualStudentList).usingRecursiveComparison()
                .ignoringFields()
                .isEqualTo(expectedStudents);

    }





}
