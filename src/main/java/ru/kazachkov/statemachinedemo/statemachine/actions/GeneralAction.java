package ru.kazachkov.statemachinedemo.statemachine.actions;

import lombok.extern.log4j.Log4j2;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import ru.kazachkov.statemachinedemo.statemachine.events.ProcessEvent;
import ru.kazachkov.statemachinedemo.statemachine.states.ProcessStates;

@Log4j2
public abstract class GeneralAction implements Action<ProcessStates, ProcessEvent> {
    @Override
    public void execute(StateContext<ProcessStates, ProcessEvent> stateContext) {
        exec(stateContext);
    }

    protected abstract void exec(StateContext<ProcessStates, ProcessEvent> stateContext);
}
