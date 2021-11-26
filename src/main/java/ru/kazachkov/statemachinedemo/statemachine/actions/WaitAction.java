package ru.kazachkov.statemachinedemo.statemachine.actions;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import ru.kazachkov.statemachinedemo.exceptions.RepeatException;
import ru.kazachkov.statemachinedemo.models.processors.Processor;
import ru.kazachkov.statemachinedemo.statemachine.events.ProcessEvent;
import ru.kazachkov.statemachinedemo.statemachine.states.ProcessStates;
import ru.kazachkov.statemachinedemo.statemachine.variables.ProcessingResultStatus;
import ru.kazachkov.statemachinedemo.statemachine.variables.Variable;

@Log4j2
@Component
@AllArgsConstructor
public class WaitAction extends GeneralAction {

    private final Processor<WaitAction> processor;

    @Override
    public void exec(StateContext<ProcessStates, ProcessEvent> stateContext) {
        try {
            processor.process(stateContext);
            stateContext.getStateMachine().getExtendedState().getVariables().put(Variable.PROCESSINGRESULT_STATUS, ProcessingResultStatus.SUCCESS);
        } catch (RepeatException x) {
            stateContext.getStateMachine().getExtendedState().getVariables().put(Variable.PROCESSINGRESULT_STATUS, ProcessingResultStatus.REPEAT);
        } catch (Exception x) {
            stateContext.getStateMachine().getExtendedState().getVariables().put(Variable.PROCESSINGRESULT_STATUS, ProcessingResultStatus.ERROR);
            stateContext.getStateMachine().setStateMachineError(x);
        }

    }
}

