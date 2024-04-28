package com.ruse.world.content.teleporter;

import com.google.common.collect.ImmutableList;
import com.ruse.model.Position;
import com.ruse.model.entity.character.player.Player;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.content.transportation.TeleportType;

public class Teleporter {

    private static final int INTERFACE_ID = 49550;
    private static final int ITEM_CONTAINER_ID = 49692;
    private static final int NPC_HEAD_ID = 49693;
    private static final int INFO_ID = 49690;
    private static final int NAME_ID = 49682;

    private static final int TRAINING_BUTTON = -15980;
    private static final int BOSSES_BUTTON = -15979;
    private static final int MINIGAMES_BUTTON = -15978;
    private static final int DUNGEONS_BUTTON = -15977;
    private static final int GLOBALS_BUTTON = -15842;
    private static final int RAIDS_BUTTON = -15841;

    private TeleportCategory currentCategory;
    private Teleport currentTeleport;

    private final Player player;

    public Teleporter(Player player) {
        this.player = player;
    }

    public void open() {
        changeCategory(TeleportCategory.TRAINING);
        player.getPacketSender().sendInterface(INTERFACE_ID);
    }

    public boolean handleButtonClick(int buttonId) {
        switch (buttonId) {
            case TRAINING_BUTTON:
                changeCategory(TeleportCategory.TRAINING);
                return true;
            case BOSSES_BUTTON:
                changeCategory(TeleportCategory.BOSSES);
                return true;
            case MINIGAMES_BUTTON:
                changeCategory(TeleportCategory.MINIGAMES);
                return true;
            case DUNGEONS_BUTTON:
                changeCategory(TeleportCategory.DUNGEONS);
                return true;
            case GLOBALS_BUTTON:
                changeCategory(TeleportCategory.GLOBALS);
                return true;
            case RAIDS_BUTTON:
                changeCategory(TeleportCategory.RAIDS);
                return true;
        }

        if(buttonId >= -15921 && buttonId <= -15872) {
            int index = 15921 + buttonId;
            ImmutableList<Teleport> teleports = Teleport.getTeleportsByCategory(currentCategory);
            if(teleports.size() > index) {
                currentTeleport = teleports.get(index);
                showTeleport();
            }
            return true;
        }

        if(buttonId == -15859) {
            teleportPlayer();
        }

        return false;
    }

    public void showTeleport() {
        player.getPacketSender()
                .sendNpcHeadOnInterface(currentTeleport.getNpcShow(), NPC_HEAD_ID)
                .sendString(INFO_ID, currentTeleport.getInfo())
                .sendString(NAME_ID, currentTeleport.getName())
                .sendItemContainer(currentTeleport.getItems(), ITEM_CONTAINER_ID);
    }

    public void teleportPlayer() {
        Position position = currentTeleport.getPosition();
        TeleportHandler.teleportPlayer(player, position, TeleportType.NORMAL);
    }

    public void changeCategory(TeleportCategory category) {
        if(category == currentCategory) {
            return;
        }

        currentCategory = category;
        player.getPacketSender().sendConfig(312, category.getConfigFrame());
        ImmutableList<Teleport> teleports = Teleport.getTeleportsByCategory(currentCategory);
        currentTeleport = teleports.get(0);
        showTeleport();

        for(int i = 0; i < 50; i++) {
            if(teleports.size() > i) {
                player.getPacketSender().sendString(i + 49615, teleports.get(i).getName());
            } else {
                player.getPacketSender().sendString(i + 49615, "");
            }
        }
    }
}
