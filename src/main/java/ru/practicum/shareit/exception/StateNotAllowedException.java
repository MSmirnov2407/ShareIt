package ru.practicum.shareit.exception;

public class StateNotAllowedException extends RuntimeException {
    public StateNotAllowedException(String message) {
        super(message);
    }
}
