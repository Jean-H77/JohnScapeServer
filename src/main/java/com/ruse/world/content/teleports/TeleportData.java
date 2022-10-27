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
                                    new TeleportMenuItemChild("Archer", new Position(3055, 2853), 351),
                                    new TeleportMenuItemChild("Magician", new Position(3001, 2835), 352),
                                    new TeleportMenuItemChild("Warrior", new Position(3333, 3333), 353)
                            }
                    ),

                    new TeleportMenuItemParent("Ancient Hideout", TeleportType.AREAS,
                            new TeleportMenuItemChild[]{
                                    new TeleportMenuItemChild("Ancient Skeleton", new Position(3047, 2888),  13397),
                                    new TeleportMenuItemChild("Ancient Scorpion", new Position(3017, 2924),  13399)
                            }
                    ),

                    new TeleportMenuItemParent("Lost Forest", TeleportType.AREAS,
                            new TeleportMenuItemChild[]{
                                    new TeleportMenuItemChild("Forest Overseer", new Position(3226, 2847),  13396),
                                    new TeleportMenuItemChild("Forest Guardian", new Position(3263, 2838),  13395),
                                    new TeleportMenuItemChild("Forest Beast", new Position(3284, 2847),  13394)

                            }
                    ),

                    new TeleportMenuItemParent("Haunted Woods", TeleportType.AREAS,
                            new TeleportMenuItemChild[]{
                                    new TeleportMenuItemChild("Forgotten Soul", new Position(2909, 2833), 5),
                                    new TeleportMenuItemChild("Zombified Thrall", new Position(2914, 2856), 6),
                                    new TeleportMenuItemChild("Skeletal Thrall", new Position(2922, 2892), 7),
                                    new TeleportMenuItemChild("Ghostly Thrall", new Position(2911, 2915), 8),
                                    new TeleportMenuItemChild("Manic Engineer", new Position(2911, 2915), 13400)
                            }
                    ),

                    new TeleportMenuItemParent("The Inferno", TeleportType.AREAS,
                            new TeleportMenuItemChild[]{
                                    new TeleportMenuItemChild("Fire Monster", new Position(2864, 3235),  7773),
                                    new TeleportMenuItemChild("Pyrefiend", new Position(3226, 2847),  1634),
                                    new TeleportMenuItemChild("Fire Wave", new Position(3263, 2838),  9),
                                    new TeleportMenuItemChild("Fire Titan", new Position(3284, 2847),  7355),
                                    new TeleportMenuItemChild("Infernal Pyrelord", new Position(3284, 2847), 22)
                            }
                    ),

                    new TeleportMenuItemParent("Castle", TeleportType.AREAS,
                            new TeleportMenuItemChild[]{
                                    new TeleportMenuItemChild("Fire Monster", new Position(2918, 2724),  10),
                                    new TeleportMenuItemChild("Pyrefiend", new Position(3226, 2847),  11),
                                    new TeleportMenuItemChild("Fire Wave", new Position(3263, 2838),  12),
                                    new TeleportMenuItemChild("Fire Titan", new Position(3284, 2847),  13),
                                    new TeleportMenuItemChild("Infernal Pyrelord", new Position(3284, 2847),  14)
                            }
                    )
            },
            {
                    /*
                     * MINI-GAMES
                    */
                    new TeleportMenuItemParent("Zombies", TeleportType.MINI_GAMES,
                            new TeleportMenuItemChild[]{
                                    new TeleportMenuItemChild("Fadli", new Position(2503, 3743),  958)
                            }
                    ),
                    new TeleportMenuItemParent("Duel Arena", TeleportType.MINI_GAMES,
                            new TeleportMenuItemChild[]{
                                    new TeleportMenuItemChild("Fadli", new Position(3372, 3269, 0),  958)
                            }
                    )
            }
    };

    private TeleportData() {

    }
}
