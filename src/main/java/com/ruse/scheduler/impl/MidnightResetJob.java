package com.ruse.scheduler.impl;

import com.ruse.scheduler.BaseJob;
import com.ruse.world.content.attendance.AttendanceManager;
import org.quartz.*;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.TimeZone;
@DisallowConcurrentExecution
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
        System.out.println("Fired");
        AttendanceManager.nextDay();
    }


    @Override
    public Date nextFireTime() {
        try {
            return new CronExpression("0 0 0 * * ?").getNextValidTimeAfter(Date.from(Instant.now(Clock.systemUTC())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
