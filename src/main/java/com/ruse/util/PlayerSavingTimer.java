package com.ruse.util;

import com.ruse.GameSettings;
import com.ruse.world.World;

import static com.ruse.world.World.fileIOExecutor;

public class PlayerSavingTimer {
	
	public static long massSaveTimer = System.currentTimeMillis();

	public static void massSaving() {
		if (System.currentTimeMillis() - massSaveTimer > GameSettings.charcterSavingInterval) {
			fileIOExecutor.submit(World::savePlayers);
			World.save();
			massSaveTimer = System.currentTimeMillis();
		}
	}
}
