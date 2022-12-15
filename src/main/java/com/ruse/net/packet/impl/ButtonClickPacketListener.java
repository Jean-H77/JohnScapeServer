package com.ruse.net.packet.impl;

import com.ruse.GameSettings;
import com.ruse.model.Item;
import com.ruse.model.Locations.Location;
import com.ruse.model.Position;
import com.ruse.model.container.impl.Bank;
import com.ruse.model.container.impl.Bank.BankSearchAttributes;
import com.ruse.model.definitions.WeaponInterfaces.WeaponInterface;
import com.ruse.model.input.impl.AchievementSearch;
import com.ruse.model.input.impl.EnterClanChatToJoin;
import com.ruse.model.input.impl.EnterSyntaxToBankSearchFor;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.*;
import com.ruse.world.content.Sounds.Sound;
import com.ruse.world.content.clan.ClanChat;
import com.ruse.world.content.clan.ClanChatManager;
import com.ruse.world.content.combat.magic.Autocasting;
import com.ruse.world.content.combat.magic.MagicSpells;
import com.ruse.world.content.combat.prayer.CurseHandler;
import com.ruse.world.content.combat.prayer.PrayerHandler;
import com.ruse.world.content.combat.weapon.CombatSpecial;
import com.ruse.world.content.combat.weapon.FightType;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.dialogue.DialogueOptions;
import com.ruse.world.content.minigames.Dueling;
import com.ruse.world.content.minigames.PestControl;
import com.ruse.world.content.skill.ChatboxInterfaceSkillAction;
import com.ruse.world.content.skill.construction.Construction;
import com.ruse.world.content.skill.crafting.LeatherMaking;
import com.ruse.world.content.skill.crafting.Tanning;
import com.ruse.world.content.skill.dungeoneering.Dungeoneering;
import com.ruse.world.content.skill.dungeoneering.DungeoneeringParty;
import com.ruse.world.content.skill.dungeoneering.ItemBinding;
import com.ruse.world.content.skill.fletching.Fletching;
import com.ruse.world.content.skill.herblore.ingredientsBook;
import com.ruse.world.content.skill.smithing.SmithingData;
import com.ruse.world.content.skill.summoning.PouchMaking;
import com.ruse.world.content.skill.summoning.SummoningTab;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.content.transportation.TeleportType;
import com.ruse.model.entity.character.player.Player;
import com.ruse.model.PlayerRights;
/**
 * This packet listener manages a button that the player has clicked upon.
 * 
 * @author Gabriel Hannason
 */

public class ButtonClickPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {

		int bankid = 0;
		int id = packet.readShort();

		if (player.getRights() == PlayerRights.OWNER || player.getRights().isDeveloperOnly()) {
			player.getPacketSender().sendMessage("Clicked button: " + id);
		}

		if(id >= -24475 && id <= -24445) {
			if(player.getDropViewer().handleButtonClick(id))
				return;
		}

		if(checkHandlers(player, id))
			return;

		if(player.getProcessing().clickRecipe(id))
			return;

