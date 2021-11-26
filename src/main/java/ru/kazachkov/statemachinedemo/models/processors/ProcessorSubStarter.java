package ru.kazachkov.statemachinedemo.models.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import ru.kazachkov.statemachinedemo.exceptions.ProcessException;
import ru.kazachkov.statemachinedemo.services.ProcessService;
import ru.kazachkov.statemachinedemo.statemachine.actions.SubProcessStartAction;
import ru.kazachkov.statemachinedemo.statemachine.events.ProcessEvent;
import ru.kazachkov.statemachinedemo.statemachine.states.ProcessStates;
import ru.kazachkov.statemachinedemo.statemachine.variables.Variable;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Log4j2
@RequiredArgsConstructor
public class ProcessorSubStarter implements Processor<SubProcessStartAction> {
    private final ApplicationContext context;

    @Override
    public void process(StateContext<ProcessStates, ProcessEvent> stateContext) throws ProcessException {
        var processNames = ((String) stateContext.getStateMachine().getExtendedState().getVariables().get(Variable.SUBPROCESS_NAMES)).split(",");
        var processMessageService = context.getBean(ProcessService.class);
        var parentId = UUID.fromString(stateContext.getStateMachine().getId());
        var processIds = Stream.of(processNames).map(el -> processMessageService.createMessageProcess(el, parentId).toString()).collect(Collectors.joining(","));
        stateContext.getStateMachine().getExtendedState().getVariables().put(Variable.SUBPROCESS_IDS, processIds);
    }


    @Override
    public Class<SubProcessStartAction> getType() {
        return SubProcessStartAction.class;
    }
}
