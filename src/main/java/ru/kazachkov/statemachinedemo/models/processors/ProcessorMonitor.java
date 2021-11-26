package ru.kazachkov.statemachinedemo.models.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import ru.kazachkov.statemachinedemo.exceptions.ProcessException;
import ru.kazachkov.statemachinedemo.exceptions.RepeatException;
import ru.kazachkov.statemachinedemo.models.repository.ProcessTaskRepository;
import ru.kazachkov.statemachinedemo.statemachine.actions.MonitorAction;
import ru.kazachkov.statemachinedemo.statemachine.events.ProcessEvent;
import ru.kazachkov.statemachinedemo.statemachine.states.ProcessStates;
import ru.kazachkov.statemachinedemo.statemachine.variables.Variable;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Log4j2
@RequiredArgsConstructor
public class ProcessorMonitor implements Processor<MonitorAction> {
    private final ProcessTaskRepository processTaskRepository;

    @Override
    public void process(StateContext<ProcessStates, ProcessEvent> stateContext) throws ProcessException {
        var processIds = ((String) stateContext.getStateMachine().getExtendedState().getVariables().get(Variable.SUBPROCESS_IDS)).split(",");
        var listProc = Stream.of(processIds).map(UUID::fromString).collect(Collectors.toList());

        if (processTaskRepository.findByIdInAndStatusIn(listProc, ProcessStates.SUCCESS).size() == listProc.size()) {
            log.info("The all processes was completed");
        } else if (!processTaskRepository.findByIdInAndStatusIn(listProc, ProcessStates.ERROR, ProcessStates.CANCEL).isEmpty()) {
            throw new ProcessException("Found error status in the child processes");
        } else {
            throw new RepeatException("need to repeat again for monitoring");
        }
    }

    @Override
    public Class<MonitorAction> getType() {
        return MonitorAction.class;
    }
}
