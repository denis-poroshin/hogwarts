package ru.hogwarts.springhogwars.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.hogwarts.springhogwars.exceptions.AvatarProcessingException;
import ru.hogwarts.springhogwars.exceptions.NotCorrectValueException;
import ru.hogwarts.springhogwars.exceptions.NotFoundException;

@RestControllerAdvice
public class HogwartsExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }
    @ExceptionHandler(AvatarProcessingException.class)
    public ResponseEntity<String> handleAvatarProcessingException(){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Не удалось прочитать аватарку из запроса или файла");
    }
    @ExceptionHandler(NotCorrectValueException.class)
    public ResponseEntity<String> handleNotCorrectValueException(){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Значение введено некорректно");
    }
}