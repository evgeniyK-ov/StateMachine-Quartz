package ru.kazachkov.statemachinedemo.statemachine.listeners;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.statemachine.event.OnStateChangedEvent;
import org.springframework.statemachine.event.StateMachineEvent;
import org.springframework.statemachine.state.State;
import ru.kazachkov.statemachinedemo.services.ProcessService;
import ru.kazachkov.statemachinedemo.statemachine.events.ProcessEvent;
import ru.kazachkov.statemachinedemo.statemachine.states.ProcessStates;

import java.util.Optional;

@Log4j2
public class StateMachineApplicationEventListener implements ApplicationListener<StateMachineEvent> {
    @Override
    public void onApplicationEvent(StateMachineEvent event) {
        if (event instanceof OnStateChangedEvent) {
            log.info("Event: {} SourceState: {} TargetState: {}",
                    event.getClass().getSimpleName(),
                    Optional.ofNullable((State<ProcessStates, ProcessEvent>) ((OnStateChangedEvent) event).getSourceState()).map(State::getId).orElse(ProcessStates.UNDEFINED),
                    Optional.ofNullable((State<ProcessStates, ProcessEvent>) ((OnStateChangedEvent) event).getTargetState()).map(State::getId).orElse(ProcessStates.UNDEFINED));

            if (ProcessService.machineThreadId.get() != null && ProcessService.publishes.containsKey(ProcessService.machineThreadId.get())) {
                try {
                    ProcessService.publishes.get(ProcessService.machineThreadId.get()).send(
                            Optional.ofNullable((State<ProcessStates, ProcessEvent>) ((OnStateChangedEvent) event).getTargetState()).map(State::getId).orElse(ProcessStates.UNDEFINED)
                    );
                } catch (Exception x) {
                    ProcessService.publishes.remove(ProcessService.machineThreadId.get());
                }
                if (((OnStateChangedEvent) event).getTargetState().getId() == ProcessStates.SUCCESS) {
                    try {
                        ProcessService.publishes.get(ProcessService.machineThreadId.get()).complete();
                    } catch (Exception x) {
                        ProcessService.publishes.remove(ProcessService.machineThreadId.get());
                    }

                }

            }

        }
    }

}
