package ru.kazachkov.statemachinedemo.schedules;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.stereotype.Service;
import ru.kazachkov.statemachinedemo.schedules.jobs.ProcessJob;

import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
@Log4j2
public class SchedulerService {

    private final Scheduler scheduler;

    @SneakyThrows
    private void addScheduleCronJob(UUID jobId, String simpleLoadingName, String interval, boolean isEnabled) {

        try {
            JobDetail job = JobBuilder
                    .newJob(ProcessJob.class)
                    .withIdentity(jobId.toString())
                    .withDescription(simpleLoadingName)
                    .storeDurably()
                    .build();

            scheduler.addJob(job, false);

            if (isEnabled) {
                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(jobId.toString())
                        .withSchedule(CronScheduleBuilder
                                .cronSchedule(interval))
                        .forJob(jobId.toString())
                        .build();
                scheduler.scheduleJob(trigger);
            }

        } catch (SchedulerException e) {
            log.error(e);

        }
    }

    @SneakyThrows
    public void interrupt(UUID id) {
        scheduler.interrupt(JobKey.jobKey(id.toString()));
    }

    @SneakyThrows
    public void removeJob(UUID id) {
        var jobKey = JobKey.jobKey(id.toString());
        scheduler.deleteJob(jobKey);

    }

    @SneakyThrows
    private void rescheduleCronJob(UUID jobId, String interval,
                                   boolean isEnabled) {
        try {

            var triggerKey = TriggerKey.triggerKey(jobId.toString());
            if (isEnabled) {
                if (scheduler.checkExists(triggerKey)) {
                    CronTriggerImpl trigger = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
                    trigger.setCronExpression(interval);
                    scheduler.rescheduleJob(triggerKey, trigger);
                } else {
                    Trigger trigger = TriggerBuilder.newTrigger()
                            .withIdentity(jobId.toString())
                            .withSchedule(CronScheduleBuilder
                                    .cronSchedule(interval))
                            .forJob(jobId.toString())
                            .build();
                    scheduler.scheduleJob(trigger);
                }
            } else {
                if (scheduler.checkExists(triggerKey)) {
                    scheduler.unscheduleJob(triggerKey);
                }
            }

        } catch (SchedulerException e) {
            log.error(e);
        }
    }

    @SneakyThrows
    public UUID createOrUpdate(UUID jobId, String simpleLoadingName, String interval, boolean isEnabled) {

        JobDetail jobDetail = null;
        if (jobId != null) {
            jobDetail = scheduler.getJobDetail(JobKey.jobKey(jobId.toString()));
        } else {
            jobId = UUID.randomUUID();
        }

        if (jobDetail == null) {
            addScheduleCronJob(jobId, simpleLoadingName, interval, isEnabled);
        } else {
            rescheduleCronJob(jobId, interval, isEnabled);
        }

        return jobId;
    }


    @SneakyThrows
    public UUID createJob(UUID jobId, String simpleLoadingName) {

        JobDetail job = JobBuilder
                .newJob(ProcessJob.class)
                .withIdentity(jobId.toString())
                .withDescription(simpleLoadingName)
                .storeDurably(true)
                .build();
        scheduler.addJob(job, true);

        return jobId;
    }

    @SneakyThrows
    public void upDateJob(String id, Map<String, String> properties) {
        var jobOld = scheduler.getJobDetail(JobKey.jobKey(id));

        var jobDataMap = new JobDataMap();

        if (properties != null) {
            jobDataMap.putAll(properties);
        }


        JobDetail job = JobBuilder
                .newJob(jobOld.getJobClass())
                .withIdentity(id)
                .withDescription(jobOld.getDescription())
                .usingJobData(jobDataMap)
                .storeDurably(true)
                .build();
        scheduler.addJob(job, true);
    }

    @SneakyThrows
    public Object getJob(String id, String key) {
        var jobOld = scheduler.getJobDetail(JobKey.jobKey(id));
        return jobOld.getJobDataMap().get(key);
    }

}
