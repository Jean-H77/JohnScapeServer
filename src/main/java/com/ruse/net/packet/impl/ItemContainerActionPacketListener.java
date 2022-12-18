package com.ruse.net.packet.impl;

import com.ruse.model.Animation;
import com.ruse.model.Flag;
import com.ruse.model.Item;
import com.ruse.model.Locations.Location;
import com.ruse.model.PlayerRights;
import com.ruse.model.container.impl.Bank;
import com.ruse.model.container.impl.BeastOfBurden;
import com.ruse.model.container.impl.Equipment;
import com.ruse.model.container.impl.PriceChecker;
import com.ruse.model.container.impl.Shop;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.definitions.WeaponAnimations;
import com.ruse.model.definitions.WeaponInterfaces;
import com.ruse.model.input.impl.EnterAmountToBank;
import com.ruse.model.input.impl.EnterAmountToMakeSmithing;
import com.ruse.model.input.impl.EnterAmountToPriceCheck;
import com.ruse.model.input.impl.EnterAmountToRemoveFromBank;
import com.ruse.model.input.impl.EnterAmountToRemoveFromBob;
import com.ruse.model.input.impl.EnterAmountToRemoveFromPriceCheck;
import com.ruse.model.input.impl.EnterAmountToRemoveFromStake;
import com.ruse.model.input.impl.EnterAmountToRemoveFromTrade;
import com.ruse.model.input.impl.EnterAmountToStake;
import com.ruse.model.input.impl.EnterAmountToStore;
import com.ruse.model.input.impl.EnterAmountToTrade;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.util.Misc;
import com.ruse.world.content.BonusManager;
import com.ruse.world.content.ShopManager;
import com.ruse.world.content.tradingpost.newer.TradingPostManager;
import com.ruse.world.content.Trading;
import com.ruse.world.content.combat.CombatFactory;
import com.ruse.world.content.combat.magic.Autocasting;
import com.ruse.world.content.combat.weapon.CombatSpecial;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.minigames.Dueling;
import com.ruse.world.content.minigames.Dueling.DuelRule;
import com.ruse.world.content.skill.crafting.Jewelry;
import com.ruse.world.content.skill.smithing.EquipmentMaking;
import com.ruse.world.content.skill.smithing.SmithingData;
import com.ruse.model.entity.character.player.Player;

public class ItemContainerActionPacketListener implements PacketListener {

