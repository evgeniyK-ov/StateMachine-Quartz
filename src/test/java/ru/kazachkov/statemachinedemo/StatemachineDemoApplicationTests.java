package ru.kazachkov.statemachinedemo;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import ru.kazachkov.statemachinedemo.models.processors.Processor;
import ru.kazachkov.statemachinedemo.models.repository.ProcessTaskRepository;
import ru.kazachkov.statemachinedemo.services.ProcessService;
import ru.kazachkov.statemachinedemo.statemachine.actions.ProcessAction;
import ru.kazachkov.statemachinedemo.statemachine.actions.WaitAction;
import ru.kazachkov.statemachinedemo.statemachine.states.ProcessStates;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@SpringBootTest
class StatemachineDemoApplicationTests extends AbstractContainerTest {

    @Autowired
    ProcessService processService;
    @Autowired
    ProcessTaskRepository processTaskRepository;


    @SpyBean
    Processor<WaitAction> processorWaitResult;
    @SpyBean
    Processor<ProcessAction> processorExecuting;

    @Test
    @SneakyThrows
    void jobSingleProcessTest() {
        Mockito.doCallRealMethod().when(processorExecuting).process(Mockito.any());
        Mockito.doCallRealMethod().when(processorWaitResult).process(Mockito.any());
        var listProcess = List.of("TESTMESSAGE_1");
        ArrayList<UUID> processIds = new ArrayList<>();
        listProcess.forEach(el -> processIds.add(processService.createMessageProcess(el, null)));
        Awaitility
                .await().timeout(100, TimeUnit.SECONDS)
                .until(
                        () -> processIds.stream()
                                .filter((el) -> !processTaskRepository.findByIdAndStatusIn(el, ProcessStates.SUCCESS).isPresent())
                                .collect(Collectors.toList()).isEmpty());
        Assertions.assertEquals(1, processTaskRepository.findByIdInAndStatusIn(processIds, ProcessStates.SUCCESS).size());
    }

    @Test
    @SneakyThrows
    void jobSingleProcessErrorTest() {
        Mockito.doThrow(new RuntimeException("error")).when(processorExecuting).process(Mockito.any());

        var listProcess = List.of("TESTMESSAGE_1");
        ArrayList<UUID> processIds = new ArrayList<>();
        listProcess.forEach(el -> processIds.add(processService.createMessageProcess(el, null)));
        Awaitility
                .await().timeout(100, TimeUnit.SECONDS)
                .until(
                        () -> processIds.stream()
                                .filter((el) -> !processTaskRepository.findByIdAndStatusIn(el, ProcessStates.ERROR).isPresent())
                                .collect(Collectors.toList()).isEmpty());
        Assertions.assertEquals(1, processTaskRepository.findByIdInAndStatusIn(processIds, ProcessStates.ERROR).size());
    }

    @Test
    @SneakyThrows
    void jobSingleWaitErrorTest() {
        Mockito.doNothing().when(processorExecuting).process(Mockito.any());
        Mockito.doThrow(new RuntimeException("error")).when(processorWaitResult).process(Mockito.any());

        var listProcess = List.of("TESTMESSAGE_1");
        ArrayList<UUID> processIds = new ArrayList<>();
        listProcess.forEach(el -> processIds.add(processService.createMessageProcess(el, null)));
        Awaitility
                .await().timeout(100, TimeUnit.SECONDS)
                .until(
                        () -> processIds.stream()
                                .filter((el) -> !processTaskRepository.findByIdAndStatusIn(el, ProcessStates.ERROR).isPresent())
                                .collect(Collectors.toList()).isEmpty());
        Assertions.assertEquals(1, processTaskRepository.findByIdInAndStatusIn(processIds, ProcessStates.ERROR).size());
    }

    @Test
    @SneakyThrows
    void jobSingleProcessesTest() {
        Mockito.doCallRealMethod().when(processorExecuting).process(Mockito.any());
        Mockito.doCallRealMethod().when(processorWaitResult).process(Mockito.any());
        var listProcess = List.of("TESTMESSAGE_1", "TESTMESSAGE_2", "TESTMESSAGE_3", "TESTMESSAGE_4", "TESTMESSAGE_5");
        ArrayList<UUID> processIds = new ArrayList<>();
        String processName = "TESTMESSAGE";
        listProcess.forEach(el -> processIds.add(processService.createMessageProcess(el, null)));
        Awaitility
                .await().timeout(100, TimeUnit.SECONDS)
                .until(
                        () -> processIds.stream()
                                .filter((el) -> !processTaskRepository.findByIdAndStatusIn(el, ProcessStates.SUCCESS).isPresent())
                                .collect(Collectors.toList()).isEmpty());
        Assertions.assertEquals(5, processTaskRepository.findByIdInAndStatusIn(processIds, ProcessStates.SUCCESS).size());
    }


    @Test
    @SneakyThrows
    void jobMultiProcessTest() {
        Mockito.doCallRealMethod().when(processorExecuting).process(Mockito.any());
        Mockito.doCallRealMethod().when(processorWaitResult).process(Mockito.any());
        var listInternalProcess = List.of("TESTMESSAGE_1", "TESTMESSAGE_2", "TESTMESSAGE_3", "TESTMESSAGE_4", "TESTMESSAGE_5");
        var processIds = List.of(processService
                .createMultiProcess("MULTI_PROCESS", listInternalProcess.toArray(new String[listInternalProcess.size()])));
        Awaitility
                .await().timeout(100, TimeUnit.SECONDS)
                .until(
                        () -> processIds.stream()
                                .filter((el) -> !processTaskRepository.findByIdAndStatusIn(el, ProcessStates.SUCCESS).isPresent())
                                .collect(Collectors.toList()).isEmpty());
        Assertions.assertEquals(1, processTaskRepository.findByIdInAndStatusIn(processIds, ProcessStates.SUCCESS).size());
        Assertions.assertEquals(processTaskRepository.findByParentIdInAndStatusIn(processIds, ProcessStates.SUCCESS).size(), listInternalProcess.size());
    }

    @Test
    @SneakyThrows
    void jobMultiProcessErrorTest() {
        Mockito.doCallRealMethod().when(processorExecuting).process(Mockito.any());
        Mockito.doCallRealMethod().when(processorWaitResult).process(Mockito.any());
        Mockito.doThrow(new RuntimeException("error")).when(processorExecuting).process(Mockito.any());
        var listInternalProcess = List.of("TESTMESSAGE_1", "TESTMESSAGE_2", "TESTMESSAGE_3", "TESTMESSAGE_4", "TESTMESSAGE_5");
        var processIds = List.of(processService
                .createMultiProcess("MULTI_PROCESS", listInternalProcess.toArray(new String[listInternalProcess.size()])));
        Awaitility
                .await().timeout(100, TimeUnit.SECONDS)
                .until(
                        () -> processIds.stream()
                                .filter((el) -> !processTaskRepository.findByIdAndStatusIn(el, ProcessStates.ERROR).isPresent())
                                .collect(Collectors.toList()).isEmpty());
        Assertions.assertEquals(1, processTaskRepository.findByIdInAndStatusIn(processIds, ProcessStates.ERROR).size());
        Assertions.assertEquals(processTaskRepository.findByParentIdInAndStatusIn(processIds, ProcessStates.ERROR).size(), listInternalProcess.size());
    }


}
