package ru.kazachkov.statemachinedemo.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.kazachkov.statemachinedemo.models.entities.ProcessTask;
import ru.kazachkov.statemachinedemo.models.repository.ProcessTaskRepository;
import ru.kazachkov.statemachinedemo.schedules.SchedulerService;
import ru.kazachkov.statemachinedemo.statemachine.events.ProcessEvent;
import ru.kazachkov.statemachinedemo.statemachine.states.ProcessStates;
import ru.kazachkov.statemachinedemo.statemachine.variables.ProcessTypes;
import ru.kazachkov.statemachinedemo.statemachine.variables.Variable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Data
public class ProcessService {
    private final StateMachineService<ProcessStates, ProcessEvent> stateMachineService;
    private final SchedulerService schedulerService;
    private final ProcessTaskRepository processTaskRepository;


    public static final Map<UUID, SseEmitter> publishes = new ConcurrentHashMap<>();
    public static final ThreadLocal<UUID> machineThreadId = new ThreadLocal<>();

    public UUID createMessageProcess(String processName, UUID parentId) {
        var machineId = UUID.randomUUID();
        var machine = stateMachineService.acquireStateMachine(machineId.toString(), true);
        machine.getExtendedState().getVariables().put(Variable.PROCESS_NAME, processName);
        machine.getExtendedState().getVariables().put(Variable.TYPE_PROCESS, ProcessTypes.SINGLE);
        if (parentId != null) {
            machine.getExtendedState().getVariables().put(Variable.PARENT_PROCESS_ID, parentId);
        }
        nextStepMessageProcess(machineId);
        return machineId;
    }

    public UUID createMultiProcess(String processName, String... subProcessNames) {
        var machineId = UUID.randomUUID();
        var machine = stateMachineService.acquireStateMachine(machineId.toString(), true);
        machine.getExtendedState().getVariables().put(Variable.PROCESS_NAME, processName);
        machine.getExtendedState().getVariables().put(Variable.SUBPROCESS_NAMES, Stream.of(subProcessNames).collect(Collectors.joining(",")));
        machine.getExtendedState().getVariables().put(Variable.TYPE_PROCESS, ProcessTypes.MULTI);

        nextStepMessageProcess(machineId);
        return machineId;
    }

    public void nextStepMessageProcess(UUID machineId) {
        var machine = stateMachineService.acquireStateMachine(machineId.toString(), true);
        var processName = (String) machine.getExtendedState().getVariables().get(Variable.PROCESS_NAME);
        machineThreadId.set(machineId);
        try {
            switch (machine.getState().getId()) {
                case NEW:
                    machine.sendEvent(ProcessEvent.START);
                    schedulerService.createOrUpdate(UUID.fromString(machine.getId()), processName, "* * * ? * *", true);
                    break;
                case STARTING:
                    machine.sendEvent(ProcessEvent.PROCESS);
                    break;
                //SINGLE
                case REPEATING:
                    machine.sendEvent(ProcessEvent.PROCESS);
                    break;
                case REPEATINGWAITINGRESULT:
                    machine.sendEvent(ProcessEvent.WAIT_PROCESS_RESULT);
                    break;

                //MULTI
                case SUBPROCESS_STARTING:
                    machine.sendEvent(ProcessEvent.MONITOR);
                    break;
                case REPEATMONITORING:
                    machine.sendEvent(ProcessEvent.MONITOR);
                    break;
                //GENERAL
                case SUCCESS:
                case CANCEL:
                case ERROR:
                default:
                    schedulerService.createOrUpdate(UUID.fromString(machine.getId()), processName, null, false);
                    processUpdateStatus(machine);
                    break;
            }
        } finally {
            if (machine.hasStateMachineError()) {
                schedulerService.createOrUpdate(UUID.fromString(machine.getId()), processName, null, false);
            }
            processUpdateStatus(machine);
            stateMachineService.releaseStateMachine(machineId.toString(), true);
            machineThreadId.remove();
        }


    }

    private void processUpdateStatus(StateMachine<ProcessStates, ProcessEvent> machine) {
        var processName = (String) machine.getExtendedState().getVariables().get(Variable.PROCESS_NAME);
        var parentId = Optional.ofNullable(machine.getExtendedState().getVariables()
                        .get(Variable.PARENT_PROCESS_ID))
                .map(String::valueOf).map(UUID::fromString).orElse(null);
        var processTask = processTaskRepository.findById(UUID.fromString(machine.getId()))
                .orElseGet(
                        () -> ProcessTask.builder()
                                .processId(UUID.fromString(machine.getId()))
                                .processName(processName)
                                .id(UUID.fromString(machine.getId()))
                                .parentId(parentId)
                                .status(ProcessStates.NEW)
                                .build()
                );

        processTask.setStatus(machine.getState().getId());
        processTaskRepository.save(processTask);
    }

}
