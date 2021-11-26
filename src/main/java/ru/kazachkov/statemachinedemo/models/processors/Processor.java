package ru.kazachkov.statemachinedemo.models.processors;

import org.springframework.statemachine.StateContext;
import ru.kazachkov.statemachinedemo.exceptions.ProcessException;
import ru.kazachkov.statemachinedemo.statemachine.events.ProcessEvent;
import ru.kazachkov.statemachinedemo.statemachine.states.ProcessStates;

public interface Processor<T> {

    void process(StateContext<ProcessStates, ProcessEvent> stateContext) throws ProcessException;

    Class<T> getType();
}
