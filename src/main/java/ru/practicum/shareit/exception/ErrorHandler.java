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

    @ExceptionHandler(BookerIsOwnerException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) //404
    public String handleBookerIsOwner(BookerIsOwnerException ex) {
        log.debug("BookerIsOwnerException :" + ex.getMessage());
        return ex.getMessage();
    }

    @ExceptionHandler(ItemNotAvailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public String handleNotAvailable(ItemNotAvailableException ex) {
        log.debug("ItemNotAvailableException :" + ex.getMessage());
        return ex.getMessage();
    }

    @ExceptionHandler(BookingTimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public String handleBookingTimeEx(BookingTimeException ex) {
        log.debug("BookingTimeException :" + ex.getMessage());
        return ex.getMessage();
    }

    @ExceptionHandler(BookingAlreadyApprovedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public String handleBookingTimeEx(BookingAlreadyApprovedException ex) {
        log.debug("BookingAlreadyApprovedException :" + ex.getMessage());
        return ex.getMessage();
    }

    @ExceptionHandler(ValidateCommentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public String handleNotAvailable(ValidateCommentException ex) {
        log.debug("ValidateCommentException :" + ex.getMessage());
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
