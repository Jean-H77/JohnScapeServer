package com.ruse.scheduler;

import org.quartz.CronExpression;
import org.quartz.Job;
import org.quartz.Trigger;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public abstract class BaseJob implements Job {
    private final Trigger trigger;

    protected BaseJob(Trigger trigger) {
        this.trigger = trigger;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public Date nextFireTime() {
        return this.getTrigger().getNextFireTime();
    }

}
