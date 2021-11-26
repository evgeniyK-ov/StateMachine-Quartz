package ru.kazachkov.statemachinedemo.models.processors;

import lombok.extern.log4j.Log4j2;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import ru.kazachkov.statemachinedemo.exceptions.ProcessException;
import ru.kazachkov.statemachinedemo.exceptions.RepeatException;
import ru.kazachkov.statemachinedemo.statemachine.actions.ProcessAction;
import ru.kazachkov.statemachinedemo.statemachine.events.ProcessEvent;
import ru.kazachkov.statemachinedemo.statemachine.states.ProcessStates;

@Component
@Log4j2
public class ProcessorExecutor implements Processor<ProcessAction> {


    @Override
    public void process(StateContext<ProcessStates, ProcessEvent> stateContext) throws ProcessException {
        if (Math.random() > 0.8) {
            log.info("Exec process");
        } else {
            log.error("error processs");
            throw new RepeatException("Error executing process");
        }

    }

    @Override
    public Class<ProcessAction> getType() {
        return ProcessAction.class;
    }
}