	/**
	 * Manages an item's first action.
	 * @param player	The player clicking the item.
	 * @param packet	The packet to read values from.
	 */
	private static void firstAction(Player player, Packet packet) {
		int interfaceId = packet.readShortA();
		int slot = packet.readShortA();
		int id = packet.readUnsignedShort();
		Item item = new Item(id);
		if(player.getRights().isDeveloperOnly() || player.getRights().equals(PlayerRights.OWNER)) {
			player.getPacketSender().sendMessage("secondAction itemContainer. IF: "+interfaceId+" slot: "+slot+", id: "+id);
		}
		switch (interfaceId) {
			case Shop.SHOP_ITEM_CONTAINER_ID:
				if(player.isShopping() && player.getShop() != null) {
					player.getPacketSender().sendMessage(ItemDefinition.forId(id).getName() + ": currently costs "
							+ Misc.formatNumber(ShopManager.getPrice(player.getShop(), id)) + " " + ShopManager.getCurrencyName(player.getShop()) + ".");
				}
				break;
		case Trading.INTERFACE_ID:
			if(player.getTrading().inTrade()) {
				player.getTrading().tradeItem(id, 1, slot);
			} else if(Dueling.checkDuel(player, 1) || Dueling.checkDuel(player, 2)) {
				player.getDueling().stakeItem(id, 1, slot);
			} else if(player.getInterfaceId() == TradingPostManager.SELLING_PAGE_INTERFACE_ID) {
				player.getTradingPostManager().selectUnlistedItem(id,1);
			}
			break;
		case Trading.INTERFACE_REMOVAL_ID:
			if(player.getTrading().inTrade())
				player.getTrading().removeTradedItem(id, 1);
			break;
		case Dueling.INTERFACE_REMOVAL_ID:
			if(Dueling.checkDuel(player, 1) || Dueling.checkDuel(player, 2)) {
				player.getDueling().removeStakedItem(id, 1);
				return;
			}
			break;
		case Equipment.INVENTORY_INTERFACE_ID:
			item = slot < 0 ? null : player.getEquipment().getItems()[slot];
			if(item == null || item.getId() != id)
				return;
			if(player.getLocation() == Location.DUEL_ARENA) {
				if(player.getDueling().selectedDuelRules[DuelRule.LOCK_WEAPON.ordinal()]) {
					if(item.getDefinition().getEquipmentSlot() == Equipment.WEAPON_SLOT || item.getDefinition().isTwoHanded()) {
						player.getPacketSender().sendMessage("Weapons have been locked during this duel!");
						return;
					}
				}
			}
			boolean stackItem = item.getDefinition().isStackable() && player.getInventory().getAmount(item.getId()) > 0;
			int inventorySlot = player.getInventory().getEmptySlot();
			if (inventorySlot != -1) {
				Item itemReplacement = new Item(-1, 0);
				player.getEquipment().setItem(slot, itemReplacement);
				if(!stackItem)
					player.getInventory().setItem(inventorySlot, item);
				else
					player.getInventory().add(item.getId(), item.getAmount());
				BonusManager.update(player);
				if (item.getDefinition().getEquipmentSlot() == Equipment.WEAPON_SLOT) {
					WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
					WeaponAnimations.update(player);
					if(player.getAutocastSpell() != null || player.isAutocast()) {
						Autocasting.resetAutocast(player, true);
						player.getPacketSender().sendMessage("Autocast spell cleared.");
					}
					player.setSpecialActivated(false);
					CombatSpecial.updateBar(player);
					if (player.hasStaffOfLightEffect()) {
						player.setStaffOfLightEffect(-1);
						player.getPacketSender().sendMessage("You feel the spirit of the Staff of Light begin to fade away...");
					}
				}
				player.getEquipment().refreshItems();
				player.getInventory().refreshItems();
				player.getUpdateFlag().flag(Flag.APPEARANCE);
			} else {
				player.getInventory().full();
			}
			break;
			case Bank.INTERFACE_ID:
			if (!player.isBanking() || player.getInterfaceId() != 5292)
				break;
			item = player.getBank(player.getCurrentBankTab()).get(slot);
			if (!item.getAttributes().hasAttributes())
				item = new Item(item.getId(), 1);
			player.getBank(player.getCurrentBankTab()).switchItem(player.getInventory(), item, slot, true, true);
			player.getBank(player.getCurrentBankTab()).open();
			break;
		case Bank.INVENTORY_INTERFACE_ID:
			if (!player.isBanking() || !player.getInventory().contains(item.getId()) || player.getInterfaceId() != 5292)
				return;
			item = player.getInventory().get(slot);
			player.setCurrentBankTab(Bank.getTabToDepositItem(player, item));
			player.getInventory().switchItem(player.getBank(player.getCurrentBankTab()), item, slot, false, true);
			break;
		case BeastOfBurden.INTERFACE_ID:
			if(player.getInterfaceId() == BeastOfBurden.INTERFACE_ID && player.getSummoning().getBeastOfBurden() != null) {
				if(item.getDefinition().isStackable()){
					player.getPacketSender().sendMessage("You cannot store stackable items.");
					return;
				}
				player.getInventory().switchItem(player.getSummoning().getBeastOfBurden(), item, slot, false, true);
			}
			break;
		case PriceChecker.INTERFACE_PC_ID:
			if(player.getInterfaceId() == PriceChecker.INTERFACE_ID && player.getPriceChecker().isOpen()) {
				player.getInventory().switchItem(player.getPriceChecker(), item, slot, false, true);
			}
			break;
		case 4233:
			Jewelry.jewelryMaking(player, "RING", id, 1);
			break;
		case 4239:
			Jewelry.jewelryMaking(player, "NECKLACE", id, 1);
			break;
		case 4245:
			Jewelry.jewelryMaking(player, "AMULET", id, 1);
			break;
		case 18796:
			Jewelry.jewelryMaking(player, "BRACELET", id, 1);
			break;
		case 1119: //smithing interface row 1
		case 1120: // row 2
		case 1121: // row 3
		case 1122: // row 4
		case 1123: // row 5
			int barsRequired = SmithingData.getBarAmount(item);
			Item bar = new Item(player.getSelectedSkillingItem(), barsRequired);
			int x = 1;
			if(x > (player.getInventory().getAmount(bar.getId()) / barsRequired))
				x = (player.getInventory().getAmount(bar.getId()) / barsRequired);
			EquipmentMaking.smithItem(player, new Item(player.getSelectedSkillingItem(), barsRequired), new Item(item.getId(), SmithingData.getItemAmount(item)), x);
			break;
		}

		if(BeastOfBurden.beastOfBurdenSlot(interfaceId) >= 0) {
			if(player.getInterfaceId() == BeastOfBurden.INTERFACE_ID && player.getSummoning().getBeastOfBurden() != null) {
				player.getSummoning().getBeastOfBurden().switchItem(player.getInventory(), item, BeastOfBurden.beastOfBurdenSlot(interfaceId), false, true);
			}
		} else if(PriceChecker.priceCheckerSlot(interfaceId) >= 0) {
			if(player.getPriceChecker().isOpen()) {
				player.getPriceChecker().switchItem(player.getInventory(), new Item(id, 1), PriceChecker.priceCheckerSlot(interfaceId), false, true);
			}
		}
	}

