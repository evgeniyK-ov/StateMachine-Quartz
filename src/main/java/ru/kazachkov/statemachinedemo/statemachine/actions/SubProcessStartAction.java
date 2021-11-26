package ru.kazachkov.statemachinedemo.statemachine.actions;

import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import ru.kazachkov.statemachinedemo.models.processors.Processor;
import ru.kazachkov.statemachinedemo.statemachine.events.ProcessEvent;
import ru.kazachkov.statemachinedemo.statemachine.states.ProcessStates;

@RequiredArgsConstructor
@Component
public class SubProcessStartAction extends GeneralAction {
    private final Processor<SubProcessStartAction> processor;

    @Override
    public void exec(StateContext<ProcessStates, ProcessEvent> stateContext) {
        try {
            processor.process(stateContext);
        } catch (Exception x) {
            stateContext.getStateMachine().setStateMachineError(x);
        }


    }
}
