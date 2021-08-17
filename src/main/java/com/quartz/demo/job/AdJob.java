package com.quartz.demo.job;

import com.quartz.demo.service.SchedulerServiceImpl;
import com.quartz.demo.util.TimerInfo;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AdJob implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerServiceImpl.class);

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        TimerInfo info = (TimerInfo) jobDataMap.get(AdJob.class.getSimpleName());
        LOGGER.info(info.getCallbackData() + " - Remaining fire count: '{}'", info.getRemainingFireCount());
    }
}
