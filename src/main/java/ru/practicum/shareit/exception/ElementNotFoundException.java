package ru.practicum.shareit.exception;

public class ElementNotFoundException extends RuntimeException{
    public ElementNotFoundException(String message){
        super(message);
    }
}
