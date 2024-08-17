package ru.hogwarts.springhogwars.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.springhogwars.models.Student;

import java.util.Collection;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Collection<Student> findAllByAge(int age);
    Collection<Student> findByAgeBetween(int minAge, int magAge);
    Collection<Student>findAllByFaculty_Id(long id);
    // можно с SQL запросом
//    @Query("SELECT * FROM student WHERE faculty_id = :facultyId")
//    Collection<Student>findAllByFacultyId(@Param("facultyId") long facultyId);
    @Query(value = "SELECT COUNT(*) FROM student", nativeQuery = true)
    Integer getNumberOfStudents();
    @Query(value = "SELECT AVG(age) FROM student", nativeQuery = true)
    Double getMiddleAged();
    @Query(value = "SELECT * FROM (SELECT * FROM student ORDER BY id DESC LIMIT 5) t ORDER BY id;", nativeQuery = true)
    Collection<Student> getNewFiveStudents();
}

