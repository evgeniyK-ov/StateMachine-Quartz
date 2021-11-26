package ru.kazachkov.statemachinedemo.statemachine.guards;

import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;
import ru.kazachkov.statemachinedemo.statemachine.events.ProcessEvent;
import ru.kazachkov.statemachinedemo.statemachine.states.ProcessStates;
import ru.kazachkov.statemachinedemo.statemachine.variables.MonitoringStatus;
import ru.kazachkov.statemachinedemo.statemachine.variables.Variable;

@Component
public class GuardMonitoringRepeat implements Guard<ProcessStates, ProcessEvent> {
    @Override
    public boolean evaluate(StateContext<ProcessStates, ProcessEvent> stateContext) {

        if (stateContext.getExtendedState().getVariables().containsKey(Variable.MONITORING_STATUS)) {
            return stateContext.getExtendedState().getVariables().get(Variable.MONITORING_STATUS).equals(MonitoringStatus.REPEAT);
        } else {
            return false;
        }
    }
}
