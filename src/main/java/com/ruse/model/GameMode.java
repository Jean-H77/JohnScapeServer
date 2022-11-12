package com.ruse.model;

public enum GameMode {

	NORMAL(
			new Item[]{
					new Item(1147), //helmet
					new Item(19111), //cape
					new Item(6585), //amulet
					new Item(7142), // weapon
					new Item(6575), //ring
					new Item(1113), //torso
					new Item(1191), //shield
					new Item(1073), //legs
					new Item(7462), //gloves
					new Item(11732), //boots
					new Item(5621,100) //ammo
			},
			"none", new Item(4151,1)),

	ULTIMATE_IRONMAN(null, null, (Item) null),
	IRONMAN(
			new Item[]{
					new Item(1147), //helmet
					new Item(19111), //cape
					new Item(6585), //amulet
					new Item(7142), // weapon
					new Item(6575), //ring
					new Item(1113), //torso
					new Item(1191), //shield
					new Item(1073), //legs
					new Item(7462), //gloves
					new Item(11732), //boots
					new Item(5621,100) //ammo
			},
			"ironman", new Item(4151,1)),
	GROUP_IRONMAN(
			new Item[]{
					new Item(1147), //helmet
					new Item(19111), //cape
					new Item(6585), //amulet
					new Item(7142), // weapon
					new Item(6575), //ring
					new Item(1113), //torso
					new Item(1191), //shield
					new Item(1073), //legs
					new Item(7462), //gloves
					new Item(11732), //boots
					new Item(5621,100) //ammo
			},
			"The group ironman game mode is slightly different when compared to the ironman game mode. This game mode will not be able to trade" +
					" other ironman and group ironman players who are not in the same group as yours. <br><br>Benefits of group ironman over regular ironman is" +
					" that you will gain access to a group shared bank and the ability to trade other group ironmen of the same group.", new Item(4151,1));

	private final transient Item[] inventoryStarter;
	private final transient Item[] equipmentStarter;
	private final transient String description;

	GameMode(Item[] equipmentStarter, String description, Item... inventoryStarter) {
		this.equipmentStarter = equipmentStarter;
		this.description = description;
		this.inventoryStarter = inventoryStarter;
	}

	public Item[] getInventoryStarter() {
		return inventoryStarter;
	}

	public Item[] getEquipmentStarter() {
		return equipmentStarter;
	}

	public String getDescription() {
		return description;
	}
}
