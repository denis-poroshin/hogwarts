package ru.hogwarts.springhogwars.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.springhogwars.models.Faculty;

import java.util.Collection;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {


    Collection<Faculty> findByNameIgnoreCaseOrColorIgnoreCase(String name, String color);







}