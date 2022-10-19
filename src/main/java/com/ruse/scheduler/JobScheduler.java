package com.ruse.scheduler;

import com.ruse.scheduler.impl.GlobalBossJob;
import com.ruse.scheduler.impl.MarketBoardJob;
import com.ruse.scheduler.impl.MidnightResetJob;
import org.quartz.JobBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.reflections.Reflections;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * John77 (Concerned)
 */
public class JobScheduler {
    private static final Logger LOGGER = Logger.getLogger("Scheduler");
    public final static List<BaseJob> JOBS = new ArrayList<>();

    public static void initialize() {
        JOBS.add(new GlobalBossJob());
        JOBS.add(new MarketBoardJob());
        JOBS.add(new MidnightResetJob());

        for(BaseJob job : JOBS) {
            registerNewJob(job);
        }
    }

    public Optional<BaseJob> getJob(BaseJob job) {
        return JOBS.stream()
                .filter(baseJob -> baseJob == job)
                .findFirst();
    }

    public Date getNextFireTime(BaseJob job) {
        return getJob(job).map(baseJob -> baseJob.getTrigger().getNextFireTime()).orElse(null);
    }

    public static void registerNewJob(BaseJob job) {
        try {
            Scheduler sch = StdSchedulerFactory.getDefaultScheduler();
            sch.scheduleJob(JobBuilder.newJob(job.getClass()).build(),job.getTrigger());
            sch.start();
        } catch (SchedulerException e) {
            LOGGER.log(Level.SEVERE, "Error scheduling job of : " + job.getTrigger().getDescription());
            e.printStackTrace();
        }
    }
}