		switch(id) {
		case 28177:
			if(player.getRights().OwnerDeveloperOnly()){
				TeleportHandler.teleportPlayer(player, new Position(2607, 3093), TeleportType.NORMAL);
				player.getPacketSender().sendMessage("Non owner/dev can not tele here yet.");
			} else {
				player.getPacketSender().sendMessage("Construction is not yet finished.");
			}
			break;
			case DropViewer.SEARCH_BUTTON_ID:
				player.getDropViewer().search();
				break;
		case -27454:
		case -27534:
		case -16534:
		case 36002:
		case 26003:
		case 5384:
			player.getPacketSender().sendInterfaceRemoval();
			break;
		case 1036:
			EnergyHandler.rest(player);
			break;
		case -26376:
			PlayersOnlineInterface.showInterface(player);
			break;
			case -16036:
				player.getAchievementManger().openInterface();
				break;
		case 26250:
		case 27229:
			DungeoneeringParty.create(player);
			for (Bank b : player.getBanks()) {
				if (b.contains(15707)) {
					player.getPacketSender().sendMessage("You already have a Ring of Kinship in your bank.");
					return;
				}
			}
			if (player.getInventory().contains(15707)) {
				player.getPacketSender().sendMessage("Use your Ring of Kinship to invite players!");
				return;
			} else {
				player.getInventory().add(15707, 1);
				player.getPacketSender().sendMessage("You can use your Ring of Kinship to invite others to your party!");
			}
			break;
		case 26226:
		case 26229:
			if(Dungeoneering.doingDungeoneering(player)) {
				DialogueManager.start(player, 114);
				player.setDialogueActionId(71);
			} else {
				Dungeoneering.leave(player, false, true);
			}
			break;
			case -5398:
				if(player.getClickDelay().elapsed(1000)) {
					if (player.getAttendanceUI().isPopUp()) {
						player.getAttendanceUI().setPopUp(false);
						player.getPacketSender().sendSpriteChange(60138, 588);
					} else {
						player.getAttendanceUI().setPopUp(true);
						player.getPacketSender().sendSpriteChange(60138, 589);
					}
					player.getClickDelay().reset();
				}
				break;
		case 26244:
		case 26247:
			if(player.getMinigameAttributes().getDungeoneeringAttributes().getParty() != null) {
				if(player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getOwner().getUsername().equals(player.getUsername())) {
					DialogueManager.start(player, id == 26247 ? 106 : 105);
					player.setDialogueActionId(id == 26247 ? 68 : 67);
				} else {
					player.getPacketSender().sendMessage("Only the party owner can change this setting.");
				}
			}
			break;
		case 28180:
			TeleportHandler.teleportPlayer(player, new Position(3450, 3715), player.getSpellbook().getTeleportType());
			break;
		case 14176:
			player.setDestroyItemSlot(-1);
			player.getPacketSender().sendInterfaceRemoval();
			break;
		case 14175:
			player.getPacketSender().sendInterfaceRemoval();
			if(player.getDestroyItemSlot() != -1 && player.getInventory().get(player.getDestroyItemSlot()).getId() != -1) {
				Item destroying = player.getInventory().get(player.getDestroyItemSlot());
				ItemBinding.unbindItem(player, destroying.getId());
				player.getInventory().delete(destroying, player.getDestroyItemSlot(), true);
				PlayerLogs.log(player.getUsername(), "Player destroying item: "
						+destroying.getId()+", amount: "+destroying.getAmount());
				player.getPacketSender().sendMessage("Your item vanishes as it hits the floor.");
				Sounds.sendSound(player, Sound.DROP_ITEM);
			}
			player.setDestroyItemSlot(-1);
			break;
		case 1013:
			player.getSkillManager().setTotalGainedExp(0);
			break;
		case -26349:
			KillsTracker.open(player);
			break;
		case -26348:
			DropLog.open(player);
			break;
		case -10531:
			if(player.isKillsTrackerOpen()) {
				player.setKillsTrackerOpen(false);
				player.getPacketSender().sendTabInterface(GameSettings.QUESTS_TAB, 639);
			}
			break;
			case -14178:
				player.getPacketSender().sendEnterInputPrompt("Enter achievement to search for.");
				player.setInputHandling(new AchievementSearch());
				break;
		case -26337:
			player.getPacketSender().sendString(1, GameSettings.StoreUrl);
			player.getPacketSender().sendMessage("Attempting to open the forums");
			break;
		case -26338:
			player.getPacketSender().sendString(1, GameSettings.RuleUrl);
			player.getPacketSender().sendMessage("Attempting to open the rules");
			break;
		case -26339:
			player.getPacketSender().sendString(1, GameSettings.ForumUrl);
			player.getPacketSender().sendMessage("Attempting to open the store");
			break;
		case -26336:
			player.getPacketSender().sendString(1, GameSettings.VoteUrl);
			player.getPacketSender().sendMessage("Attempting to open the Vote page");
			break;
		case -26335:
			player.getPacketSender().sendString(1, GameSettings.HiscoreUrl);
			player.getPacketSender().sendMessage("Attempting to open the Hiscore page");
			break;
		case -26334:
			player.getPacketSender().sendString(1, GameSettings.ReportUrl);
			player.getPacketSender().sendMessage("Attempting to open the report page");
			break;
		case 350:
			player.getPacketSender().sendMessage("To autocast a spell, please right-click it and choose the autocast option.").sendTab(GameSettings.MAGIC_TAB).sendConfig(108, player.getAutocastSpell() == null ? 3 : 1);
			break;
		case 12162:
			DialogueManager.start(player, 212);
			player.setDialogueActionId(100000);
			//TeleportHandler.teleportPlayer(player, new Position(3674, 2966), TeleportType.NORMAL);
			//player.getPacketSender().sendMessage("Get a task from a Slayer Master here. (Slayer tower is now in Dungeon teleports)");
			break;
		case 29335:
			if(player.getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
				return;
			}
			DialogueManager.start(player, 60);
			player.setDialogueActionId(27);
			break;
		case 29455:
			if(player.getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
				return;
			}
			ClanChatManager.toggleLootShare(player);
			break;
		case 8658:
			DialogueManager.start(player, 55);
			player.setDialogueActionId(26);
			break;
		case 1195:
			TeleportHandler.teleportPlayer(player, GameSettings.HOME_CORDS.copy(), player.getSpellbook().getTeleportType());
			break;
		case 8667:
			TeleportHandler.teleportPlayer(player, new Position(2742, 3443), player.getSpellbook().getTeleportType());
			break;
		case 8672:
			TeleportHandler.teleportPlayer(player, new Position(2135, 5533, 3), player.getSpellbook().getTeleportType());
			//player.getPacketSender().sendMessage("<img=10> To get started with Runecrafting, buy a talisman and use the locate option on it.");
			break;
		case 8861:
			TeleportHandler.teleportPlayer(player, new Position(2914, 3450), player.getSpellbook().getTeleportType());
			break;
		case 8656:
			player.setDialogueActionId(47);
			DialogueManager.start(player, 86);
			break;
			case -15978:
				player.getDungeonPartyManager().showInterface();
				break;
			case -10530:
			case 1042:
				if(!player.isInDungeon()) {
					player.getTeleportMenuManager().showInterface();
				} else {
					player.getPacketSender().sendMessage("@red@You can't open this at this moment.");
				}
				break;
		case 8659:
			TeleportHandler.teleportPlayer(player, new Position(3079, 9499), player.getSpellbook().getTeleportType());
			break;
		case 8664:
			TeleportHandler.teleportPlayer(player, new Position(3094, 3501), player.getSpellbook().getTeleportType());
			break;
		case 8666:
			TeleportHandler.teleportPlayer(player, new Position(3084, 3484), player.getSpellbook().getTeleportType());
			break;
		case 8671:
			player.setDialogueActionId(56);
			DialogueManager.start(player, 89);
			break;
		case 8670:
			TeleportHandler.teleportPlayer(player, new Position(2561, 3866), player.getSpellbook().getTeleportType());
			break;
		case 8668:
			TeleportHandler.teleportPlayer(player, new Position(2538, 3890), player.getSpellbook().getTeleportType());
			break;
		case 8665:
			TeleportHandler.teleportPlayer(player, new Position(3143, 3446), player.getSpellbook().getTeleportType());
			//player.getPacketSender().sendMessage("Welcome to the new Cooking zone!");
			break;
		case 8662:
			TeleportHandler.teleportPlayer(player, new Position(2594, 3404), player.getSpellbook().getTeleportType());
			break;
		case 13928:
			TeleportHandler.teleportPlayer(player, new Position(3052, 3304), player.getSpellbook().getTeleportType());
			break;
		case 28179:
			TeleportHandler.teleportPlayer(player, new Position(2209, 5348), player.getSpellbook().getTeleportType());
			break;
		case 28178:
			DialogueManager.start(player, 54);
			player.setDialogueActionId(25);
			break;
		case 1159: //Bones to Bananas
		case 15877://Bones to peaches
		case 30306:
			MagicSpells.handleMagicSpells(player, id);
			break;
		case 10001:
			if(player.getInterfaceId() == -1) {
				Consumables.handleHealAction(player);
			} else {
				player.getPacketSender().sendMessage("You cannot heal yourself right now.");
			}
			break;
		case 18025:
			if(PrayerHandler.isActivated(player, PrayerHandler.AUGURY)) {
				PrayerHandler.deactivatePrayer(player, PrayerHandler.AUGURY);
			} else {
				PrayerHandler.activatePrayer(player, PrayerHandler.AUGURY);
			}
			break;
		case 18018:
			if(PrayerHandler.isActivated(player, PrayerHandler.RIGOUR)) {
				PrayerHandler.deactivatePrayer(player, PrayerHandler.RIGOUR);
			} else {
				PrayerHandler.activatePrayer(player, PrayerHandler.RIGOUR);
			}
			break;
		case 10000:
		case 950:
			if(player.getInterfaceId() < 0)
				player.getPacketSender().sendInterface(40030);
			else
				player.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
			break;
		case 3546:
		case 3420:
			if(System.currentTimeMillis() - player.getTrading().lastAction <= 300)
				return;
			player.getTrading().lastAction = System.currentTimeMillis();
			if(player.getTrading().inTrade()) {
				player.getTrading().acceptTrade(id == 3546 ? 2 : 1);
			} else {
				player.getPacketSender().sendInterfaceRemoval();
			}
			break;
			case 29456:
				player.getDungeonViewer().displayActiveDungeonList();
				break;
		case 10162:
		case -18269:
			player.getPacketSender().sendInterfaceRemoval();
			break;
		case 841:
			ingredientsBook.readBook(player, player.getCurrentBookPage() + 2, true);
			break;
		case 839:
			ingredientsBook.readBook(player, player.getCurrentBookPage() - 2, true);
			break;
		case 14922:
			player.getPacketSender().sendClientRightClickRemoval().sendInterfaceRemoval();
			break;
		case 14921:
			player.getPacketSender().sendMessage("Please visit the forums and ask for help in the support section.");
			break;
		case 5294:
			player.getPacketSender().sendClientRightClickRemoval().sendInterfaceRemoval();
			player.setDialogueActionId(player.getBankPinAttributes().hasBankPin() ? 8 : 7);
			DialogueManager.start(player, DialogueManager.getDialogues().get(player.getBankPinAttributes().hasBankPin() ? 12 : 9));
			break;
		case 15002:
			if(!player.busy() && !player.getCombatBuilder().isBeingAttacked() && !Dungeoneering.doingDungeoneering(player)) {
				player.getSkillManager().stopSkilling();
				player.getPriceChecker().open();
			} else {
				player.getPacketSender().sendMessage("You cannot open this right now.");
			}
			break;
		case 2735:
		case 1511:
			if(player.getSummoning().getBeastOfBurden() != null) {
				player.getSummoning().toInventory();
				player.getPacketSender().sendInterfaceRemoval();
			} else {
				player.getPacketSender().sendMessage("You do not have a familiar who can hold items.");
			}
			break;
		case -11501:
		case -11504:
		case -11498:
		case -11507:
		case 1020:
		case 1021:
		case 1019:
		case 1018:
			if(id == 1020 || id == -11504)
				SummoningTab.renewFamiliar(player);
			else if(id == 1019 || id == -11501)
				SummoningTab.callFollower(player);
			else if(id == 1021 || id == -11498)
				SummoningTab.handleDismiss(player, false);
			else if(id == -11507) //TODO swagyolo
				player.getSummoning().store();
			else if(id == 1018) 
				player.getSummoning().toInventory();
			break;
			case 1037:
			SummoningTab.callFollower(player);
			break;
		case 1038:
			SummoningTab.renewFamiliar(player);
			break;
		case 1039:
			SummoningTab.handleDismiss(player, false);
			break;
		case 1040:
			player.getSummoning().toInventory();
			break;
		case 1041:
			player.getSummoning().store();
			break;
		case 1095:
			player.setExperienceLocked(!player.experienceLocked());
			if (player.experienceLocked()) {
				player.getPacketSender().sendMessage("Your EXP is now locked, revert this lock to get EXP again.");
			} else {
				player.getPacketSender().sendMessage("Your EXP is unlocked, and you will gain EXP as normal.");
			}
			break;
		case 11004:
			player.getPacketSender().sendTab(GameSettings.SKILLS_TAB);
			DialogueManager.sendStatement(player, "Simply press on the skill you want to train in the skills tab.");
			player.setDialogueActionId(-1);
			break;
		case 2799:
		case 2798:
		case 1747:
		case 1748:
		case 8890:
		case 8886:
		case 8875:
		case 8871:
		case 8894:
			ChatboxInterfaceSkillAction.handleChatboxInterfaceButtons(player, id);
			break;
		case 14873:
		case 14874:
		case 14875:
		case 14876:
		case 14877:
		case 14878:
		case 14879:
		case 14880:
		case 14881:
		case 14882:
			BankPin.clickedButton(player, id);
			break;
		case 27005:
		case 22012:
			if(!player.isBanking() || player.getInterfaceId() != 5292)
				return;
			Bank.depositItems(player, id == 27005 ? player.getEquipment() : player.getInventory(), false);
			break;
		case 27023:
			if(!player.isBanking() || player.getInterfaceId() != 5292)
				return;
			if(player.getSummoning().getBeastOfBurden() == null) {
				player.getPacketSender().sendMessage("You do not have a familiar which can hold items.");
				return;
			}
			Bank.depositItems(player, player.getSummoning().getBeastOfBurden(), false);
			break;
		case 22008:
			if(!player.isBanking() || player.getInterfaceId() != 5292)
				return;
			player.setNoteWithdrawal(!player.withdrawAsNote());
			break;
		case 21000:
			if(!player.isBanking() || player.getInterfaceId() != 5292)
				return;
			player.setSwapMode(!player.swapMode());
			break;
		case 27009:
		// 	MoneyPouch.toBank(player);
			break;
		case 27014:
		case 27015:
		case 27016:
		case 27017:
		case 27018:
		case 27019:
		case 27020:
		case 27021:
		case 27022:
			if(!player.isBanking())
				return;
			if(player.getBankSearchingAttribtues().isSearchingBank())
				BankSearchAttributes.stopSearch(player, true);
			int bankId = id - 27014;
			boolean empty = bankId > 0 ? Bank.isEmpty(player.getBank(bankId)) : false;
			if(!empty || bankId == 0) {
				player.setCurrentBankTab(bankId);
				player.getPacketSender().sendString(5385, "scrollreset");
				player.getPacketSender().sendString(27002, Integer.toString(player.getCurrentBankTab()));
				player.getPacketSender().sendString(27000, "1");
				player.getBank(bankId).open();
			} else
				player.getPacketSender().sendMessage("To create a new tab, please drag an item here.");	
			break;
		case 22004:
			if(!player.isBanking())
				return;
			if(!player.getBankSearchingAttribtues().isSearchingBank()) {
				player.getBankSearchingAttribtues().setSearchingBank(true);
				player.setInputHandling(new EnterSyntaxToBankSearchFor());
				player.getPacketSender().sendEnterInputPrompt("What would you like to search for?");
			} else {
				BankSearchAttributes.stopSearch(player, true);
			}
			break;
		case 22845:
		case 24115:
		case 24010:
		case 24041:
		case 150:
			player.setAutoRetaliate(!player.isAutoRetaliate());
			break;
		case 29332:
			ClanChat clan = player.getCurrentClanChat();
			if (clan == null) {
				player.getPacketSender().sendMessage("You are not in a clanchat channel.");
				return;
			}
			ClanChatManager.leave(player, false);
			player.setClanChatName(null);
			break;
		case 29329:
			if(player.getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
				return;
			}
			player.setInputHandling(new EnterClanChatToJoin());
			player.getPacketSender().sendEnterInputPrompt("Enter the name of the clanchat channel you wish to join:");
			break;
		case 19158:
		case 152:
			if(player.getRunEnergy() <= 1) {
				player.getPacketSender().sendMessage("You do not have enough energy to do this.");
				player.setRunning(false);
			} else
				player.setRunning(!player.isRunning());
			player.getPacketSender().sendRunStatus();
			break;
		case 15004:
			player.setExperienceLocked(!player.experienceLocked());
			String type = player.experienceLocked() ? "locked" : "unlocked";
			player.getPacketSender().sendMessage("Your experience is now "+type+".");
			break;
		case 27651:
		case 15001:
			if(player.getInterfaceId() <= 0) {
				player.getSkillManager().stopSkilling();
				BonusManager.update(player);
				player.getPacketSender().sendInterface(21172);
			} else 
				player.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
			break;
		case 15003:
			if(player.getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
				return;
			}
			player.getSkillManager().stopSkilling();
			ItemsKeptOnDeath.sendInterface(player);
			break;
		case 2458: //Logout
			if(player.logout()) {
				World.getPlayers().remove(player);
			}
			break;
	//	case 10003:
		case 29138:
		case 29038:
		case 29063:
		case 29113:
		case 29163:
		case 29188:
		case 29213:
		case 29238:
		case 30007:
		case 48023:
		case 33033:
		case 30108:
		case 7473:
		case 7562:
		case 7487:
		case 7788:
		case 8481:
		case 7612:
		case 7587:
		case 7662:
		case 7462:
		case 7548:
		case 7687:
		case 7537:
		case 12322:
		case 7637:
		case 12311:
			CombatSpecial.activate(player);
			break;
		case 1772: // shortbow & longbow & blowpipe & crossbow
			if (player.getWeapon() == WeaponInterface.SHORTBOW) {
				player.setFightType(FightType.SHORTBOW_ACCURATE);
			} else if (player.getWeapon() == WeaponInterface.LONGBOW) {
				player.setFightType(FightType.LONGBOW_ACCURATE);
			} else if (player.getWeapon() == WeaponInterface.CROSSBOW) {
				player.setFightType(FightType.CROSSBOW_ACCURATE);
			} else if (player.getWeapon() == WeaponInterface.BLOWPIPE) {
				player.setFightType(FightType.BLOWPIPE_ACCURATE);
			} else if (player.getWeapon() == WeaponInterface.BSOAT) {
				player.setFightType(FightType.BSOAT_ACCURATE);
			} else if (player.getWeapon() == WeaponInterface.ARMADYLXBOW) {
				player.setFightType(FightType.ARMADYLXBOW_ACCURATE);
			}
			break;
		case 1771:
			if (player.getWeapon() == WeaponInterface.SHORTBOW) {
				player.setFightType(FightType.SHORTBOW_RAPID);
			} else if (player.getWeapon() == WeaponInterface.LONGBOW) {
				player.setFightType(FightType.LONGBOW_RAPID);
			} else if (player.getWeapon() == WeaponInterface.CROSSBOW) {
				player.setFightType(FightType.CROSSBOW_RAPID);
			} else if (player.getWeapon() == WeaponInterface.BLOWPIPE) {
				player.setFightType(FightType.BLOWPIPE_RAPID);
			} else if (player.getWeapon() == WeaponInterface.BSOAT) {
				player.setFightType(FightType.BSOAT_RAPID);
			} else if (player.getWeapon() == WeaponInterface.ARMADYLXBOW) {
				player.setFightType(FightType.ARMADYLXBOW_RAPID);
			}
			break;
		case 1770:
			if (player.getWeapon() == WeaponInterface.SHORTBOW) {
				player.setFightType(FightType.SHORTBOW_LONGRANGE);
			} else if (player.getWeapon() == WeaponInterface.LONGBOW) {
				player.setFightType(FightType.LONGBOW_LONGRANGE);
			} else if (player.getWeapon() == WeaponInterface.CROSSBOW) {
				player.setFightType(FightType.CROSSBOW_LONGRANGE);
			} else if (player.getWeapon() == WeaponInterface.BLOWPIPE) {
				player.setFightType(FightType.BLOWPIPE_LONGRANGE);
			} else if (player.getWeapon() == WeaponInterface.BSOAT) {
				player.setFightType(FightType.BSOAT_LONGRANGE);
			}else if (player.getWeapon() == WeaponInterface.ARMADYLXBOW) {
				player.setFightType(FightType.ARMADYLXBOW_LONGRANGE);
			}
			break;
		case 2282: // dagger & sword
			if (player.getWeapon() == WeaponInterface.DAGGER) {
				player.setFightType(FightType.DAGGER_STAB);
			} else if (player.getWeapon() == WeaponInterface.SWORD) {
				player.setFightType(FightType.SWORD_STAB);
			}
			break;
		case 2285:
			if (player.getWeapon() == WeaponInterface.DAGGER) {
				player.setFightType(FightType.DAGGER_LUNGE);
			} else if (player.getWeapon() == WeaponInterface.SWORD) {
				player.setFightType(FightType.SWORD_LUNGE);
			}
			break;
		case 2284:
			if (player.getWeapon() == WeaponInterface.DAGGER) {
				player.setFightType(FightType.DAGGER_SLASH);
			} else if (player.getWeapon() == WeaponInterface.SWORD) {
				player.setFightType(FightType.SWORD_SLASH);
			}
			break;
		case 2283:
			if (player.getWeapon() == WeaponInterface.DAGGER) {
				player.setFightType(FightType.DAGGER_BLOCK);
			} else if (player.getWeapon() == WeaponInterface.SWORD) {
				player.setFightType(FightType.SWORD_BLOCK);
			}
			break;
		case 2429: // scimitar & longsword
			if (player.getWeapon() == WeaponInterface.SCIMITAR) {
				player.setFightType(FightType.SCIMITAR_CHOP);
			} else if (player.getWeapon() == WeaponInterface.LONGSWORD) {
				player.setFightType(FightType.LONGSWORD_CHOP);
			}
			break;
		case 2432:
			if (player.getWeapon() == WeaponInterface.SCIMITAR) {
				player.setFightType(FightType.SCIMITAR_SLASH);
			} else if (player.getWeapon() == WeaponInterface.LONGSWORD) {
				player.setFightType(FightType.LONGSWORD_SLASH);
			}
			break;
		case 2431:
			if (player.getWeapon() == WeaponInterface.SCIMITAR) {
				player.setFightType(FightType.SCIMITAR_LUNGE);
			} else if (player.getWeapon() == WeaponInterface.LONGSWORD) {
				player.setFightType(FightType.LONGSWORD_LUNGE);
			}
			break;
		case 2430:
			if (player.getWeapon() == WeaponInterface.SCIMITAR) {
				player.setFightType(FightType.SCIMITAR_BLOCK);
			} else if (player.getWeapon() == WeaponInterface.LONGSWORD) {
				player.setFightType(FightType.LONGSWORD_BLOCK);
			}
			break;
		case 3802: // mace
			player.setFightType(FightType.MACE_POUND);
			break;
		case 3805:
			player.setFightType(FightType.MACE_PUMMEL);
			break;
		case 3804:
			player.setFightType(FightType.MACE_SPIKE);
			break;
		case 3803:
			player.setFightType(FightType.MACE_BLOCK);
			break;
		case 4454: // knife, thrownaxe, dart & javelin
			if (player.getWeapon() == WeaponInterface.KNIFE) {
				player.setFightType(FightType.KNIFE_ACCURATE);
			} else if (player.getWeapon() == WeaponInterface.THROWNAXE) {
				player.setFightType(FightType.THROWNAXE_ACCURATE);
			} else if (player.getWeapon() == WeaponInterface.DART) {
				player.setFightType(FightType.DART_ACCURATE);
			} else if (player.getWeapon() == WeaponInterface.JAVELIN) {
				player.setFightType(FightType.JAVELIN_ACCURATE);
			}
			break;
		case 4453:
			if (player.getWeapon() == WeaponInterface.KNIFE) {
				player.setFightType(FightType.KNIFE_RAPID);
			} else if (player.getWeapon() == WeaponInterface.THROWNAXE) {
				player.setFightType(FightType.THROWNAXE_RAPID);
			} else if (player.getWeapon() == WeaponInterface.DART) {
				player.setFightType(FightType.DART_RAPID);
			} else if (player.getWeapon() == WeaponInterface.JAVELIN) {
				player.setFightType(FightType.JAVELIN_RAPID);
			}
			break;
		case 4452:
			if (player.getWeapon() == WeaponInterface.KNIFE) {
				player.setFightType(FightType.KNIFE_LONGRANGE);
			} else if (player.getWeapon() == WeaponInterface.THROWNAXE) {
				player.setFightType(FightType.THROWNAXE_LONGRANGE);
			} else if (player.getWeapon() == WeaponInterface.DART) {
				player.setFightType(FightType.DART_LONGRANGE);
			} else if (player.getWeapon() == WeaponInterface.JAVELIN) {
				player.setFightType(FightType.JAVELIN_LONGRANGE);
			}
			break;
		case 4685: // spear
			player.setFightType(FightType.SPEAR_LUNGE);
			break;
		case 4688:
			player.setFightType(FightType.SPEAR_SWIPE);
			break;
		case 4687:
			player.setFightType(FightType.SPEAR_POUND);
			break;
		case 4686:
			player.setFightType(FightType.SPEAR_BLOCK);
			break;
		case 4711: // 2h sword
			player.setFightType(FightType.TWOHANDEDSWORD_CHOP);
			break;
		case 4714:
			player.setFightType(FightType.TWOHANDEDSWORD_SLASH);
			break;
		case 4713:
			player.setFightType(FightType.TWOHANDEDSWORD_SMASH);
			break;
		case 4712:
			player.setFightType(FightType.TWOHANDEDSWORD_BLOCK);
			break;
		case 5576: // pickaxe
			player.setFightType(FightType.PICKAXE_SPIKE);
			break;
		case 5579:
			player.setFightType(FightType.PICKAXE_IMPALE);
			break;
		case 5578:
			player.setFightType(FightType.PICKAXE_SMASH);
			break;
		case 5577:
			player.setFightType(FightType.PICKAXE_BLOCK);
			break;
		case 7768: // claws
			player.setFightType(FightType.CLAWS_CHOP);
			break;
		case 7771:
			player.setFightType(FightType.CLAWS_SLASH);
			break;
		case 7770:
			player.setFightType(FightType.CLAWS_LUNGE);
			break;
		case 7769:
			player.setFightType(FightType.CLAWS_BLOCK);
			break;
		case 8466: // halberd
			player.setFightType(FightType.HALBERD_JAB);
			break;
		case 8468:
			player.setFightType(FightType.HALBERD_SWIPE);
			break;
		case 8467:
			player.setFightType(FightType.HALBERD_FEND);
			break;
		case 5862: // unarmed
			player.setFightType(FightType.UNARMED_PUNCH);
			break;
		case 5861:
			player.setFightType(FightType.UNARMED_KICK);
			break;
		case 5860:
			player.setFightType(FightType.UNARMED_BLOCK);
			break;
		case 12298: // whip
			player.setFightType(FightType.WHIP_FLICK);
			break;
		case 12297:
			player.setFightType(FightType.WHIP_LASH);
			break;
		case 12296:
			player.setFightType(FightType.WHIP_DEFLECT);
			break;
		case 336: // staff
			player.setFightType(FightType.STAFF_BASH);
			break;
		case 335:
			player.setFightType(FightType.STAFF_POUND);
			break;
		case 334:
			player.setFightType(FightType.STAFF_FOCUS);
			break;
		case 433: // warhammer
			player.setFightType(FightType.WARHAMMER_POUND);
			break;
		case 432:
			player.setFightType(FightType.WARHAMMER_PUMMEL);
			break;
		case 431:
			player.setFightType(FightType.WARHAMMER_BLOCK);
			break;
		case 782: // scythe
			player.setFightType(FightType.SCYTHE_REAP);
			break;
		case 784:
			player.setFightType(FightType.SCYTHE_CHOP);
			break;
		case 785:
			player.setFightType(FightType.SCYTHE_JAB);
			break;
		case 783:
			player.setFightType(FightType.SCYTHE_BLOCK);
			break;
		case 1704: // battle axe
			player.setFightType(FightType.BATTLEAXE_CHOP);
			break;
		case 1707:
			player.setFightType(FightType.BATTLEAXE_HACK);
			break;
		case 1706:
			player.setFightType(FightType.BATTLEAXE_SMASH);
			break;
		case 1705:
			player.setFightType(FightType.BATTLEAXE_BLOCK);
			break;
		}
	}

	private boolean checkHandlers(Player player, int id) {
		if(Construction.handleButtonClick(id, player)) {
			return true;
		}
		switch(id) {
		case 2494:
		case 2495:
		case 2496:
		case 2497:
		case 2498:
		case 2471:
		case 2472:
		case 2473:
		case 2461:
		case 2462:
		case 2482:
		case 2483:
		case 2484:
		case 2485:
			DialogueOptions.handle(player, id);
			return true;
		}
		if(Sounds.handleButton(player, id)) {
			return true;
		}
		if (PrayerHandler.isButton(id)) {
			PrayerHandler.togglePrayerWithActionButton(player, id);
			return true;
		}
		if (CurseHandler.isButton(player, id)) {
			return true;
		}
		if(Autocasting.handleAutocast(player, id)) {
			return true;
		}
		if(SmithingData.handleButtons(player, id)) {
			return true;
		}
		if(PouchMaking.pouchInterface(player, id)) {
			return true;
		}
		if(LoyaltyProgramme.handleButton(player, id)) {
			return true;
		}
		if(Fletching.fletchingButton(player, id)) {
			return true;
		}
		if(LeatherMaking.handleButton(player, id) || Tanning.handleButton(player, id)) {
			return true;
		}
		if(Emotes.doEmote(player, id)) {
			return true;
		}
		if(PestControl.handleInterface(player, id)) {
			return true;
		}
		if(player.getLocation() == Location.DUEL_ARENA && Dueling.handleDuelingButtons(player, id)) {
			return true;
		}

		if(ExperienceLamps.handleButton(player, id)) {
			return true;
		}
		if(PlayersOnlineInterface.handleButton(player, id)) {
			return true;
		}
		if(ClanChatManager.handleClanChatSetupButton(player, id)) {
			return true;
		}
		if(player.getPlayerShopManager().handleButtonClick(id)) {
			return true;
		}
		if(player.getQuestManager().getQuestInterface().handleTabSwitch(id)) {
			return true;
		}
		if(player.getTeleportMenuManager().handleButtonClick(id)) {
			return true;
		}
		if(player.getCollectionLogManager().handleButtonClick(id)) {
			return true;
		}
		if(player.getUpgradeManager().handleButtonClick(id)) {
			return true;
		}
	//	if(player.getAchievementManger().handleButtonClick(id)) {
	//		return true;
	//	}
		if(player.getDungeonPartyManager().handleButtonClick(id)) {
			return true;
		}
		if(player.getGameModeSelector().handleButtonClick(id)) {
			return true;
		}
		if(player.getTradingPostManager().handleButtonClick(id)) {
			return true;
		}

		return false;
	}
	
	public static final int OPCODE = 185;
}
