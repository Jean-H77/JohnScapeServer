package com.ruse.scheduler.impl;

import com.ruse.scheduler.BaseJob;
import org.quartz.*;

public class GlobalBossJob extends BaseJob {
    public static final Trigger trigger = TriggerBuilder.newTrigger().withIdentity("GlobalBossJob")
            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(5).repeatForever().withMisfireHandlingInstructionIgnoreMisfires())
            .build();

    public GlobalBossJob() {
        super(trigger);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

    }

}
