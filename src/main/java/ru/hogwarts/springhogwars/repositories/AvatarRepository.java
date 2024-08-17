package ru.hogwarts.springhogwars.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.springhogwars.models.Avatar;

import java.util.Optional;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {

    Optional<Avatar> findByStudent_Id(long studentId);

}

