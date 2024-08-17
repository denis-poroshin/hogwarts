package ru.hogwarts.springhogwars.testRestTemplate.Controllers;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import ru.hogwarts.springhogwars.controllers.FacultyController;
import ru.hogwarts.springhogwars.controllers.StudentController;
import ru.hogwarts.springhogwars.models.Faculty;
import ru.hogwarts.springhogwars.models.Student;
import ru.hogwarts.springhogwars.repositories.FacultyRepository;
import ru.hogwarts.springhogwars.repositories.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {


    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private FacultyController facultyController;


    @Autowired
    private TestRestTemplate testRestTemplate;
    private Faculty faculty1;
    private Faculty faculty2;
    private Student student1;
    private Student student2;
    private Student student3;
    private Student student4;
    private Student student5;
    private Student student6;
    @Autowired
    private FacultyRepository facultyRepository;
    @Autowired
    private StudentRepository studentRepository;

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
        student2 = new Student(2L, "Рон", 13, faculty2);
        student3 = new Student(3L, "Рон", 13, faculty2);
        student4 = new Student(4L, "Рон", 13, faculty2);
        student5 = new Student(5L, "Рон", 13, faculty2);
        student6 = new Student(6L, "Рон", 13, faculty2);

        studentRepository.saveAll(List.of(student1, student2, student3, student4, student5, student6));

    }

    @Test
    public void contextLoads(){
        Assertions.assertThat(studentController).isNotNull();
    }

    @Test
    public void createStudentTestPositive() throws Exception{
        Student student = new Student(3L, "Гарри", 11, null);


        ResponseEntity<Student> studentResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/student", student, Student.class);


        Assertions.assertThat(studentResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(Objects.requireNonNull(studentResponseEntity.getBody()).getId()).isEqualTo(student.getId());
        Assertions.assertThat(studentResponseEntity.getBody()).isEqualTo(student);
    }
    @Test
    public void createFacultyTestPositive() throws Exception{
        Student student = new Student(3L, "Гарри", 11, faculty2);


        ResponseEntity<Student> studentResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/student", student, Student.class);


        Assertions.assertThat(studentResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(Objects.requireNonNull(studentResponseEntity.getBody()).getId()).isEqualTo(student.getId());
        Assertions.assertThat(studentResponseEntity.getBody()).isEqualTo(student);
        Assertions.assertThat(studentResponseEntity.getBody().getFaculty()).isEqualTo(faculty2);
    }

    @Test
    public void createStudentTestNegative() throws Exception{
        Faculty facultyNotBD = new Faculty(-1L, "Гриффиндор", "Красный");
        Student student = new Student(1L, "Гарри", 11, facultyNotBD);


        ResponseEntity<String> studentResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/student", student, String.class);
        Assertions.assertThat(studentResponseEntity.getBody()).isEqualTo("Факультет с id = %d не найден".formatted(-1));

    }

    @Test
    public void updateTestPositive(){
        Student newStudent = new Student(1L, "Рон", 15, faculty2);


        testRestTemplate.put("http://localhost:" + port + "/student/" + newStudent.getId(),newStudent, Student.class);
        ResponseEntity<Student> studentResponseEntity = testRestTemplate.getForEntity("http://localhost:" + port + "/student/1", Student.class);


        Assertions.assertThat(studentResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(Objects.requireNonNull(studentResponseEntity.getBody()).getId()).isEqualTo(newStudent.getId());
        Assertions.assertThat(studentResponseEntity.getBody()).isEqualTo(newStudent);
        Assertions.assertThat(studentResponseEntity.getBody().getFaculty()).isEqualTo(faculty2);

    }

    @Test
    public void getStudentTestPositive(){
        ResponseEntity<Student> studentResponseEntity = testRestTemplate.getForEntity("http://localhost:" + port + "/student/" + student1.getId(), Student.class);


        Assertions.assertThat(studentResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(Objects.requireNonNull(studentResponseEntity.getBody()).getId()).isEqualTo(student1.getId());
        Assertions.assertThat(studentResponseEntity.getBody()).isEqualTo(student1);
        Assertions.assertThat(studentResponseEntity.getBody().getFaculty()).isEqualTo(faculty1);
    }
    @Test
    public void getStudentTestNegative(){
        Student newStudent = new Student(-1L, "Рон", 15, faculty2);


        ResponseEntity<String> studentResponseEntity = testRestTemplate.getForEntity("http://localhost:" + port + "/student/" + newStudent.getId(), String.class);


        Assertions.assertThat(studentResponseEntity.getBody()).isEqualTo("Студент с id = %d не найден".formatted(-1));
    }

    @Test
    public void removeStudentTestPositive(){
        List<Student> expectedStudents = new ArrayList<>(List.of(student1));


        ResponseEntity<Student> responseEntityDelete = testRestTemplate.exchange("http://localhost:" + port + "/student/" + student2.getId(), HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<>() {
                });
        ResponseEntity<List<Student>> responseEntity = testRestTemplate.exchange("http://localhost:" + port + "/student", HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        List<Student> actualStudents = responseEntity.getBody();


        Assertions.assertThat(responseEntityDelete.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntityDelete.getBody()).isEqualTo(student2);
        Assertions.assertThat(actualStudents).usingRecursiveAssertion()
                .ignoringAllNullFields()
                .isEqualTo(expectedStudents);

    }
    @Test
    public void removeStudentTestNegative(){
        ResponseEntity<String> responseEntityDelete = testRestTemplate.exchange("http://localhost:" + port + "/student/" + -1, HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<>() {
                });

        Assertions.assertThat(responseEntityDelete.getBody()).isEqualTo("Студент с id = %d не найден".formatted(-1));

    }

    @Test
    public void getAllStudentTest(){
        List<Student> expectedStudents = new ArrayList<>(List.of(student1, student2));


        ResponseEntity<List<Student>> responseEntity = testRestTemplate.exchange("http://localhost:" + port + "/student", HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });


        List<Student> actualStudents = responseEntity.getBody();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(actualStudents).usingRecursiveAssertion()
                .ignoringAllNullFields()
                .isEqualTo(expectedStudents);

    }

    @Test
    public void searchForStudentsByAge1(){
        int age = 11;
        List<Student> students = new ArrayList<>(List.of(student1, student2));
        List<Student> expectedStudents = students.stream()
                .filter(student -> student.getAge() == age)
                .toList();


        ResponseEntity<List<Student>> responseEntity = testRestTemplate.exchange("http://localhost:" + port + "/student/?age=" + age, HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                Map.of("minAge", age));


        List<Student> actualStudents = responseEntity.getBody();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(actualStudents).usingRecursiveAssertion()
                .ignoringAllNullFields()
                .isEqualTo(expectedStudents);

    }

    @Test
    public void findStudentsByAgeBetweenTest(){
        int minAge = 10;
        int maxAge = 12;
        List<Student> students = new ArrayList<>(List.of(student1, student2));
        List<Student> expectedStudents = students.stream()
                .filter(student -> student.getAge() <= maxAge && student.getAge() >= minAge)
                .toList();


        ResponseEntity<List<Student>> responseEntity = testRestTemplate.exchange("http://localhost:" + port + "/student?minAge=" + minAge + "&maxAge=" + maxAge, HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });


        List<Student> actualStudents = responseEntity.getBody();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(actualStudents).usingRecursiveAssertion()
                .ignoringAllNullFields()
                .isEqualTo(expectedStudents);


    }
    @Test
    public void searchForFacultyByStudentIdTest(){
        ResponseEntity<Faculty> responseEntity = testRestTemplate.getForEntity("http://localhost:" + port + "/student/" + student1.getId() + "/faculty",
                Faculty.class);


        Assertions.assertThat(responseEntity).isNotNull();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isEqualTo(student1.getFaculty());
    }
    @Test
    public void getAvatarFromDbTest(){
        LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();

        parameters.add("file", new org.springframework.core.io.ClassPathResource("image.jpg"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<LinkedMultiValueMap<String, Object>>(parameters, headers);

        ResponseEntity<String> response = testRestTemplate.exchange("http://localhost:" + port + "/1/avatar-from-db", HttpMethod.GET, entity, String.class);
        Assertions.assertThat(response.getStatusCode().is2xxSuccessful());

    }
    @Test
    public void getAvatarFromFsTest(){
        LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();

        parameters.add("file", new org.springframework.core.io.ClassPathResource("image.jpg"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<LinkedMultiValueMap<String, Object>>(parameters, headers);

        ResponseEntity<String> response = testRestTemplate.exchange("http://localhost:" + port + "/1/avatar-from-db", HttpMethod.GET, entity, String.class);
        Assertions.assertThat(response.getStatusCode().is2xxSuccessful());

    }
    @Test
    public void getNumberOfStudentsTest(){
        List<Student> students = new ArrayList<>(List.of(student1, student2));
        int expectedStudentsSize = students.size();


        ResponseEntity<Integer> responseEntity = testRestTemplate.getForEntity("http://localhost:" + port + "/student/count", Integer.class);


        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isEqualTo(expectedStudentsSize);

    }
    @Test
    public void getMiddleAgedTest(){
        Double avgStudent = (student1.getAge() + student2.getAge()) / 2.0;


        ResponseEntity<Double> responseEntity = testRestTemplate.getForEntity("http://localhost:" + port + "/student/avg", Double.class);


        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isEqualTo(avgStudent);
    }
    @Test
    public void getNewFiveStudents(){
        List<Student> expectedStudents = new ArrayList<>(List.of(student2, student3, student4, student5, student6));
        ResponseEntity<List<Student>> responseEntity = testRestTemplate.exchange("http://localhost:" + port + "/student/new-five-students", HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        List<Student> actualStudents = responseEntity.getBody();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(actualStudents).usingRecursiveAssertion()
                .ignoringAllNullFields()
                .isEqualTo(expectedStudents);




    }
}
