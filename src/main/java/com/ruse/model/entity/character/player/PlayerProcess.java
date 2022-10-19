package com.ruse.model.entity.character.player;

import com.ruse.model.RegionInstance.RegionInstanceType;
import com.ruse.model.entity.character.GroundItemManager;
import com.ruse.world.content.skill.construction.House;

public class PlayerProcess {

	/*
	 * The player (owner) of this instance
	 */
	private Player player;

	/*
	 * The loyalty tick, once this reaches 6, the player
	 * will be given loyalty points.
	 * 6 equals 3.6 seconds.
	 */
	private int loyaltyTick;

	/*
	 * The timer tick, once this reaches 2, the player's
	 * total play time will be updated.
	 * 2 equals 1.2 seconds.
	 */
	private int timerTick;

	/*
	 * Makes sure ground items are spawned on height change
	 */
	private int previousHeight;

	public PlayerProcess(Player player) {
		this.player = player;
		this.previousHeight = player.getPosition().getZ();
	}

	public void sequence() {
		player.getInventory().processRefreshItems();
		player.getCombatBuilder().process();

		if(previousHeight != player.getPosition().getZ()) {
			GroundItemManager.handleRegionChange(player);
			previousHeight = player.getPosition().getZ();
		}
		timerTick++;

	}
}
