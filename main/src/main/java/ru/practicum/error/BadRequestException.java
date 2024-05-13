package ru.practicum.error;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message, String errorObject) {
        super("Incorrectly made request." + message + " " + errorObject);
    }

}