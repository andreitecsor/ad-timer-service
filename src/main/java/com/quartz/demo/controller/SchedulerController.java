package com.quartz.demo.controller;

import com.quartz.demo.service.SchedulerService;
import com.quartz.demo.util.TimerInfo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/scheduler")
public class SchedulerController {
    private final SchedulerService schedulerService;

    public SchedulerController(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @GetMapping
    public List<TimerInfo> getAllRunningTimers() {
        return schedulerService.getAllRunningTimers();
    }

    /**
     * @param timerId is the job's class simple name(e.g. AdJob)
     */
    @GetMapping("/{timerId}")
    public TimerInfo getRunningTimer(@PathVariable String timerId) {
        return schedulerService.getRunningTimer(timerId);
    }

    /**
     * @param timerId is the job's class simple name(e.g. AdJob)
     */
    @DeleteMapping("/{timerId}")
    public Boolean deleteTimer(@PathVariable String timerId) {
        return schedulerService.delete(timerId);
    }
}
