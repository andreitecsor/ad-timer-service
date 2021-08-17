package com.quartz.demo.service;

import com.quartz.demo.util.TimerInfo;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

public class SimpleTriggerListener implements TriggerListener {
    private final SchedulerService schedulerService;

    public SimpleTriggerListener(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @Override
    public String getName() {
        return SimpleTriggerListener.class.getSimpleName();
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        final String timerId = trigger.getKey().getName();
        final JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        final TimerInfo info = (TimerInfo) jobDataMap.get(timerId);

        if (!info.isRunForever()) {
            if (info.getRemainingFireCount() == 0) {
                return;
            }
            info.setRemainingFireCount(info.getRemainingFireCount() - 1);
        }
        schedulerService.updateTimer(timerId, info);
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {

    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {

    }
}
