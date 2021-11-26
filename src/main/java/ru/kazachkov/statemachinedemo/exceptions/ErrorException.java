package ru.kazachkov.statemachinedemo.exceptions;

public class ErrorException extends ProcessException {
    public ErrorException(String message) {
        super(message);
    }
}