package com.quartz.demo.config;

import com.quartz.demo.job.AdJob;
import com.quartz.demo.util.TimerInfo;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.time.LocalDateTime;
import java.util.Date;

@Configuration
@EnableAutoConfiguration
public class SchedulerConfig {

    private final Logger LOGGER = LoggerFactory.getLogger(SchedulerConfig.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        LOGGER.debug("Configuring Job factory");
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public JobDetail jobDetail() {
        final JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(AdJob.class.getSimpleName(), "Job working -> Showing ad");
        return JobBuilder.newJob(AdJob.class)
                .withIdentity(AdJob.class.getSimpleName())
                .setJobData(jobDataMap)
                .storeDurably(true)
                .build();
    }

    @Bean
    public Trigger trigger(JobDetail jobDetail, TimerInfo timerInfo) {
        SimpleScheduleBuilder builder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMilliseconds(timerInfo.getRepeatIntervalMs());

        if (timerInfo.isRunForever()) {
            builder = builder.repeatForever();
        } else {
            builder = builder.withRepeatCount(timerInfo.getTotalFireCount() - 1);
        }
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(AdJob.class.getSimpleName())
                .withSchedule(builder)
                .startAt(new Date(System.currentTimeMillis() + timerInfo.getInitialOffsetMs()))
                .build();
    }

    @Bean
    public SchedulerFactoryBean scheduler(Trigger trigger, JobDetail job) {

        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setConfigLocation(new ClassPathResource("application.properties"));

        LOGGER.debug("Setting the Scheduler up");
        schedulerFactory.setJobFactory(springBeanJobFactory());
        schedulerFactory.setJobDetails(job);
        schedulerFactory.setTriggers(trigger);

        return schedulerFactory;
    }

    @Bean
    public TimerInfo timerInfo() {
        final TimerInfo info = new TimerInfo();
        info.setTotalFireCount(4);
        info.setRemainingFireCount(info.getTotalFireCount());
        info.setRepeatIntervalMs(3000);
        info.setInitialOffsetMs(2000);
        return info;
    }

}