	/**
	 * Manages an item's second action.
	 * @param player	The player clicking the item.
	 * @param packet	The packet to read values from.
	 */
	private static void secondAction(Player player, Packet packet) {
		int interfaceId = packet.readLEShortA();
		int id = packet.readUnsignedShort();
		int slot = packet.readLEShort();
		Item item = new Item(id);

		if(player.getRights().isDeveloperOnly() || player.getRights().equals(PlayerRights.OWNER)) {
			player.getPacketSender().sendMessage("secondAction itemContainer. IF: "+interfaceId+" slot: "+slot+", id: "+id);
		}
		switch (interfaceId) {
			case Shop.SHOP_ITEM_CONTAINER_ID:
				ShopManager.buyItem(player, id,1);
				break;
		case Trading.INTERFACE_ID:
			if(player.getTrading().inTrade()) {
				player.getTrading().tradeItem(id, 5, slot);
			} else if(Dueling.checkDuel(player, 1) || Dueling.checkDuel(player, 2)) {
				player.getDueling().stakeItem(id, 5, slot);
			} else if(player.getInterfaceId() == TradingPostManager.SELLING_PAGE_INTERFACE_ID) {
				player.getTradingPostManager().selectUnlistedItem(id,5);
			}
			break;
		case Trading.INTERFACE_REMOVAL_ID:
			if(player.getTrading().inTrade())
				player.getTrading().removeTradedItem(id, 5);
			break;
		case Dueling.INTERFACE_REMOVAL_ID:
			if(Dueling.checkDuel(player, 1) || Dueling.checkDuel(player, 2)) {
				player.getDueling().removeStakedItem(id, 5);
				return;
			}
			break;
		case Bank.INTERFACE_ID:
			if (!player.isBanking() || item.getId() != id || player.getInterfaceId() != 5292)
				return;
			Item itemObj = player.getBank(player.getCurrentBankTab()).get(slot);
			if (!itemObj.getAttributes().hasAttributes())
				itemObj = new Item(itemObj.getId(), 5);
			player.getBank(player.getCurrentBankTab()).switchItem(player.getInventory(), itemObj, slot, true, true);
			player.getBank(player.getCurrentBankTab()).open();
			break;
		case Bank.INVENTORY_INTERFACE_ID:
			if (!player.isBanking() || item.getId() != id || !player.getInventory().contains(item.getId()) || player.getInterfaceId() != 5292)
				return;
			item = player.getInventory().getFromSlot(slot, 5);
			if (item == null)
				return;
			player.setCurrentBankTab(Bank.getTabToDepositItem(player, item));
			player.getInventory().switchItem(player.getBank(player.getCurrentBankTab()), item, slot, false, true);
			break;
		case BeastOfBurden.INTERFACE_ID:
			if(player.getInterfaceId() == BeastOfBurden.INTERFACE_ID && player.getSummoning().getBeastOfBurden() != null) {
				if(item.getDefinition().isStackable()){
					player.getPacketSender().sendMessage("You cannot store stackable items.");
					return;
				}
				player.getInventory().switchItem(player.getSummoning().getBeastOfBurden(), new Item(id, 5), slot, false, true);
			}
			break;
		case PriceChecker.INTERFACE_PC_ID:
			if(player.getInterfaceId() == PriceChecker.INTERFACE_ID && player.getPriceChecker().isOpen()) {
				player.getInventory().switchItem(player.getPriceChecker(), new Item(id, 5), slot, false, true);
			}
			break;
		case 4233:
			Jewelry.jewelryMaking(player, "RING", id, 5);
			break;
		case 4239:
			Jewelry.jewelryMaking(player, "NECKLACE", id, 5);
			break;
		case 4245:
			Jewelry.jewelryMaking(player, "AMULET", id, 5);
			break;
		case 18796:
			Jewelry.jewelryMaking(player, "BRACELET", id, 5);
			break;
		case 1119: //smithing interface row 1
		case 1120: // row 2
		case 1121: // row 3
		case 1122: // row 4
		case 1123: // row 5
			int barsRequired = SmithingData.getBarAmount(item);
			Item bar = new Item(player.getSelectedSkillingItem(), barsRequired);
			int x = 5;
			if(x > (player.getInventory().getAmount(bar.getId()) / barsRequired))
				x = (player.getInventory().getAmount(bar.getId()) / barsRequired);
			EquipmentMaking.smithItem(player, new Item(player.getSelectedSkillingItem(), barsRequired), new Item(item.getId(), SmithingData.getItemAmount(item)), x);
			break;
		}

		if(BeastOfBurden.beastOfBurdenSlot(interfaceId) >= 0) {
			if(player.getInterfaceId() == BeastOfBurden.INTERFACE_ID && player.getSummoning().getBeastOfBurden() != null) {
				player.getSummoning().getBeastOfBurden().switchItem(player.getInventory(), new Item(id, 5), BeastOfBurden.beastOfBurdenSlot(interfaceId), false, true);
			}
		} else if(PriceChecker.priceCheckerSlot(interfaceId) >= 0) {
			if(player.getPriceChecker().isOpen()) {
				player.getPriceChecker().switchItem(player.getInventory(), new Item(id, 5), PriceChecker.priceCheckerSlot(interfaceId), false, true);
			}
		}
	}

