package com.ruse.world.content.teleporter;

import com.google.common.collect.ImmutableList;
import com.ruse.GameSettings;
import com.ruse.model.Item;
import com.ruse.model.Position;

import java.util.Arrays;

public enum Teleport {
    Areas(
            "Forbidden Outpost",
            351,
            TeleportCategory.AREAS,
            "Training Arena that offers training for new players to get started in " + GameSettings.RSPS_NAME + "!",
            new Position(3055, 2853),
            new Item(4151, 1), new Item(4151,1)),

    PIRATE_HAVEN(
            "Ancient Hideout",
            13397,
            TeleportCategory.AREAS,
            "Great place to upgrade your gear once you're done with training arena!",
            new Position(3047, 2888),
            new Item(4151, 1), new Item(4151,1)),

    LOST_FOREST(
            "Lost Forest",
            13396,
            TeleportCategory.AREAS,
            "Great place to upgrade your gear once you're done with training arena!",
            new Position(3226, 2847),
            new Item(4151, 1), new Item(4151,1)),

    HAUNTED_WOODS(
            "Haunted Woods",
            13400,
            TeleportCategory.AREAS,
            "Great place to upgrade your gear once you're done with training arena!",
            new Position(2909, 2833),
            new Item(4151, 1), new Item(4151,1)),

    THE_INFERNO(
            "The Inferno",
            7773,
            TeleportCategory.AREAS,
            "Great place to upgrade your gear once you're done with training arena!",
            new Position(2864, 3235),
            new Item(4151, 1), new Item(4151,1)),

    TEST_TWO(
            "Boss #1",
            50,
            TeleportCategory.BOSSES,
            "Hey info two here",
            new Position(3333,3333),
            new Item(6585, 1), new Item(6585,1)),

    BARROWS(
            "Barrows",
            2026,
            TeleportCategory.MINIGAMES,
            "Custom Twist on the barrows mini-game! Offers gear drops that help med-level players progress in their journey!",
            new Position(3333,3333),
            new Item(11732, 1), new Item(11732)),

    PEST_CONTROL(
            "Pest Control",
            7203,
            TeleportCategory.MINIGAMES,
            "Custom Twist on the Pest Control mini-game, Offers High level weapons that will help get players started with raids! Enjoy!",
            new Position(3333,3333),
            new Item(4151, 1), new Item(4151,1)
    ),

    FIGHT_CAVES(
            "Fight Caves",
            13721,
            TeleportCategory.MINIGAMES,
            "Engage in this challenging wave-based mini-game, where overcoming each level rewards you generously, including the acquisition of a formidable cape!",
            new Position(3333,3333),
            new Item(4151, 1), new Item(4151,1)
    ),

    FAIRY_FRENZY(
            "Fairy Frenzy",
            31848,
            TeleportCategory.MINIGAMES,
            "Play this fun mini-game with your friends to acquire fairy companions which you can attach perks to and level up!",
            new Position(3333,3333),
            new Item(4151, 1), new Item(4151,1)
    ),

    ETHEREAL_DUNGEON(
            "Ethereal Dungeon",
            53,
            TeleportCategory.DUNGEONS,
            "Ethereal Dungeon is " + GameSettings.RSPS_NAME + "'s first slayer dungeon! " +
                    "This dungeon doesn't have high gear requirements so you'll be able to kill mobs when you're starting out, Speak with a slayer master to get a task here!",
            new Position(3333,3333),
            new Item(6199, 1), new Item(6199, 1)),

    DIABOLICAL_DUNGEON(
            "Diabolical Dungeon",
            53,
            TeleportCategory.DUNGEONS,
            GameSettings.RSPS_NAME + "'s second slayer dungeon! Get slayer tasks here once you've unlocked the dungeon from the slayer master!",
            new Position(3333,3333),
            new Item(6199, 1), new Item(6199, 1)),

    GLOBAL_ONE(
            "global",
            53,
            TeleportCategory.GLOBALS,
            GameSettings.RSPS_NAME + "'s second slayer dungeon! Get slayer tasks here once you've unlocked the dungeon from the slayer master!",
            new Position(3333,3333),
            new Item(6199, 1), new Item(6199, 1)),

    RAIDS_ONE(
            "raids",
            53,
            TeleportCategory.RAIDS,
            GameSettings.RSPS_NAME + "'s second slayer dungeon! Get slayer tasks here once you've unlocked the dungeon from the slayer master!",
            new Position(3333,3333),
            new Item(6199, 1), new Item(6199, 1));

    private final String name;
    private final int npcShow;
    private final TeleportCategory category;
    private final String info;
    private final Position position;
    private final Item[] items;

    Teleport(String name, int npcShow, TeleportCategory category, String info, Position position, Item... items) {
        this.name = name;
        this.npcShow = npcShow;
        this.category = category;
        this.info = info;
        this.position = position;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public TeleportCategory getCategory() {
        return category;
    }

    public String getInfo() {
        return info;
    }

    public Item[] getItems() {
        return items;
    }

    public Position getPosition() {
        return position;
    }

    public int getNpcShow() {
        return npcShow;
    }

    public static final ImmutableList<Teleport> AREAS_TELEPORTS = ImmutableList.copyOf(Arrays.stream(values()).filter(it -> it.category == TeleportCategory.AREAS).toList());
    public static final ImmutableList<Teleport> BOSSES_TELEPORTS = ImmutableList.copyOf(Arrays.stream(values()).filter(it -> it.category == TeleportCategory.BOSSES).toList());
    public static final ImmutableList<Teleport> MINIGAMES_TELEPORTS = ImmutableList.copyOf(Arrays.stream(values()).filter(it -> it.category == TeleportCategory.MINIGAMES).toList());
    public static final ImmutableList<Teleport> DUNGEONS_TELEPORTS = ImmutableList.copyOf(Arrays.stream(values()).filter(it -> it.category == TeleportCategory.DUNGEONS).toList());
    public static final ImmutableList<Teleport> GLOBALS_TELEPORTS = ImmutableList.copyOf(Arrays.stream(values()).filter(it -> it.category == TeleportCategory.GLOBALS).toList());
    public static final ImmutableList<Teleport> RAIDS_TELEPORTS = ImmutableList.copyOf(Arrays.stream(values()).filter(it -> it.category == TeleportCategory.RAIDS).toList());

    public static ImmutableList<Teleport> getTeleportsByCategory(TeleportCategory category) {
        return switch (category) {
            case AREAS -> AREAS_TELEPORTS;
            case BOSSES -> BOSSES_TELEPORTS;
            case MINIGAMES -> MINIGAMES_TELEPORTS;
            case DUNGEONS -> DUNGEONS_TELEPORTS;
            case GLOBALS -> GLOBALS_TELEPORTS;
            case RAIDS -> RAIDS_TELEPORTS;
        };
    }
}
