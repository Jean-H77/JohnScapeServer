package com.ruse.scheduler.impl;

import com.ruse.scheduler.BaseJob;
import com.ruse.world.content.attendance.AttendanceManager;
import org.quartz.*;

import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;
@DisallowConcurrentExecution
public class MidnightResetJob extends BaseJob {
    private static final String CRON_EXPRESSION = "0 0 0 * * ?";

    public static final CronTrigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("MidnightReset")
            .withSchedule(CronScheduleBuilder.cronSchedule(CRON_EXPRESSION).inTimeZone(TimeZone.getTimeZone("UTC")).withMisfireHandlingInstructionFireAndProceed())
            .startNow()
            .build();

    public MidnightResetJob() {
        super(trigger);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        AttendanceManager.nextDay();
    }

    @Override
    public Date nextFireTime() {
        try {
            CronExpression cronExpression = new CronExpression(CRON_EXPRESSION);
            cronExpression.setTimeZone(TimeZone.getTimeZone("UTC"));
            return cronExpression.getNextValidTimeAfter(Date.from(Instant.now()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
