package com.quartz.demo.job;

import org.quartz.DailyTimeIntervalTrigger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AdJob implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String info = (String) jobDataMap.get(AdJob.class.getSimpleName());
        LOGGER.info(info + " at " + new Date(System.currentTimeMillis()));
    }
}
