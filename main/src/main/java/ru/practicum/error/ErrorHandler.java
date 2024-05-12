package ru.practicum.error;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.RollbackException;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Collections;

@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadRequestException.class, MethodArgumentNotValidException.class,
            ConversionFailedException.class, ConstraintViolationException.class,
            MissingServletRequestParameterException.class, RollbackException.class,
            ConstraintViolationException.class})
    public ApiError badRequestExceptionResponse(Exception e) {
        return new ApiError(Collections.emptyList(),
                e.getMessage(),
                "For the requested operation the conditions are not met.",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ObjectNotFoundException.class)
    public ApiError objectNotFoundResponse(ObjectNotFoundException e) {
        return new ApiError(Collections.emptyList(),
                e.getMessage(),
                "The required object was not found.",
                HttpStatus.NOT_FOUND,
                LocalDateTime.now());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiError dataIntegrityViolationResponse(DataIntegrityViolationException e) {
        return new ApiError(Collections.emptyList(),
                e.getMessage(),
                "Integrity constraint has been violated",
                HttpStatus.CONFLICT,
                LocalDateTime.now());
    }

//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler
//    public ApiError unknownError(RuntimeException e) {
//        return new ApiError(Collections.emptyList(),
//                e.getMessage(),
//                "UnknownError",
//                HttpStatus.INTERNAL_SERVER_ERROR,
//                LocalDateTime.now());
//    }
}