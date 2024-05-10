package ru.practicum.error;

public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String message, String reason) {
        super("The required object was not found." + message + " " + reason);
    }
}