	/**
	 * Manages an item's third action.
	 * @param player	The player clicking the item.
	 * @param packet	The packet to read values from.
	 */
	private static void thirdAction(Player player, Packet packet) {
		int interfaceId = packet.readLEShort();
		int id = packet.readUnsignedShort();
		int slot = packet.readShortA();
		Item item1 = new Item(id);

		if(player.getRights().isDeveloperOnly()) {
			player.getPacketSender().sendMessage("thirdAction itemContainer. IF: "+interfaceId+" slot: "+slot+", id: "+id);
		}
		switch (interfaceId) {
			case Shop.SHOP_ITEM_CONTAINER_ID:
				ShopManager.buyItem(player, id,5);
				break;
		case Equipment.INVENTORY_INTERFACE_ID:
			if(!player.getEquipment().contains(id))
				return;
			switch(id) {
			//case 13738:
			case 13740:
			case 13742:
			//case 13744:
				if(player.isSpiritDebug()) {
					player.getPacketSender().sendMessage("You toggle your Spirit Shield to not display specific messages.");
					player.setSpiritDebug(false);
				} else if (player.isSpiritDebug() == false) {
					player.getPacketSender().sendMessage("You toggle your Spirit Shield to display specific messages.");
					player.setSpiritDebug(true);
				}
			break;
			case 4566:
				player.performAnimation(new Animation(451));
				break;
			case 1704:
				player.getPacketSender().sendMessage("Your amulet has run out of charges.");
				break;
			case 11126:
				player.getPacketSender().sendMessage("Your bracelet has run out of charges.");
				break;
			case 9759:
			case 9760:
				if(player.getCombatBuilder().isAttacking() || player.getCombatBuilder().isBeingAttacked()) {
					player.getPacketSender().sendMessage("You cannot configure this right now.");
					return;
				}
				player.getPacketSender().sendInterfaceRemoval();
				player.setBonecrushEffect(!player.getBonecrushEffect());
				player.getPacketSender().sendMessage("<img=10> You have "+(player.getBonecrushEffect() ? "activated" : "disabled")+" your cape's Bonecrusher effect.");
				break;
			case 18508:
			case 18509:
				if(player.getCombatBuilder().isAttacking() || player.getCombatBuilder().isBeingAttacked()) {
					player.getPacketSender().sendMessage("You cannot configure this right now.");
					return;
				}
				player.getPacketSender().sendInterfaceRemoval();
				DialogueManager.start(player, 101);
				player.setDialogueActionId(60);
				break;
			case 22052:
			case 14019:
			case 14022:
			case 20081:
				if(player.getCombatBuilder().isAttacking() || player.getCombatBuilder().isBeingAttacked()) {
					player.getPacketSender().sendMessage("You cannot configure this right now.");
					return;
				}
				player.getPacketSender().sendInterfaceRemoval();
				DialogueManager.start(player, 202);
				player.setDialogueActionId(202);
			break;
			case 11283:
				if(player.getDfsCharges() > 0) {
					if(player.getCombatBuilder().isAttacking()) {
						CombatFactory.handleDragonFireShield(player, player.getCombatBuilder().getVictim());
					} else {
						player.getPacketSender().sendMessage("You can only use this in combat.");
					}
				} else {
					player.getPacketSender().sendMessage("Your shield doesn't have enough power yet. It has "+player.getDfsCharges()+"/20 dragon-fire charges.");
				}
				break;
			}
			break;
		case Trading.INTERFACE_ID:
			if(player.getTrading().inTrade()) {
				player.getTrading().tradeItem(id, 10, slot);
			} else if(Dueling.checkDuel(player, 1) || Dueling.checkDuel(player, 2)) {
				player.getDueling().stakeItem(id, 10, slot);
			} else if(player.getInterfaceId() == TradingPostManager.SELLING_PAGE_INTERFACE_ID) {
				player.getTradingPostManager().selectUnlistedItem(id,10);
			}
			break;
		case Trading.INTERFACE_REMOVAL_ID:
			if(player.getTrading().inTrade())
				player.getTrading().removeTradedItem(id, 10);
			break;
		case Dueling.INTERFACE_REMOVAL_ID:
			if(Dueling.checkDuel(player, 1) || Dueling.checkDuel(player, 2)) {
				player.getDueling().removeStakedItem(id, 10);
				return;
			}
			break;
		case Bank.INTERFACE_ID:
			if (!player.isBanking() || player.getInterfaceId() != 5292)
				return;
			Item itemObj = player.getBank(player.getCurrentBankTab()).get(slot);
			if (!itemObj.getAttributes().hasAttributes())
				itemObj = new Item(itemObj.getId(), 10);
			player.getBank(player.getCurrentBankTab()).switchItem(player.getInventory(), itemObj, slot, true, true);
			player.getBank(player.getCurrentBankTab()).open();
			break;
		case Bank.INVENTORY_INTERFACE_ID:
			if (!player.isBanking()|| player.getInterfaceId() != 5292)
				return;
			Item item = player.getInventory().getFromSlot(slot, 10);
			if (item == null)
				return;
			player.setCurrentBankTab(Bank.getTabToDepositItem(player, item));
			player.getInventory().switchItem(player.getBank(player.getCurrentBankTab()), item, slot, false, true);
			break;
		case BeastOfBurden.INTERFACE_ID:
			if(player.getInterfaceId() == BeastOfBurden.INTERFACE_ID && player.getSummoning().getBeastOfBurden() != null) {
				Item storeItem = new Item(id, 10);
				if(storeItem.getDefinition().isStackable()){
					player.getPacketSender().sendMessage("You cannot store stackable items.");
					return;
				}
				player.getInventory().switchItem(player.getSummoning().getBeastOfBurden(), storeItem, slot, false, true);
			}
			break;
		case PriceChecker.INTERFACE_PC_ID:
			if(player.getInterfaceId() == PriceChecker.INTERFACE_ID && player.getPriceChecker().isOpen()) {
				player.getInventory().switchItem(player.getPriceChecker(), new Item(id, 10), slot, false, true);
			}
			break;
		case 4233:
			Jewelry.jewelryMaking(player, "RING", id, 10);
			break;
		case 4239:
			Jewelry.jewelryMaking(player, "NECKLACE", id, 10);
			break;
		case 4245:
			Jewelry.jewelryMaking(player, "AMULET", id, 10);
			break;
		case 18796:
			Jewelry.jewelryMaking(player, "BRACELET", id, 10);
			break;
		case 1119: //smithing interface row 1
		case 1120: // row 2
		case 1121: // row 3
		case 1122: // row 4
		case 1123: // row 5
			int barsRequired = SmithingData.getBarAmount(item1);
			Item bar = new Item(player.getSelectedSkillingItem(), barsRequired);
			int x = 10;
			if(x > (player.getInventory().getAmount(bar.getId()) / barsRequired))
				x = (player.getInventory().getAmount(bar.getId()) / barsRequired);
			EquipmentMaking.smithItem(player, new Item(player.getSelectedSkillingItem(), barsRequired), new Item(item1.getId(), SmithingData.getItemAmount(item1)), x);
			break;
		}

		if(BeastOfBurden.beastOfBurdenSlot(interfaceId) >= 0) {
			if(player.getInterfaceId() == BeastOfBurden.INTERFACE_ID && player.getSummoning().getBeastOfBurden() != null) {
				player.getSummoning().getBeastOfBurden().switchItem(player.getInventory(), new Item(id, 10), BeastOfBurden.beastOfBurdenSlot(interfaceId), false, true);
			}
		} else if(PriceChecker.priceCheckerSlot(interfaceId) >= 0) {
			if(player.getPriceChecker().isOpen()) {
				player.getPriceChecker().switchItem(player.getInventory(), new Item(id, 10), PriceChecker.priceCheckerSlot(interfaceId), false, true);
			}
		}
	}

