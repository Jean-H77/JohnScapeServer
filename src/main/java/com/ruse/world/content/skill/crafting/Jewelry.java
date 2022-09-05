package com.ruse.world.content.skill.crafting;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Item;
import com.ruse.model.Items;
import com.ruse.model.Skill;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.model.entity.character.player.Player;

public class Jewelry {
	
	public static void stringAmulet(Player player, final int itemUsed, final int usedWith) {
		final int amuletId = (itemUsed == 1759 ? usedWith : itemUsed);
		if (!player.getInventory().contains(1759)) {
			player.getPacketSender().sendMessage("You need a ball of wool in order to string your "+ItemDefinition.forId(amuletId).getName().toLowerCase()+".");
			return;
		}
		if (!player.getInventory().contains(amuletId)) {
			player.getPacketSender().sendMessage("You need an amulet to utilize your ball of wool.");
			return;
		}
		for (final AmuletData a : AmuletData.values()) {
			if (amuletId == a.getAmuletId()) {
				player.getInventory().delete(1759, 1);
				player.getInventory().delete(amuletId, 1);
				player.getInventory().add(a.getProduct(), 1);
				player.getSkillManager().addExperience(Skill.CRAFTING, 4);
			}
		}
	}
	
	public static void jewelryMaking(Player player, final String type, final int itemId, final int amount) {
		switch (type) {
		case "RING":
			for (int i = 0; i < JewelryData.RINGS.item.length; i++) {
				if (itemId == JewelryData.RINGS.item[i][1]) {
					mouldJewelry(player, JewelryData.RINGS.item[i][0], itemId, amount, JewelryData.RINGS.item[i][2], JewelryData.RINGS.item[i][3]);
				}
			}
			break;
		case "NECKLACE":
			for (int i = 0; i < JewelryData.NECKLACE.item.length; i++) {
				if (itemId == JewelryData.NECKLACE.item[i][1]) {
					mouldJewelry(player, JewelryData.NECKLACE.item[i][0], itemId, amount, JewelryData.NECKLACE.item[i][2], JewelryData.NECKLACE.item[i][3]);
				}
			}
			break;
		case "AMULET":
			for (int i = 0; i < JewelryData.AMULETS.item.length; i++) {
				if (itemId == JewelryData.AMULETS.item[i][1]) {
					mouldJewelry(player, JewelryData.AMULETS.item[i][0], itemId, amount, JewelryData.AMULETS.item[i][2], JewelryData.AMULETS.item[i][3]);
				}
			}
			break;
		case "BRACELET":
			for (int i = 0; i < JewelryData.BRACELETS.item.length; i++) {
				if (itemId == JewelryData.BRACELETS.item[i][1]) {
					mouldJewelry(player, JewelryData.BRACELETS.item[i][0], itemId, amount, JewelryData.BRACELETS.item[i][2], JewelryData.BRACELETS.item[i][3]);
				}
			}
			break;
		}
	}
	
	private static void mouldJewelry(Player player, final int required, final int itemId, final int amount, final int level, final int xp) {
		if (player.getInterfaceId() != 18875) {
			return;
		}
		player.getPacketSender().sendInterfaceRemoval();

		if (player.getSkillManager().getCurrentLevel(Skill.CRAFTING) < level) {
			player.getPacketSender()
					.sendMessage("You need a Crafting level of at least " +level+ " to mould this.");
			return;
		}
		if (!player.getInventory().contains(2357)) {
			player.getPacketSender().sendMessage("You need a gold bar to mould this item.");
			return;
		}
		if (!player.getInventory().contains(required)) {
			player.getPacketSender().sendMessage("You need "+Misc.anOrA(ItemDefinition.forId(required).getName())
			+" "+ItemDefinition.forId(required).getName().toLowerCase() +" to mould this item.");
			return;
		}
		player.setCurrentTask(new Task(2, player, true) {
			int toMake = amount;

			@Override
			public void execute() {
				if (!player.getInventory().contains(2357) || !player.getInventory().contains(required)) {
					player.getPacketSender().sendMessage("You have run out of materials.");
					stop();
					return;
				}
				if (required != 2357) {
					player.getInventory().delete(2357, 1);
				}
				player.getInventory().delete(required, 1).add(itemId, 1);
				player.getSkillManager().addExperience(Skill.CRAFTING, (int) xp);
				player.performAnimation(new Animation(896));
				toMake--;
				if (toMake <= 0) {
					stop();
					return;
				}
			}
		});
		TaskManager.submit(player.getCurrentTask());
	}

	public enum JewelryData {
		
