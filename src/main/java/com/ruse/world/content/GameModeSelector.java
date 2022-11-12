package com.ruse.world.content;

import com.ruse.model.*;
import com.ruse.model.entity.character.player.Player;
import org.apache.commons.lang3.ArrayUtils;

public class GameModeSelector {
    private static final int INTERFACE_ID = 60140;

    private final Player player;
    private GameMode selectedGameMode;
    private GameType selectedGameType;

    public GameModeSelector(Player player) {
        this.player = player;
        selectedGameMode = GameMode.NORMAL;
        selectedGameType = GameType.NORMAL;
    }

    public void displayInterface() {
        player.getPacketSender().sendConfig(317, 0)
        .sendConfig(316, 0)
        .sendConfig(312, 0)
        .sendInterfaceChange(60144, 60150);
        updateInterface(true);
        player.getPacketSender().sendInterface(INTERFACE_ID);
    }

    public boolean handleButtonClick(int btnId) {
        if(btnId == 2458) {
            return false;
        }
        if(player.getInterfaceId() == INTERFACE_ID && player.newPlayer()) {
          if(btnId == -5357) {
              setModes();
              player.setNewPlayer(false);
              player.setPlayerLocked(false);
          } else if(btnId == -5351) {
              selectedGameType = GameType.NORMAL;
              updateInterface(false);
          } else if(btnId == -5350) {
              selectedGameType = GameType.REALIST;
              updateInterface(false);
          } else if(btnId == -5349) {
              selectedGameMode = GameMode.NORMAL;
              updateInterface(true);
          } else if(btnId == -5348) {
              selectedGameMode = GameMode.IRONMAN;
              updateInterface(true);
          } else if(btnId == -5347) {
              selectedGameMode = GameMode.GROUP_IRONMAN;
              player.getPacketSender().sendNpcHeadOnInterface(511, 60143);
              player.getPacketSender().sendInterfaceAnimation(60143, new Animation(9816));
              updateInterface(true);
          } else if(btnId == -5390) {
              player.getPacketSender().sendInterfaceChange(60144, 60150);
          } else if(btnId == -5389) {
              player.getPacketSender().sendInterfaceChange(60144, 60152);
          }
          return true;
        }
        return false;
    }

    public void setModes() {
        player.getPacketSender().sendIronmanMode(selectedGameMode.ordinal());
        player.setGameMode(selectedGameMode);
        player.setGameType(selectedGameType);
        Item[] inventoryItems = ArrayUtils.addAll(selectedGameMode.getInventoryStarter(), selectedGameType.getInventoryItems());
        for(Item it : selectedGameMode.getEquipmentStarter()) {
            if(it.getId()!=-1) {
                player.getEquipment().set(it.getDefinition().getEquipmentSlot(), it);
            }
        }
        player.getInventory().addItemSet(inventoryItems);
        player.getEquipment().refreshItems();
        player.getInventory().refreshItems();
        player.getUpdateFlag().flag(Flag.APPEARANCE);
        player.getPacketSender().sendInterfaceRemoval();
    }

    public void updateInterface(boolean updateEquipment) {
        player.getPacketSender().sendString(60199, selectedGameMode.getDescription());
        player.getPacketSender().sendItemContainer(ArrayUtils.addAll(selectedGameMode.getInventoryStarter(), selectedGameType.getInventoryItems()), 60151);
        if(updateEquipment) {
            Item[] equipment = selectedGameMode.getEquipmentStarter();
            for (int i = 0; i < 11; i++) {
                Item item = equipment[i];
                player.getPacketSender().sendItemOnInterface(60165 + i, item.getId(), item.getAmount())
                        .sendInterfaceVisibility(60154 + i, item.getId() == -1);
            }
        }
    }
}
