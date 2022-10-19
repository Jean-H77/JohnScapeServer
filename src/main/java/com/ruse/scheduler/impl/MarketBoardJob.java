package com.ruse.scheduler.impl;

import com.ruse.scheduler.BaseJob;
import com.ruse.world.content.trading_post.ShopUtils;
import org.quartz.*;

public class MarketBoardJob extends BaseJob {
    public static final Trigger trigger = TriggerBuilder.newTrigger().withIdentity("MarketBoardJob")
            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(50).repeatForever().withMisfireHandlingInstructionFireNow())
            .build();

    public MarketBoardJob() {
        super(trigger);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        ShopUtils.processQueues();
    }
}
