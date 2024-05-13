package ru.practicum.error;

public class ConditionsAreNotMetException extends RuntimeException {

    public ConditionsAreNotMetException(String message, String errorObject) {
        super("Only pending or canceled events can be changed." + message + " " + errorObject);
    }
}