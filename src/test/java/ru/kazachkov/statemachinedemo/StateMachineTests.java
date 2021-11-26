package ru.kazachkov.statemachinedemo;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.test.StateMachineTestPlan;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;
import ru.kazachkov.statemachinedemo.exceptions.ErrorException;
import ru.kazachkov.statemachinedemo.exceptions.RepeatException;
import ru.kazachkov.statemachinedemo.models.processors.Processor;
import ru.kazachkov.statemachinedemo.statemachine.actions.ProcessAction;
import ru.kazachkov.statemachinedemo.statemachine.actions.WaitAction;
import ru.kazachkov.statemachinedemo.statemachine.events.ProcessEvent;
import ru.kazachkov.statemachinedemo.statemachine.states.ProcessStates;
import ru.kazachkov.statemachinedemo.statemachine.variables.ProcessTypes;
import ru.kazachkov.statemachinedemo.statemachine.variables.ProcessingResultStatus;
import ru.kazachkov.statemachinedemo.statemachine.variables.ProcessingStatus;
import ru.kazachkov.statemachinedemo.statemachine.variables.Variable;

import java.util.UUID;


public class StateMachineTests extends AbstractContainerTest {

    @Autowired
    private StateMachineService stateMachineService;

    @MockBean
    Processor<WaitAction> processorWaitResult;
    @MockBean
    Processor<ProcessAction> processorExecuting;

    @Test
    @SneakyThrows
    void stateMachineSuccessTest() {

        Mockito.doNothing().when(processorWaitResult).process(Mockito.any());
        Mockito.doNothing().when(processorExecuting).process(Mockito.any());

        var idMachine = UUID.randomUUID();
        var machine = stateMachineService.acquireStateMachine(idMachine.toString(), false);
        machine.getExtendedState().getVariables().put(Variable.TYPE_PROCESS, ProcessTypes.SINGLE);
        machine.getExtendedState().getVariables().put(Variable.PROCESSING_STATUS, ProcessingStatus.SUCCESS);
        machine.getExtendedState().getVariables().put(Variable.PROCESSINGRESULT_STATUS, ProcessingResultStatus.SUCCESS);
        StateMachineTestPlan<ProcessStates, ProcessEvent> plan =
                StateMachineTestPlanBuilder.<ProcessStates, ProcessEvent>builder()
                        .defaultAwaitTime(10)
                        .stateMachine(machine)
                        .step()
                        .expectStates(ProcessStates.NEW)
                        .expectStateChanged(1)
                        .and()
                        .step()
                        .sendEvent(ProcessEvent.START)
                        .expectState(ProcessStates.SUCCESS)
                        .expectStateChanged(1)
//                        .and()
//                        .step()
//                        .sendEvent(ProcessEvent.SEND_MESSAGE)
//                        .expectState(ProcessStates.SUCCESS)
//                        .expectStateChanged(1)
                        .and()
                        .build();
        plan.test();
        stateMachineService.releaseStateMachine(idMachine.toString(),true);
    }

