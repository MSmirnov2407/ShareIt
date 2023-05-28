package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.exception.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ErrorHandlerTest {

    ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleEmailAlreadyExistTest() {
        String exceptionMessage = errorHandler.handleEmailAlreadyExist(new EmailAlreadyExistException("message"));
        assertThat(exceptionMessage, equalTo("message"));
    }

    @Test
    void handleElementNotFoundTest() {
        String exceptionMessage = errorHandler.handleElementNotFound(new ElementNotFoundException("message"));
        assertThat(exceptionMessage, equalTo("message"));

        exceptionMessage = errorHandler.handleElementNotFound(new NotAnOwnerException("message"));
        assertThat(exceptionMessage, equalTo("message"));

        exceptionMessage = errorHandler.handleElementNotFound(new BookerIsOwnerException("message"));
        assertThat(exceptionMessage, equalTo("message"));
    }

    @Test
    void handleBadRequestTest() {
        String exceptionMessage = errorHandler.handleBadRequest(new ItemNotAvailableException("message"));
        assertThat(exceptionMessage, equalTo("message"));

        exceptionMessage = errorHandler.handleBadRequest(new BookingTimeException("message"));
        assertThat(exceptionMessage, equalTo("message"));

        exceptionMessage = errorHandler.handleBadRequest(new BookingAlreadyApprovedException("message"));
        assertThat(exceptionMessage, equalTo("message"));

        exceptionMessage = errorHandler.handleBadRequest(new ValidateCommentException("message"));
        assertThat(exceptionMessage, equalTo("message"));

        exceptionMessage = errorHandler.handleBadRequest(new PaginationParametersException("message"));
        assertThat(exceptionMessage, equalTo("message"));
    }

    @Test
    void handleStateNotAllowedTest() {
        ResponseEntity<ErrorResponse> responseEntity = errorHandler.handleStateNotAllowed(new StateNotAllowedException("message"));
        assertThat(responseEntity.getBody().getError(), equalTo("message"));
        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    void handleAllAnotherTest() {
        String exceptionMessage = errorHandler.handleAllAnother(new RuntimeException("message"));
        assertThat(exceptionMessage, equalTo("message"));
    }
}
