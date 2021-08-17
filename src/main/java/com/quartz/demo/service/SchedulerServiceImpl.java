package com.quartz.demo.service;

import com.quartz.demo.util.TimerInfo;
import com.quartz.demo.util.TimerUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SchedulerServiceImpl implements SchedulerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerServiceImpl.class);
    private final Scheduler scheduler;

    @Autowired
    public SchedulerServiceImpl(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void schedule(final Class jobClass, final TimerInfo info) {
        final JobDetail jobDetail = TimerUtils.buildJobDetail(jobClass, info);
        final Trigger trigger = TimerUtils.buildTrigger(jobClass, info);

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public List<TimerInfo> getAllRunningTimers() {
        try {
            return scheduler.getJobKeys(GroupMatcher.anyGroup())
                    .stream()
                    .map(jobKey -> {
                        try {
                            final JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                            return (TimerInfo) jobDetail.getJobDataMap().get(jobKey.getName());
                        } catch (SchedulerException e) {
                            LOGGER.error(e.getMessage(), e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (SchedulerException e) {
            LOGGER.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public TimerInfo getRunningTimer(String timerId) {
        try {
            final JobDetail jobDetail = scheduler.getJobDetail(new JobKey(timerId));
            if (jobDetail == null) {
                LOGGER.error("Failed to find timer with ID '{}'", timerId);
                return null;
            }
            return (TimerInfo) jobDetail.getJobDataMap().get(timerId);
        } catch (SchedulerException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void updateTimer(String timerId, TimerInfo info) {
        try {
            final JobDetail jobDetail = scheduler.getJobDetail(new JobKey(timerId));
            if (jobDetail == null) {
                LOGGER.error("Failed to find timer with ID '{}'", timerId);
            }
            jobDetail.getJobDataMap().put(timerId, info);
        } catch (SchedulerException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public Boolean delete(String timerId) {
        try {
            return scheduler.deleteJob(new JobKey(timerId));
        } catch (SchedulerException e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    @PostConstruct
    public void init() {
        try {
            scheduler.start();
            scheduler.getListenerManager().addTriggerListener(new SimpleTriggerListener(this));
        } catch (SchedulerException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @PreDestroy
    public void preDestroy() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