	/**
	 * Manages an item's fourth action.
	 * @param player	The player clicking the item.
	 * @param packet	The packet to read values from.
	 */
	private static void fourthAction(Player player, Packet packet) {
		int slot = packet.readShortA();
		int interfaceId = packet.readShort();
		int id = packet.readUnsignedShort();

		if(player.getRights().isDeveloperOnly()) {
			player.getPacketSender().sendMessage("fourthAction itemContainer. IF: "+interfaceId+" slot: "+slot+", id: "+id);
		}
		switch (interfaceId) {
			case Shop.SHOP_ITEM_CONTAINER_ID:
				ShopManager.buyItem(player, id,10);
				break;
		case 1119: //smithing interface row 1
		case 1120: // row 2
		case 1121: // row 3
		case 1122: // row 4
		case 1123: // row 5
			Item item111 = new Item(id);
			int barsRequired = SmithingData.getBarAmount(item111);
			Item bar = new Item(player.getSelectedSkillingItem(), barsRequired);
			int x = (player.getInventory().getAmount(bar.getId()) / barsRequired);
			EquipmentMaking.smithItem(player, new Item(player.getSelectedSkillingItem(), barsRequired), new Item(item111.getId(), SmithingData.getItemAmount(item111)), x);
			break;
		case Trading.INTERFACE_ID:
			if(player.getTrading().inTrade()) {
				player.getTrading().tradeItem(id, player.getInventory().getAmount(id), slot);
			} else if(Dueling.checkDuel(player, 1) || Dueling.checkDuel(player, 2)) {
				player.getDueling().stakeItem(id, player.getInventory().getAmount(id), slot);
			} else if(player.getInterfaceId() == TradingPostManager.SELLING_PAGE_INTERFACE_ID) {
				player.getTradingPostManager().selectUnlistedItem(id,player.getInventory().getAmount(id));
			}
			break;
		case Trading.INTERFACE_REMOVAL_ID:
			if(player.getTrading().inTrade()) {
				for(Item item: player.getTrading().offeredItems) {
					if(item != null && item.getId() == id) {
						player.getTrading().removeTradedItem(id, item.getAmount());
						if(ItemDefinition.forId(id) != null && ItemDefinition.forId(id).isStackable())
							break;
					}
				}
			}
			break;
		case Dueling.INTERFACE_REMOVAL_ID:
			if(Dueling.checkDuel(player, 1) || Dueling.checkDuel(player, 2)) {
				for(Item item: player.getDueling().stakedItems) {
					if(item != null && item.getId() == id) {
						player.getDueling().removeStakedItem(id, item.getAmount());
						if(ItemDefinition.forId(id) != null && ItemDefinition.forId(id).isStackable())
							break;
					}
				}
			}
			break;
		case Bank.INTERFACE_ID:
			if (!player.isBanking() || player.getBank(Bank.getTabForItem(player, id)).getAmount(id) <= 0 || player.getInterfaceId() != 5292)
				return;
			Item itemObj = player.getBank(player.getCurrentBankTab()).get(slot);
			if (!itemObj.getAttributes().hasAttributes())
				itemObj = new Item(itemObj.getId(), itemObj.getAmount());
			player.getBank(player.getCurrentBankTab()).switchItem(player.getInventory(), itemObj, slot, true, true);
			player.getBank(player.getCurrentBankTab()).open();
			break;
		case Bank.INVENTORY_INTERFACE_ID:
			Item item = player.getInventory().forSlot(slot);
			if (!player.isBanking() || item.getId() != id || !player.getInventory().contains(item.getId()) || player.getInterfaceId() != 5292)
				return;
			player.setCurrentBankTab(Bank.getTabToDepositItem(player, item));
			player.getInventory().switchItem(player.getBank(player.getCurrentBankTab()), item, slot, false, true);
			break;
		case BeastOfBurden.INTERFACE_ID:
			if(player.getInterfaceId() == BeastOfBurden.INTERFACE_ID && player.getSummoning().getBeastOfBurden() != null) {
				Item storeItem = new Item(id, 29);
				if(storeItem.getDefinition().isStackable()){
					player.getPacketSender().sendMessage("You cannot store stackable items.");
					return;
				}
				player.getInventory().switchItem(player.getSummoning().getBeastOfBurden(), storeItem, slot, false, true);
			}
			break;
		case PriceChecker.INTERFACE_PC_ID:
			if(player.getInterfaceId() == PriceChecker.INTERFACE_ID && player.getPriceChecker().isOpen()) {
				player.getInventory().switchItem(player.getPriceChecker(), new Item(id, player.getInventory().getAmount(id)), slot, false, true);
			}
			break;
			case 4233:
				Jewelry.jewelryMaking(player, "RING", id, 28);
				break;
			case 4239:
				Jewelry.jewelryMaking(player, "NECKLACE", id, 28);
				break;
			case 4245:
				Jewelry.jewelryMaking(player, "AMULET", id, 28);
				break;
			case 18796:
				Jewelry.jewelryMaking(player, "BRACELET", id, 28);
				break;
		}

		if(BeastOfBurden.beastOfBurdenSlot(interfaceId) >= 0) {
			if(player.getInterfaceId() == BeastOfBurden.INTERFACE_ID && player.getSummoning().getBeastOfBurden() != null) {
				player.getSummoning().getBeastOfBurden().switchItem(player.getInventory(), new Item(id, 29), BeastOfBurden.beastOfBurdenSlot(interfaceId), false, true);
			}
		} else if(PriceChecker.priceCheckerSlot(interfaceId) >= 0) {
			if(player.getPriceChecker().isOpen()) {
				player.getPriceChecker().switchItem(player.getInventory(), new Item(id, player.getPriceChecker().getAmount(id)), PriceChecker.priceCheckerSlot(interfaceId), false, true);
			}
		}
	}


