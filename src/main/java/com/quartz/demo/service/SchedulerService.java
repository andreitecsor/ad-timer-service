package com.quartz.demo.service;

import com.quartz.demo.util.TimerInfo;

import java.util.List;

public interface SchedulerService {
    void schedule(final Class jobClass, final TimerInfo info);

    List<TimerInfo> getAllRunningTimers();

    TimerInfo getRunningTimer(String timerId);

    void updateTimer(final String timerId, final TimerInfo info);

    Boolean delete(final String timerId);
}
