package com.ruse.util;

import com.ruse.GameSettings;
import com.ruse.world.World;
import com.ruse.world.content.trading_post.ShopUtils;

public class PlayerSavingTimer {
	
	public static long massSaveTimer = System.currentTimeMillis();

	public static void massSaving() {
		if (System.currentTimeMillis() - massSaveTimer > GameSettings.charcterSavingInterval) {
			World.savePlayers();
			massSaveTimer = System.currentTimeMillis();
			ShopUtils.saveAll();
		}
	}
	
}
