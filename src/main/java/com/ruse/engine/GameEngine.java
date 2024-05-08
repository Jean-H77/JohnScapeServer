package com.ruse.engine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ruse.engine.task.TaskManager;
import com.ruse.util.messageSpammerTimer;
import com.ruse.util.PlayerSavingTimer;
import com.ruse.world.World;
//import com.ruse.world.content.tradingpost.ShopUtils;
import com.ruse.world.content.clan.ClanChatManager;
import com.ruse.model.entity.character.GlobalItemSpawner;

/**
 * 
 * @author lare96
 * @author Gabriel Hannason
 */
public final class GameEngine implements Runnable {
	private final ScheduledExecutorService logicService = GameEngine.createLogicService();

	@Override
	public void run() {
		try {
			TaskManager.sequence();
			World.sequence();
			PlayerSavingTimer.massSaving();
			//messageSpammerTimer.massMessageHandler();
			//GlobalItemSpawner.startup();
		} catch (Throwable e) {
			e.printStackTrace();
			World.savePlayers();
			ClanChatManager.save();
	//		ShopUtils.saveAll();
		}
	}

	public void submit(Runnable t) {
		try {
			logicService.execute(t);
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}

	public static ScheduledExecutorService createLogicService() {
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
		executor.setRejectedExecutionHandler(new CallerRunsPolicy());
		executor.setThreadFactory(new ThreadFactoryBuilder().setNameFormat("LogicServiceThread").build());
		executor.setKeepAliveTime(45, TimeUnit.SECONDS);
		executor.allowCoreThreadTimeOut(true);
		return Executors.unconfigurableScheduledExecutorService(executor);
	}
}
