package com.ruse.util;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.ruse.GameServer;
import com.ruse.net.GameHandler;
import com.ruse.world.World;
import com.ruse.world.content.clan.ClanChatManager;
import com.ruse.model.entity.character.player.Player;
import com.ruse.model.entity.character.player.PlayerHandler;

public class ShutdownHook extends Thread {

	/**
	 * The ShutdownHook logger to print out information.
	 */
	private static final Logger logger = Logger.getLogger(ShutdownHook.class.getName());

	@Override
	public void run() {
		logger.info("The shutdown hook is processing all required actions...");
		World.savePlayers();
		GameServer.setUpdating(true);
		for (Player player : World.getPlayers()) {
			if (player != null) {
				World.deregister(player);
				PlayerHandler.handleLogout(player, false);
			}
		}
		ClanChatManager.save();

		GameHandler.fileIOExecutor.shutdown();
		while (true) {
			try {
				logger.info("Waiting for the file IO executor to terminate...");
				if (GameHandler.fileIOExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		logger.info("The shudown hook actions have been completed, shutting the server down...");
	}
}
