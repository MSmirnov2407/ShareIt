package ru.practicum.shareit.exception;

//исключение для случая дублирования Email
public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException(String message) {
        super(message);
    }
}