		RINGS(new int[][] {{2357, 1635, 5, 15},
				{1607, 1637, 20, 40},
				{1605, 1639, 27, 55},
				{1603, 1641, 34, 70},
				{1601, 1643, 43, 85},
				{1615, 1645, 55, 100},
				{6573, 6575, 67, 115},
				{Items.ZENYTE, Items.ZENYTE_RING, 89, 150}

		}),
		NECKLACE(new int[][] {{2357, 1654, 6, 20},
				{1607, 1656, 22, 55},
				{1605, 1658, 29, 60},
				{1603, 1660, 40, 75},
				{1601, 1662, 56, 90},
				{1615, 1664, 72, 105},
				{6573, 6577, 82, 120},
				{Items.ZENYTE, Items.ZENYTE_NECKLACE, 92, 165}

		}),
		AMULETS(new int[][] {
				{2357, 1673, 8, 30},
				{1607, 1675, 24, 65},
				{1605, 1677, 31, 70},
				{1603, 1679, 50, 85},
				{1601, 1681, 70, 100},
				{1615, 1683, 80, 150},
				{6573, 6579, 90, 165},
				{Items.ZENYTE, Items.ZENYTE_AMULET_U, 92, 165}
		}),
		BRACELETS(new int[][] {
				{Items.GOLD_BAR, Items.GOLD_BRACELET, 7, 25},
				{Items.SAPPHIRE, Items.SAPPHIRE_BRACELET, 23, 60},
				{Items.EMERALD, Items.EMERALD_BRACELET, 30, 65},
				{Items.RUBY, Items.RUBY_BRACELET, 42, 80},
				{Items.DIAMOND, Items.DIAMOND_BRACELET, 58, 95},
				{Items.DRAGONSTONE, Items.DRAGON_BRACELET, 74, 110},
				{Items.ONYX, Items.ONYX_BRACELET, 84, 125},
				{Items.ZENYTE, Items.ZENYTE_BRACELET, 95, 180},
		}),

		;
		
		public int[][] item;

		JewelryData(final int[][] item) {
			this.item = item;
		}
	}

	public enum AmuletData {
		GOLD(1673, 1692),
		SAPPHIRE(1675, 1694),
		EMERALD(1677, 1696),
		RUBY(1679, 1698),
		DIAMOND(1681, 1700),
		DRAGONSTONE(1683, 1702),
		ONYX(6579, 6581),
		ZENYTE(Items.ZENYTE_AMULET_U, Items.ZENYTE_AMULET)
		;
			
		private int amuletId, product;

		AmuletData(final int amuletId, final int product) {
			this.amuletId = amuletId;
			this.product = product;
		}

		public int getAmuletId() {
			return amuletId;
		}

		public int getProduct() {
			return product;
		}
	}

	private static Item[] getItems(int[][] jewelry) {
		Item[] items = new Item[jewelry.length];
		for (int index = 0; index < jewelry.length; index++) {
			items[index] = new Item(jewelry[index][1], 1);
		}
		return items;
	}

	public static void jewelryInterface(Player player) {
		if (player.getInventory().contains(1592)) {
			player.getPacketSender().sendItemContainer(getItems(JewelryData.RINGS.item), 4233);
			player.getPacketSender().sendInterfaceModel(4229, -1, 0);
		} else {
			player.getPacketSender().sendItemContainer(new Item[8], 4233);
			player.getPacketSender().sendInterfaceModel(4229, 1592, 120);
		}

		if (player.getInventory().contains(1597)) {
			player.getPacketSender().sendItemContainer(getItems(JewelryData.NECKLACE.item), 4239);
			player.getPacketSender().sendInterfaceModel(4235, -1, 0);
		} else {
			player.getPacketSender().sendItemContainer(new Item[8], 4239);
			player.getPacketSender().sendInterfaceModel(4235, 1597, 120);
		}

		if (player.getInventory().contains(1595)) {
			player.getPacketSender().sendItemContainer(getItems(JewelryData.AMULETS.item), 4245);
			player.getPacketSender().sendInterfaceModel(4241, -1, 0);
		} else {
			player.getPacketSender().sendItemContainer(new Item[8], 4245);
			player.getPacketSender().sendInterfaceModel(4241, 1595, 120);
		}

		if (player.getInventory().contains(Items.BRACELET_MOULD)) {
			player.getPacketSender().sendItemContainer(getItems(JewelryData.BRACELETS.item), 18796);
			player.getPacketSender().sendInterfaceModel(18790, -1, 0);
		} else {
			player.getPacketSender().sendItemContainer(new Item[8], 18796);
			player.getPacketSender().sendInterfaceModel(18790, Items.BRACELET_MOULD, 120);
		}

		player.getPacketSender().sendInterface(18875);
	}
}
