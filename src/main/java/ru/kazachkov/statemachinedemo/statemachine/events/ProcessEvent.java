package ru.kazachkov.statemachinedemo.statemachine.events;

public enum ProcessEvent {

    START,
    SUB_PROCESS_START,
    MONITOR,
    PROCESS,
    WAIT_PROCESS_RESULT,
    REPEAT_MESSAGE,
    COMPLETE_MESSAGE,
    CANCEL_MESSAGE
}
