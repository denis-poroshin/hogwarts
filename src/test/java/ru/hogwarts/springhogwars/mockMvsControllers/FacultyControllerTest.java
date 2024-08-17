package ru.hogwarts.springhogwars.mockMvsControllers;

import org.assertj.core.api.Assertions;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.springhogwars.controllers.FacultyController;
import ru.hogwarts.springhogwars.models.Faculty;
import ru.hogwarts.springhogwars.models.Student;
import ru.hogwarts.springhogwars.repositories.AvatarRepository;
import ru.hogwarts.springhogwars.repositories.FacultyRepository;
import ru.hogwarts.springhogwars.repositories.StudentRepository;
import ru.hogwarts.springhogwars.services.AvatarService;
import ru.hogwarts.springhogwars.services.FacultyService;
import ru.hogwarts.springhogwars.services.StudentService;


import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
public class FacultyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyRepository facultyRepository;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private AvatarRepository avatarRepository;

    @SpyBean
    private FacultyService facultyService;

    @SpyBean
    private AvatarService avatarService;

    @SpyBean
    private StudentService studentService;

    @InjectMocks
    private FacultyController facultyController;

    @Test
    public void createFacultyTest() throws Exception{
        JSONObject facultyObject = new JSONObject();
        facultyObject.put("name", "Гриффиндор");
        facultyObject.put("color", "Красный");
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный");

        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);


        mockMvc.perform(MockMvcRequestBuilders.post("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Гриффиндор"))
                .andExpect(jsonPath("$.color").value("Красный"));
    }
    @Test
    public void updateFacultyTest() throws Exception{
        JSONObject facultyObject = new JSONObject();
        facultyObject.put("name", "Слизарин");
        facultyObject.put("color", "Синий");
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный");
        Faculty newFaculty = new Faculty(1L, "Слизарин", "Синий");


        when(facultyRepository.save(any(Faculty.class))).thenReturn(newFaculty);
        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/1")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());



    }
    @Test
    public void removeFacultyTest() throws Exception{
        JSONObject facultyObject = new JSONObject();
        facultyObject.put("name", "Гриффиндор");
        facultyObject.put("color", "Красный");
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный");


        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders.delete("/faculty/1")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Гриффиндор"))
                .andExpect(jsonPath("$.color").value("Красный"));

    }
    @Test
    public void getAllFacultyTest() throws Exception{
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный");
        JSONObject facultyObject = new JSONObject();

        ArrayList<Faculty> facultyArrayList = new ArrayList<>(List.of(faculty));


        when(facultyRepository.findAll()).thenReturn(facultyArrayList);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(facultyArrayList));


    }
    @Test
    public void searchForStudentsByColorTest() throws Exception{ //должно проходить, но возващается пустой список, что может быть не так?
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный");
        JSONObject facultyObject = new JSONObject();

        ArrayList<Faculty> facultyArrayList = new ArrayList<>(List.of(faculty));

        when(facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase("Гриффиндор", "Красный")).thenReturn(facultyArrayList);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/?nameOfColor=Красный")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(facultyArrayList));


    }
    @Test
    public void searchForAStudentByFacultyTest() throws Exception{
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный");
        JSONObject facultyObject = new JSONObject();

        Student student = new Student(1L, "Гарри", 11, faculty);
        ArrayList<Student> studentsArrayList = new ArrayList<>(List.of(student));


        when(studentRepository.findAllByFaculty_Id(1)).thenReturn(studentsArrayList);


        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/1/student")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(studentsArrayList));

    }
}
