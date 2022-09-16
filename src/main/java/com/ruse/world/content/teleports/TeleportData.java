package com.ruse.world.content.teleports;

import com.ruse.model.Position;

public class TeleportData {

    public static final TeleportMenuItem[][] TELEPORTS = {
            {
                    /*
                     * AREAS
                    */
                    new TeleportMenuItemParent("Forbidden Outpost", TeleportType.AREAS,
                            new TeleportMenuItemChild[]{
                                    new TeleportMenuItemChild("Archer", new Position(3055, 2853), 351, TeleportMenuItemChild.CombatStyleType.RANGE),
                                    new TeleportMenuItemChild("Magician", new Position(3001, 2835), 352, TeleportMenuItemChild.CombatStyleType.MAGIC),
                                    new TeleportMenuItemChild("Warrior", new Position(3333, 3333), 353, TeleportMenuItemChild.CombatStyleType.MELEE)
                            }
                    ),

                    new TeleportMenuItemParent("Ancient Hideout", TeleportType.AREAS,
                            new TeleportMenuItemChild[]{
                                    new TeleportMenuItemChild("Ancient Skeleton", new Position(3047, 2888),  13397, TeleportMenuItemChild.CombatStyleType.MIXED),
                                    new TeleportMenuItemChild("Ancient Scorpion", new Position(3017, 2924),  13399, TeleportMenuItemChild.CombatStyleType.MIXED)
                            }
                    ),

                    new TeleportMenuItemParent("Lost Forest", TeleportType.AREAS,
                            new TeleportMenuItemChild[]{
                                    new TeleportMenuItemChild("Forest Overseer", new Position(3226, 2847),  13396,TeleportMenuItemChild.CombatStyleType.MIXED),
                                    new TeleportMenuItemChild("Forest Guardian", new Position(3263, 2838),  13395, TeleportMenuItemChild.CombatStyleType.MIXED),
                                    new TeleportMenuItemChild("Forest Beast", new Position(3284, 2847),  13394, TeleportMenuItemChild.CombatStyleType.MELEE)

                            }
                    ),

                    new TeleportMenuItemParent("Mech City", TeleportType.AREAS,
                            new TeleportMenuItemChild[]{
                                    new TeleportMenuItemChild("Mech Gentleman", new Position(3226, 2847), 13398, TeleportMenuItemChild.CombatStyleType.MELEE),
                                    new TeleportMenuItemChild("Manic Engineer", new Position(3226, 2847), 13400, TeleportMenuItemChild.CombatStyleType.RANGE)
                            }
                    ),

                    new TeleportMenuItemParent("The Inferno", TeleportType.AREAS,
                            new TeleportMenuItemChild[]{
                                    new TeleportMenuItemChild("Forest Overseer", new Position(3226, 2847),  13396,TeleportMenuItemChild.CombatStyleType.MIXED),
                                    new TeleportMenuItemChild("Forest Guardian", new Position(3263, 2838),  13395, TeleportMenuItemChild.CombatStyleType.RANGE),
                                    new TeleportMenuItemChild("Forest Beast", new Position(3284, 2847),  13394, TeleportMenuItemChild.CombatStyleType.MIXED)

                            }
                    )
            },
            {
                    /*
                     * MINI-GAMES
                    */
                    new TeleportMenuItemParent("Ancient Hideout", TeleportType.MINI_GAMES,
                            new TeleportMenuItemChild[]{
                                    new TeleportMenuItemChild("Ancient Skeleton", new Position(3047, 2888),  13397, TeleportMenuItemChild.CombatStyleType.MIXED),
                                    new TeleportMenuItemChild("Ancient Scorpion", new Position(3017, 2924),  13399, TeleportMenuItemChild.CombatStyleType.MIXED)
                            }
                    )
            },
            {
                    /*
                     * RAIDS
                    */
                    new TeleportMenuItemParent("Lost Forest", TeleportType.RAIDS,
                            new TeleportMenuItemChild[]{
                                    new TeleportMenuItemChild("Forest Overseer", new Position(3226, 2847),  13396,TeleportMenuItemChild.CombatStyleType.MIXED),
                                    new TeleportMenuItemChild("Forest Guardian", new Position(3250, 2858),  13395, TeleportMenuItemChild.CombatStyleType.RANGE)
                            }
                    )
            },
            {
                    /*
                     * MISC.
                    */
                    new TeleportMenuItemParent("Forbidden Forest", TeleportType.MISC,
                            new TeleportMenuItemChild[]{
                                    new TeleportMenuItemChild("Archer", new Position(3333, 3333),  351, TeleportMenuItemChild.CombatStyleType.RANGE),
                                    new TeleportMenuItemChild("Magician", new Position(3333, 3333),  352, TeleportMenuItemChild.CombatStyleType.MAGIC),
                                    new TeleportMenuItemChild("Warrior", new Position(3333, 3333),  353, TeleportMenuItemChild.CombatStyleType.MELEE)
                            }
                    )
            }
    };

    private TeleportData() {

    }
}
