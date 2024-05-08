package com.ruse.world.content.skill.thieving;

import com.ruse.model.Animation;
import com.ruse.model.Item;
import com.ruse.model.Skill;
import com.ruse.model.entity.character.player.Player;
import com.ruse.util.Misc;

import java.util.HashMap;
import java.util.Map;

public class Stalls {

	private static final Map<Integer, Stall> stalls = new HashMap<>();
	private static final Map<Integer, Integer> prices = new HashMap<>();
	private static final Animation THIEVING_ANIM = new Animation(881);

	static {
		stalls.put(630, Stall.BAKERY);
		stalls.put(632, Stall.FUR);
		stalls.put(628, Stall.SILVER);
		stalls.put(629, Stall.SILK);
		stalls.put(631, Stall.GEM);

		prices.put(2309, 75);
		prices.put(1891, 75);
		prices.put(1901, 75);
		prices.put(958, 125);
		prices.put(442, 160);
		prices.put(950, 300);
		prices.put(1617, 5000);
		prices.put(1619, 1200);
		prices.put(1621, 600);
		prices.put(1623, 400);
	}

	private enum Stall {
		BAKERY(1, 100, new Item(2309,1), new Item(1891,1), new Item(1901,1)),
		FUR(20, 300, new Item(958,1)),
		SILVER(50, 450, new Item(442,1)),
		SILK(75, 750, new Item(950,1)),
		GEM(90, 1250);

		private final int levelReq;
		private final int expGain;
		private final Item[] rewards;

        Stall(int levelReq, int expGain, Item... rewards) {
            this.levelReq = levelReq;
            this.expGain = expGain;
            this.rewards = rewards;
        }
    }

	public record SellRequest(Map<Integer, Integer> map, int amount) {}

	public static SellRequest sellGoods(Player player) {
		int sum = 0;
		Map<Integer, Integer> deleteMap = new HashMap<>();
		for(Map.Entry<Integer, Integer> entry : prices.entrySet()) {
			int key = entry.getKey();
			int value = entry.getValue();
			int invAmount = player.getInventory().getAmount(key);
			sum += (value * invAmount);
			deleteMap.put(key, invAmount);
		}
		if(sum > 0) {
			return new SellRequest(deleteMap, sum);
		}
		return null;
	}

	public static void stealFromStall(Player player, int stallId) {
		if(player.getInventory().getFreeSlots() < 1) {
			player.getPacketSender().sendMessage("You need some more inventory space to do this.");
			return;
		}

		if (player.getCombatBuilder().isBeingAttacked()) {
			player.getPacketSender().sendMessage("You must wait a few seconds after being out of combat before doing this.");
			return;
		}

		if(!player.getClickDelay().elapsed(2000)) {
			return;
		}

		Stall stall = stalls.get(stallId);

		if(stall == null) {
			return;
		}

		int levelReq = stall.levelReq;
		int expGain = stall.expGain;

		if(player.getSkillManager().getMaxLevel(Skill.THIEVING) < levelReq) {
			player.getPacketSender().sendMessage("You need a Thieving level of at least " + levelReq + " to steal from this stall.");
			return;
		}

		player.performAnimation(THIEVING_ANIM);
		player.getPacketSender().sendInterfaceRemoval();
		player.getSkillManager().addExperience(Skill.THIEVING, expGain);

		if(stall == Stall.GEM) {
			int random = Misc.random(128);
			if(random == 0) {
				player.getInventory().add(1617, 1);
			} else if(random > 0 && random < 4) {
				player.getInventory().add(1619, 1);
			} else if(random > 4 && random < 15) {
				player.getInventory().add(1621, 1);
			} else {
				player.getInventory().add(1623, 1);
			}
		} else {
			player.getInventory().add(Misc.randomElement(stall.rewards));
		}

		player.getClickDelay().reset();
		player.getSkillManager().stopSkilling();
	}
}