    @Test
    @SneakyThrows
    void stateMachineErrorTest() {
        Mockito.doThrow(new ErrorException("error wait")).when(processorWaitResult).process(Mockito.any());
        Mockito.doThrow(new ErrorException("error send")).when(processorExecuting).process(Mockito.any());
        var idMachine = UUID.randomUUID();
        var machine = stateMachineService.acquireStateMachine(idMachine.toString(), false);
        machine.getExtendedState().getVariables().put(Variable.TYPE_PROCESS, ProcessTypes.SINGLE);
        machine.getExtendedState().getVariables().put(Variable.PROCESSING_STATUS, ProcessingStatus.ERROR);
        machine.getExtendedState().getVariables().put(Variable.ERROR_MESSAGE, "ERROR");
        StateMachineTestPlan<ProcessStates, ProcessEvent> plan =
                StateMachineTestPlanBuilder.<ProcessStates, ProcessEvent>builder()
                        .defaultAwaitTime(10)
                        .stateMachine(machine)
                        .step()
                        .expectStates(ProcessStates.NEW)
                        .expectStateChanged(1)
                        .and()
                        .step()
                        .sendEvent(ProcessEvent.START)
                        .expectState(ProcessStates.ERROR)
                        .expectStateChanged(1)
//                        .and()
//                        .step()
//                        .sendEvent(ProcessEvent.SEND_MESSAGE)
//                        .expectState(ProcessStates.ERROR)
//                        .expectStateChanged(1)
                        .and()
                        .build();
        plan.test();
        stateMachineService.releaseStateMachine(idMachine.toString(),true);
    }
    @Test
    @SneakyThrows
    void stateMachineRepeatSendTest() {

        Mockito.doThrow(new RepeatException("error wait")).when(processorWaitResult).process(Mockito.any());
        Mockito.doThrow(new RepeatException("error send")).when(processorExecuting).process(Mockito.any());

        var idMachine = UUID.randomUUID();
        var machine = stateMachineService.acquireStateMachine(idMachine.toString(), false);
        machine.getExtendedState().getVariables().put(Variable.TYPE_PROCESS, ProcessTypes.SINGLE);
        machine.getExtendedState().getVariables().put(Variable.PROCESSING_STATUS, ProcessingStatus.REPEAT);
        StateMachineTestPlan<ProcessStates, ProcessEvent> plan =
                StateMachineTestPlanBuilder.<ProcessStates, ProcessEvent>builder()
                        .defaultAwaitTime(10)
                        .stateMachine(machine)
                        .step()
                        .expectStates(ProcessStates.NEW)
                        .expectStateChanged(1)
//                        .and()
//                        .step()
//                        .sendEvent(ProcessEvent.START)
//                        .expectState(ProcessStates.STARTING)
//                        .expectStateChanged(1)
                        .and()
                        .step()
                        .sendEvent(ProcessEvent.START)
                        .expectState(ProcessStates.REPEATING)
                        .expectStateChanged(1)
                        .and()
                        .step()
                        .sendEvent(ProcessEvent.PROCESS)
                        .expectState(ProcessStates.REPEATING)
                        .expectStateChanged(1)
                        .and()
                        .step()
                        .sendEvent(ProcessEvent.CANCEL_MESSAGE)
                        .expectState(ProcessStates.CANCEL)
                        .expectStateChanged(1)
                        .and()
                        .build();
        plan.test();
        stateMachineService.releaseStateMachine(idMachine.toString(),true);
    }

    @Test
    @SneakyThrows
    void stateMachineRepeatWaitTest() {

        Mockito.doThrow(new RepeatException("error wait")).when(processorWaitResult).process(Mockito.any());
        Mockito.doNothing().when(processorExecuting).process(Mockito.any());

        var idMachine = UUID.randomUUID();
        var machine = stateMachineService.acquireStateMachine(idMachine.toString(), false);
        machine.getExtendedState().getVariables().put(Variable.TYPE_PROCESS, ProcessTypes.SINGLE);
        machine.getExtendedState().getVariables().put(Variable.PROCESSING_STATUS, ProcessingStatus.REPEAT);
        StateMachineTestPlan<ProcessStates, ProcessEvent> plan =
                StateMachineTestPlanBuilder.<ProcessStates, ProcessEvent>builder()
                        .defaultAwaitTime(10)
                        .stateMachine(machine)
                        .step()
                        .expectStates(ProcessStates.NEW)
                        .expectStateChanged(1)
//                        .and()
//                        .step()
//                        .sendEvent(ProcessEvent.START)
//                        .expectState(ProcessStates.STARTING)
//                        .expectStateChanged(1)
                        .and()
                        .step()
                        .sendEvent(ProcessEvent.START)
                        .expectState(ProcessStates.REPEATINGWAITINGRESULT)
                        .expectStateChanged(1)
                        .and()
                        .step()
                        .sendEvent(ProcessEvent.WAIT_PROCESS_RESULT)
                        .expectState(ProcessStates.REPEATINGWAITINGRESULT)
                        .expectStateChanged(1)
                        .and()
                        .step()
                        .sendEvent(ProcessEvent.CANCEL_MESSAGE)
                        .expectState(ProcessStates.CANCEL)
                        .expectStateChanged(1)
                        .and()
                        .build();
        plan.test();
        stateMachineService.releaseStateMachine(idMachine.toString(),true);
    }

}
