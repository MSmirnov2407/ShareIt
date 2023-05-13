package ru.practicum.shareit.exception;

public class BookerIsOwnerException extends RuntimeException {
    public BookerIsOwnerException(String message) {
        super(message);
    }
}
