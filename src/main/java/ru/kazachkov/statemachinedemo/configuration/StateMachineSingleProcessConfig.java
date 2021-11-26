package ru.kazachkov.statemachinedemo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;
import ru.kazachkov.statemachinedemo.statemachine.actions.*;
import ru.kazachkov.statemachinedemo.statemachine.events.ProcessEvent;
import ru.kazachkov.statemachinedemo.statemachine.listeners.StateMachineApplicationEventListener;
import ru.kazachkov.statemachinedemo.statemachine.states.ProcessStates;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class StateMachineSingleProcessConfig extends EnumStateMachineConfigurerAdapter<ProcessStates, ProcessEvent> {

    @Autowired
    private StartAction startAction;
    @Autowired
    private ProcessAction processAction;
    @Autowired
    private SuccessAction successAction;
    @Autowired
    private RepeatAction repeatAction;
    @Autowired
    private RepeatAction repeatWaitingAction;
    @Autowired
    private CancelAction cancelAction;
    @Autowired
    private ErrorAction errorAction;
    @Autowired
    private WaitAction waitAction;

    @Autowired
    private SubProcessStartAction subProcessStartAction;
    @Autowired
    private MonitorAction monitorAction;


    @Autowired
    private StateMachineRuntimePersister<ProcessStates, ProcessEvent, String> stateMachineRuntimePersister;

    @Autowired
    private Guard<ProcessStates, ProcessEvent> guardProcessError;
    @Autowired
    private Guard<ProcessStates, ProcessEvent> guardProcessRepeat;
    @Autowired
    private Guard<ProcessStates, ProcessEvent> guardProcessSuccess;

    @Autowired
    private Guard<ProcessStates, ProcessEvent> guardWaitingResultRepeat;
    @Autowired
    private Guard<ProcessStates, ProcessEvent> guardWaitingResultSuccess;
    @Autowired
    private Guard<ProcessStates, ProcessEvent> guardMonitoringRepeat;
    @Autowired
    private Guard<ProcessStates, ProcessEvent> guardMonitoringSuccess;
    @Autowired
    private Guard<ProcessStates, ProcessEvent> guardSingleProcess;
    @Autowired
    private Guard<ProcessStates, ProcessEvent> guardMultiProcess;


    @Override
    public void configure(StateMachineConfigurationConfigurer<ProcessStates, ProcessEvent> config)
            throws Exception {
        config
                .withPersistence()
                .runtimePersister(stateMachineRuntimePersister);
    }

    @Override
    public void configure(StateMachineStateConfigurer<ProcessStates, ProcessEvent> states)
            throws Exception {
        states
                .withStates()
                .initial(ProcessStates.NEW)
                .choice(ProcessStates.STARTING)
                .choice(ProcessStates.PROCESSING)
                .choice(ProcessStates.WAITINGRESULT)
                .choice(ProcessStates.MONITORING)
                .end(ProcessStates.SUCCESS)
                .end(ProcessStates.ERROR)
                .end(ProcessStates.CANCEL)
                .states(EnumSet.allOf(ProcessStates.class));
    }


    @Override
    public void configure(final StateMachineTransitionConfigurer<ProcessStates, ProcessEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(ProcessStates.NEW)
                .target(ProcessStates.STARTING)
                .event(ProcessEvent.START)
                .action(startAction, errorAction)

                .and()
                .withChoice()
                .source(ProcessStates.STARTING)
                .first(ProcessStates.PROCESSING, guardSingleProcess, processAction)
                .then(ProcessStates.SUBPROCESS_STARTING, guardMultiProcess, subProcessStartAction)
                .last(ProcessStates.ERROR, errorAction)

                //SINGLE process
//                .and()
//                .withExternal()
//                .source(ProcessStates.STARTING)
//                .target(ProcessStates.SENDING)
//                .guard(guardSingleProcess)
//                .event(ProcessEvent.SEND_MESSAGE)
//                .action(sendAction, errorAction)

                .and()
                .withChoice()
                .source(ProcessStates.PROCESSING)
                .first(ProcessStates.REPEATING, guardProcessRepeat, repeatAction)
                .then(ProcessStates.WAITINGRESULT, guardProcessSuccess, waitAction)
                .last(ProcessStates.ERROR, errorAction)

                .and()
                .withChoice()
                .source(ProcessStates.WAITINGRESULT)
                .first(ProcessStates.REPEATINGWAITINGRESULT, guardWaitingResultRepeat, repeatWaitingAction)
                .then(ProcessStates.SUCCESS, guardWaitingResultSuccess, successAction)
                .last(ProcessStates.ERROR, errorAction)

                .and()
                .withExternal()
                .source(ProcessStates.REPEATINGWAITINGRESULT)
                .target(ProcessStates.WAITINGRESULT)
                .event(ProcessEvent.WAIT_PROCESS_RESULT)
                .action(waitAction, errorAction)


                .and()
                .withExternal()
                .source(ProcessStates.REPEATING)
                .target(ProcessStates.PROCESSING)
                .event(ProcessEvent.PROCESS)
                .action(processAction, errorAction)



                .and()
                .withExternal()
                .source(ProcessStates.NEW)
                .target(ProcessStates.CANCEL)
                .event(ProcessEvent.CANCEL_MESSAGE)
                .action(cancelAction, errorAction)
                .and()
                .withExternal()
                .source(ProcessStates.REPEATING)
                .target(ProcessStates.CANCEL)
                .event(ProcessEvent.CANCEL_MESSAGE)
                .action(cancelAction, errorAction)
                .and()
                .withExternal()
                .source(ProcessStates.REPEATINGWAITINGRESULT)
                .target(ProcessStates.CANCEL)
                .event(ProcessEvent.CANCEL_MESSAGE)
                .action(cancelAction, errorAction)
                .and()
                .withExternal()
                .source(ProcessStates.PROCESSING)
                .target(ProcessStates.CANCEL)
                .event(ProcessEvent.CANCEL_MESSAGE)
                .action(cancelAction, errorAction)

                //MULTI process
//                .and()
//                .withExternal()
//                .source(ProcessStates.STARTING)
//                .target(ProcessStates.SUBPROCESS_STARTING)
//                .guard(guardMultiProcess)
//                .event(ProcessEvent.SUB_PROCESS_START)
//                .action(subProcessStartAction, errorAction)

                .and()
                .withExternal()
                .source(ProcessStates.SUBPROCESS_STARTING)
                .target(ProcessStates.MONITORING)
                .event(ProcessEvent.MONITOR)
                .action(monitorAction, errorAction)

                .and()
                .withChoice()
                .source(ProcessStates.MONITORING)
                .first(ProcessStates.REPEATMONITORING, guardMonitoringRepeat, monitorAction)
                .then(ProcessStates.SUCCESS, guardMonitoringSuccess, successAction)
                .last(ProcessStates.ERROR, errorAction)

                .and()
                .withExternal()
                .source(ProcessStates.REPEATMONITORING)
                .target(ProcessStates.MONITORING)
                .event(ProcessEvent.MONITOR)
                .action(monitorAction, errorAction);


    }


    @Bean
    public StateMachineRuntimePersister<ProcessStates, ProcessEvent, String> stateMachineRuntimePersister(
            JpaStateMachineRepository jpaStateMachineRepository) {
        return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
    }

    @Bean
    public StateMachineService<ProcessStates, ProcessEvent> stateMachineService(
            StateMachineFactory<ProcessStates, ProcessEvent> stateMachineFactory,
            StateMachineRuntimePersister<ProcessStates, ProcessEvent, String> stateMachineRuntimePersister) {
        return new DefaultStateMachineService<>(stateMachineFactory, stateMachineRuntimePersister);
    }


    @Bean
    public StateMachineApplicationEventListener contextListener() {
        return new StateMachineApplicationEventListener();
    }

}
