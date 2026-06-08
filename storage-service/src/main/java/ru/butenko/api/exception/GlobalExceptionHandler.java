package ru.butenko.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.butenko.api.dto.ErrorResponse;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AssemblyOrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(AssemblyOrderNotFoundException exception) {
        return notFound(exception.getMessage());
    }

    @ExceptionHandler(InventoryEntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleStorageNotFound(InventoryEntityNotFoundException exception) {
        return notFound(exception.getMessage());
    }

    private ErrorResponse notFound(String message) {
        return new ErrorResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                message
        );
    }
}
