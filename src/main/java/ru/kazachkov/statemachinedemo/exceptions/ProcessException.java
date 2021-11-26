package ru.kazachkov.statemachinedemo.exceptions;

public class ProcessException extends Exception {
    public ProcessException(String message) {
        super(message);
    }

    public ProcessException(Exception x) {
        super(x);
    }
}