	/**
	 * Manages an item's fifth action.
	 * @param player	The player clicking the item.
	 * @param packet	The packet to read values from.
	 */
	private static void fifthAction(Player player, Packet packet) {
		int slot = packet.readLEShort();
		int interfaceId = packet.readShortA();
		int id = packet.readUnsignedShort();

		if(player.getRights().isDeveloperOnly()) {
			player.getPacketSender().sendMessage("fifthAction itemContainer. IF: "+interfaceId+" slot: "+slot+", id: "+id);
		}
		switch (interfaceId) {
			case Shop.SHOP_ITEM_CONTAINER_ID:
				System.out.println("Buy X");
				//ShopManager.buyItem(player, itemId,10);
				break;
		case 1119: //smithing interface row 1
		case 1120: // row 2
		case 1121: // row 3
		case 1122: // row 4
		case 1123: // row 5
			System.out.println(player.getInterfaceId()+" is interfaceid");
			if(player.getInterfaceId() == 994) {
				player.setInputHandling(new EnterAmountToMakeSmithing(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to smith?");
			}
			
			/*
			Item item111 = new Item(id);
			int barsRequired = SmithingData.getBarAmount(item111);
			Item bar = new Item(player.getSelectedSkillingItem(), barsRequired);
			int x = (player.getInventory().getAmount(bar.getId()) / barsRequired);
			EquipmentMaking.smithItem(player, new Item(player.getSelectedSkillingItem(), barsRequired), new Item(item111.getId(), SmithingData.getItemAmount(item111)), x);
			*/
			break;
		case Trading.INTERFACE_ID:
			if(player.getTrading().inTrade()) {
				player.setInputHandling(new EnterAmountToTrade(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to trade?");
			} else if(Dueling.checkDuel(player, 1) || Dueling.checkDuel(player, 2)) {
				player.setInputHandling(new EnterAmountToStake(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to stake?");
			} else if(player.getInterfaceId() == TradingPostManager.SELLING_PAGE_INTERFACE_ID) {
				player.getPacketSender().sendEnterAmountPrompt("Enter amount to list:");
				player.setInputHandling(new TradingPostManager.EnterAmountToList(id,slot));
			}
			break;
		case Trading.INTERFACE_REMOVAL_ID:
			if(player.getTrading().inTrade()) {
				player.setInputHandling(new EnterAmountToRemoveFromTrade(id));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to remove?");
			}
			break;
		case Dueling.INTERFACE_REMOVAL_ID:
			if(Dueling.checkDuel(player, 1) || Dueling.checkDuel(player, 2)) {
				player.setInputHandling(new EnterAmountToRemoveFromStake(id));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to remove?");
			}
			break;
		case Bank.INVENTORY_INTERFACE_ID: //BANK X
		case 12:
			/*if(player.isBanking()) {
				player.setInputHandling(new EnterAmountToBank(itemId, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to bank?");
			}*/
			if(interfaceId == 12) {
				player.setInputHandling(new EnterAmountToBank(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to bank?");
			} else {
				Item item = player.getInventory().forSlot(slot);
				if (!player.isBanking() || item.getId() != id || !player.getInventory().contains(item.getId()) || player.getInterfaceId() != 5292)
					return;
				if (!item.getAttributes().hasAttributes())
					item = new Item(item.getId(), item.getAmount() - 1);
				player.setCurrentBankTab(Bank.getTabToDepositItem(player, item));
				player.getInventory().switchItem(player.getBank(player.getCurrentBankTab()), item, slot, false, true);
			}
			break;
		case Bank.INTERFACE_ID:
		case 11:
			/*if(player.isBanking()) {
				player.setInputHandling(new EnterAmountToRemoveFromBank(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to withdraw?");
				// WITHDRAW ALL BUT ONE 
				//player.getBank(player.getCurrentBankTab()).switchItem(player.getInventory(), new Item(itemId, player.getBank(Bank.getTabForItem(player, itemId)).getAmount(itemId) - 1), slot, true, true);
				//player.getBank(player.getCurrentBankTab()).open();
			}*/
			if(player.isBanking()) {
				if(interfaceId == 11) {
					player.setInputHandling(new EnterAmountToRemoveFromBank(id, slot));
					player.getPacketSender().sendEnterAmountPrompt("How many would you like to withdraw?");
				} else {
					Item itemObj = player.getBank(player.getCurrentBankTab()).get(slot);
					if (!itemObj.getAttributes().hasAttributes())
						itemObj = new Item(itemObj.getId(), itemObj.getAmount() - 1);
					player.getBank(player.getCurrentBankTab()).switchItem(player.getInventory(), itemObj, slot, true, true);
					player.getBank(player.getCurrentBankTab()).open();
				}
			}
			break;
		case PriceChecker.INTERFACE_PC_ID:
			if(player.getInterfaceId() == PriceChecker.INTERFACE_ID && player.getPriceChecker().isOpen()) {
				player.setInputHandling(new EnterAmountToPriceCheck(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to pricecheck?");
			}
			break;
		case BeastOfBurden.INTERFACE_ID:
			if(player.getInterfaceId() == BeastOfBurden.INTERFACE_ID && player.getSummoning().getBeastOfBurden() != null) {
				Item storeItem = new Item(id, 10);
				if(storeItem.getDefinition().isStackable()){
					player.getPacketSender().sendMessage("You cannot store stackable items.");
					return;
				}
				player.setInputHandling(new EnterAmountToStore(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to store?");
			}
			break;
		}

		if(BeastOfBurden.beastOfBurdenSlot(interfaceId) >= 0) {
			if(player.getInterfaceId() == BeastOfBurden.INTERFACE_ID && player.getSummoning().getBeastOfBurden() != null) {
				player.setInputHandling(new EnterAmountToRemoveFromBob(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to remove?");
			}
		} else if(PriceChecker.priceCheckerSlot(interfaceId) >= 0) {
			if(player.getPriceChecker().isOpen()) {
				player.setInputHandling(new EnterAmountToRemoveFromPriceCheck(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to remove?");
			}
		}
	}

	private static void sixthAction(Player player, Packet packet) {
		int interfaceId = packet.readShortA();
		int slot = packet.readShortA();
		int id = packet.readUnsignedShort();

		if(player.getRights().isDeveloperOnly()) {
			player.getPacketSender().sendMessage("sixthAction itemContainer. IF: "+interfaceId+" slot: "+slot+", id: "+id);
		}
	}

	@Override
	public void handleMessage(Player player, Packet packet) {
		if (player.getConstitution() <= 0)
			return;
		switch (packet.getOpcode()) {
		case FIRST_ITEM_ACTION_OPCODE:
			firstAction(player, packet);
			break;
		case SECOND_ITEM_ACTION_OPCODE:
			secondAction(player, packet);
			break;
		case THIRD_ITEM_ACTION_OPCODE:
			thirdAction(player, packet);
			break;
		case FOURTH_ITEM_ACTION_OPCODE:
			fourthAction(player, packet);
			break;
		case FIFTH_ITEM_ACTION_OPCODE:
			fifthAction(player, packet);
			break;
		case SIXTH_ITEM_ACTION_OPCODE:
			sixthAction(player, packet);
			break;
		}
	}

	public static final int FIRST_ITEM_ACTION_OPCODE = 145;
	public static final int SECOND_ITEM_ACTION_OPCODE = 117;
	public static final int THIRD_ITEM_ACTION_OPCODE = 43;
	public static final int FOURTH_ITEM_ACTION_OPCODE = 129;
	public static final int FIFTH_ITEM_ACTION_OPCODE = 135;
	public static final int SIXTH_ITEM_ACTION_OPCODE = 138;
}
