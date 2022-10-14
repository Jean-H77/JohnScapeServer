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

                    new TeleportMenuItemParent("Haunted Woods", TeleportType.AREAS,
                            new TeleportMenuItemChild[]{
                                    new TeleportMenuItemChild("Forgotten Soul", new Position(2909, 2833), 40534, TeleportMenuItemChild.CombatStyleType.MIXED),
                                    new TeleportMenuItemChild("Zombified Thrall", new Position(2914, 2856), 40886, TeleportMenuItemChild.CombatStyleType.MELEE),
                                    new TeleportMenuItemChild("Skeletal Thrall", new Position(2922, 2892), 40883, TeleportMenuItemChild.CombatStyleType.RANGE),
                                    new TeleportMenuItemChild("Ghostly Thrall", new Position(2911, 2915), 40880, TeleportMenuItemChild.CombatStyleType.MAGIC),
                                    new TeleportMenuItemChild("Manic Engineer @cya@(World Boss)", new Position(2911, 2915), 13400, TeleportMenuItemChild.CombatStyleType.RANGE)
                            }
                    ),

                    new TeleportMenuItemParent("The Inferno", TeleportType.AREAS,
                            new TeleportMenuItemChild[]{
                                    new TeleportMenuItemChild("Fire Monster", new Position(2868, 3234),  7773, TeleportMenuItemChild.CombatStyleType.MIXED),
                                    new TeleportMenuItemChild("Pyrefiend", new Position(3226, 2847),  1634, TeleportMenuItemChild.CombatStyleType.MIXED),
                                    new TeleportMenuItemChild("Fire Wave", new Position(3263, 2838),  40939, TeleportMenuItemChild.CombatStyleType.MIXED),
                                    new TeleportMenuItemChild("Fire Titan", new Position(3284, 2847),  7355, TeleportMenuItemChild.CombatStyleType.MIXED),
                                    new TeleportMenuItemChild("Infernal Pyrelord @cya@(World Boss)", new Position(3284, 2847),  39465, TeleportMenuItemChild.CombatStyleType.MAGIC)
                            }
                    )
            },
            {
                    /*
                     * MINI-GAMES
                    */
                    new TeleportMenuItemParent("Zombies", TeleportType.MINI_GAMES,
                            new TeleportMenuItemChild[]{
                                    new TeleportMenuItemChild("Fadli", new Position(2503, 3743),  958, TeleportMenuItemChild.CombatStyleType.MIXED)
                            }
                    ),
                    new TeleportMenuItemParent("Duel Arena", TeleportType.MINI_GAMES,
                            new TeleportMenuItemChild[]{
                                    new TeleportMenuItemChild("Fadli", new Position(3372, 3269, 0),  958,TeleportMenuItemChild.CombatStyleType.MIXED)
                            }
                    )
            },
            {
                    /*
                     * RAIDS
                    */
                    new TeleportMenuItemParent("Duel Arena", TeleportType.MINI_GAMES,
                            new TeleportMenuItemChild[]{
                                    new TeleportMenuItemChild("Fadli", new Position(3372, 3269, 0),  958,TeleportMenuItemChild.CombatStyleType.MIXED)
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
