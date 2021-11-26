package ru.kazachkov.statemachinedemo.schedules.jobs;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.quartz.*;
import org.springframework.stereotype.Component;
import ru.kazachkov.statemachinedemo.services.ProcessService;

import java.util.UUID;

@Component
@AllArgsConstructor
@Log4j2
@DisallowConcurrentExecution
public class ProcessJob implements InterruptableJob {
    private final ProcessService processService;


    @Override
    public void interrupt() throws UnableToInterruptJobException {

    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        var id = UUID.fromString(jobExecutionContext.getJobDetail().getKey().getName());
        processService.nextStepMessageProcess(id);
        log.info(id);
    }
}
