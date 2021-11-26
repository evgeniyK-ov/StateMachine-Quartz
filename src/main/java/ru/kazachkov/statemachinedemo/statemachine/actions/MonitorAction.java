package ru.kazachkov.statemachinedemo.statemachine.actions;

import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import ru.kazachkov.statemachinedemo.exceptions.RepeatException;
import ru.kazachkov.statemachinedemo.models.processors.Processor;
import ru.kazachkov.statemachinedemo.statemachine.events.ProcessEvent;
import ru.kazachkov.statemachinedemo.statemachine.states.ProcessStates;
import ru.kazachkov.statemachinedemo.statemachine.variables.MonitoringStatus;
import ru.kazachkov.statemachinedemo.statemachine.variables.Variable;

@RequiredArgsConstructor
@Component
public class MonitorAction extends GeneralAction {

    private final Processor<MonitorAction> processor;

    @Override
    public void exec(StateContext<ProcessStates, ProcessEvent> stateContext) {
        try {
            processor.process(stateContext);
            stateContext.getStateMachine().getExtendedState().getVariables().put(Variable.MONITORING_STATUS, MonitoringStatus.SUCCESS);
        } catch (RepeatException x) {
            stateContext.getStateMachine().getExtendedState().getVariables().put(Variable.MONITORING_STATUS, MonitoringStatus.REPEAT);
        } catch (Exception x) {
            stateContext.getStateMachine().getExtendedState().getVariables().put(Variable.MONITORING_STATUS, MonitoringStatus.ERROR);
            stateContext.getStateMachine().setStateMachineError(x);
        }
    }
}