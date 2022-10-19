package com.ruse.scheduler.impl;

import com.ruse.scheduler.BaseJob;
import com.ruse.world.content.attendance.AttendanceManager;
import org.quartz.*;

import java.util.TimeZone;

public class MidnightResetJob extends BaseJob {
    public static final CronTrigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("MidnightReset")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 * * ?").inTimeZone(TimeZone.getTimeZone("UTC")).withMisfireHandlingInstructionFireAndProceed())
            .startNow()
            .build();

    public MidnightResetJob() {
        super(trigger);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        AttendanceManager.nextDay();
    }
}
