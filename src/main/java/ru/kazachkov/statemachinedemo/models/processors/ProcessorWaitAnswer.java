package ru.kazachkov.statemachinedemo.models.processors;

import lombok.extern.log4j.Log4j2;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import ru.kazachkov.statemachinedemo.exceptions.ProcessException;
import ru.kazachkov.statemachinedemo.exceptions.RepeatException;
import ru.kazachkov.statemachinedemo.statemachine.actions.WaitAction;
import ru.kazachkov.statemachinedemo.statemachine.events.ProcessEvent;
import ru.kazachkov.statemachinedemo.statemachine.states.ProcessStates;

@Component
@Log4j2
public class ProcessorWaitAnswer implements Processor<WaitAction> {


    @Override
    public void process(StateContext<ProcessStates, ProcessEvent> stateContext) throws ProcessException {
        if (Math.random() > 0.8) {
            log.info("Answer received");
        } else {
            log.error("error answer received");
            throw new RepeatException("error answer received");
        }

    }

    @Override
    public Class<WaitAction> getType() {
        return WaitAction.class;
    }
}

