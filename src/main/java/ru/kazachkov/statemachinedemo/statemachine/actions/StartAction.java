package ru.kazachkov.statemachinedemo.statemachine.actions;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import ru.kazachkov.statemachinedemo.statemachine.events.ProcessEvent;
import ru.kazachkov.statemachinedemo.statemachine.states.ProcessStates;
import ru.kazachkov.statemachinedemo.statemachine.variables.Variable;

@Log4j2
@Component
@RequiredArgsConstructor
public class StartAction extends GeneralAction {
    @Override
    @SneakyThrows
    public void exec(StateContext<ProcessStates, ProcessEvent> stateContext) {
        var machineId=stateContext.getStateMachine().getId();
        var processName=(String)stateContext.getStateMachine().getExtendedState().getVariables().get(Variable.PROCESS_NAME);
    }
}
