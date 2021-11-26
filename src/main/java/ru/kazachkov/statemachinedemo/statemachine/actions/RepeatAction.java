package ru.kazachkov.statemachinedemo.statemachine.actions;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import ru.kazachkov.statemachinedemo.statemachine.events.ProcessEvent;
import ru.kazachkov.statemachinedemo.statemachine.states.ProcessStates;
import ru.kazachkov.statemachinedemo.statemachine.variables.Variable;

@Log4j2
@Component
@RequiredArgsConstructor
public class RepeatAction extends GeneralAction {
    @Override
    public void exec(StateContext<ProcessStates, ProcessEvent> stateContext) {
        stateContext.getStateMachine().getExtendedState().getVariables().remove(Variable.PROCESSING_STATUS);

    }
}
