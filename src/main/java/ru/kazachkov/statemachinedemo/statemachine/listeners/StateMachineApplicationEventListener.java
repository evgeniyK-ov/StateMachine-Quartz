package ru.kazachkov.statemachinedemo.statemachine.listeners;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.statemachine.event.OnStateChangedEvent;
import org.springframework.statemachine.event.StateMachineEvent;
import org.springframework.statemachine.state.State;

import java.util.Optional;

@Log4j2
public class StateMachineApplicationEventListener implements ApplicationListener<StateMachineEvent> {
    @Override
    public void onApplicationEvent(StateMachineEvent event) {
        if (event instanceof OnStateChangedEvent) {
            log.info("Event: {} SourceState: {} TargetState: {}",
                    event.getClass().getSimpleName(),
                    Optional.ofNullable(((OnStateChangedEvent) event).getSourceState()).map(State::getId).orElse(null),
                    Optional.ofNullable(((OnStateChangedEvent) event).getTargetState()).map(State::getId).orElse(null));

        }
    }

}
