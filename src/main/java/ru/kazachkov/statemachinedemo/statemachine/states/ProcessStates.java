package ru.kazachkov.statemachinedemo.statemachine.states;

public enum ProcessStates {

    NEW,
    STARTING,
    SUBPROCESS_STARTING,
    MONITORING,
    REPEATMONITORING,
    PROCESSING,
    REPEATING,
    WAITINGRESULT,
    REPEATINGWAITINGRESULT,
    CANCEL,
    ERROR,
    SUCCESS,
    UNDEFINED
}
