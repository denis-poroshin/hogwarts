package ru.hogwarts.springhogwars.exceptions;


public class StudentNotFoundException extends NotFoundException{
    public StudentNotFoundException(long id) {
        super(id);
    }
    @Override
    public String getMessage() {
        return "Студент с id = %d не найден".formatted(getId());
    }
}
