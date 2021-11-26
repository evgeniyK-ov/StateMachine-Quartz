package ru.kazachkov.statemachinedemo.exceptions;

public class RepeatException extends ProcessException {
    public RepeatException(String message) {
        super(message);
    }
}
