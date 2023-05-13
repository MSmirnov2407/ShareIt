package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @ExceptionHandler({ElementNotFoundException.class, NotAnOwnerException.class, BookerIsOwnerException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND) //404
    public String handleElementNotFound(RuntimeException ex) {
        log.debug(ex.getClass().getSimpleName() + ": " + ex.getMessage());
        return ex.getMessage();
    }

    @ExceptionHandler({ItemNotAvailableException.class, BookingTimeException.class,
            BookingAlreadyApprovedException.class, ValidateCommentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public String handleBadRequest(RuntimeException ex) {
        log.debug(ex.getClass().getSimpleName() + ": " + ex.getMessage());
        return ex.getMessage();
    }

    /*тест из Postman подразумевает ответ в виде ResponseEntity*/
    @ExceptionHandler(StateNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleAllAnother(StateNotAllowedException ex) {
        log.debug("StateNotAllowedException :" + ex.getMessage());
        ErrorResponse response = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); //500
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //500
    public String handleAllAnother(RuntimeException ex) {
        log.debug("RuntimeException :" + ex.getMessage());
        return ex.getMessage();
    }
}
