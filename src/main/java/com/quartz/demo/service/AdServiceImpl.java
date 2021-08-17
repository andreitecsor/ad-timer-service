package com.quartz.demo.service;

import com.quartz.demo.job.AdJob;
import com.quartz.demo.util.TimerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdServiceImpl implements AdService {
    private final SchedulerService schedulerService;

    @Autowired
    public AdServiceImpl(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @Override
    public void runAdJob() {
        final TimerInfo info = new TimerInfo();
        info.setTotalFireCount(4);
        info.setRemainingFireCount(info.getTotalFireCount());
        info.setRepeatIntervalMs(3000);
        info.setInitialOffsetMs(2000);
        info.setCallbackData("A wild ad appeared");
        schedulerService.schedule(AdJob.class, info);
    }
}
