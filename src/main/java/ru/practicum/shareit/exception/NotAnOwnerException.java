package ru.practicum.shareit.exception;

public class NotAnOwnerException extends RuntimeException{
    public NotAnOwnerException (String message){
        super(message);
    }
}
