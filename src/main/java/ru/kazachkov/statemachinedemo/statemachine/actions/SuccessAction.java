package ru.kazachkov.statemachinedemo.statemachine.actions;

import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import ru.kazachkov.statemachinedemo.schedules.SchedulerService;
import ru.kazachkov.statemachinedemo.statemachine.events.ProcessEvent;
import ru.kazachkov.statemachinedemo.statemachine.states.ProcessStates;

@Component
@RequiredArgsConstructor
public class SuccessAction  extends GeneralAction {
    private final SchedulerService schedulerService;
    @Override
    public void exec(StateContext<ProcessStates, ProcessEvent> stateContext) {
    }
}
