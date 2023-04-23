package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(EmailAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT) //409
    public String handleEmailAlreadyExist(EmailAlreadyExistException ex) {
        log.debug("EmailAlreadyExistException :" + ex.getMessage());
        return ex.getMessage();
    }

    @ExceptionHandler(ElementNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) //404
    public String handleElementNotFound(ElementNotFoundException ex) {
        log.debug("ElementNotFountException :" + ex.getMessage());
        return ex.getMessage();
    }

    @ExceptionHandler(NotAnOwnerException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) //404
    public String handleNotAnOwner(NotAnOwnerException ex) {
        log.debug("NotAnOwnerException :" + ex.getMessage());
        return ex.getMessage();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //500
    public String handleAllAnother(RuntimeException ex) {
        log.debug("RuntimeException :" + ex.getMessage());
        return ex.getMessage();
    }
}
