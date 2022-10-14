package com.ruse.world.content.collectionlog;

import com.ruse.model.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public enum CollectionLogTab {
    BOSSES(
            new Log[] {
                    new Log("Archer",
                            LogType.KILLING, new int[] {20859, 16048, 16049, 16050, 16051, 16052, 15126, 995},
                            new Item[] {new Item(4151, 1), new Item(4151, 1), new Item(4151, 1)}),

                    new Log("Magician",
                            LogType.KILLING, new int[] {18341, 16058, 16056, 16057, 16055, 16054, 16053, 42002, 995},
                            new Item[] {new Item(4151, 1), new Item(4151, 1), new Item(4151, 1)}),

                    new Log("Warrior",
                            LogType.KILLING, new int[] {909, 16062, 16063, 16059, 16061, 16060, 10364 },
                            new Item[] {new Item(4151, 1), new Item(4151, 1), new Item(4151, 1)}),

                    new Log("Ancient Skeleton",
                            LogType.KILLING, new int[] {18340, 18343, 18342},
                            new Item[] {new Item(4151, 1)}),

                    new Log("Ancient Scorpion",
                            LogType.KILLING, new int[] {18342, 18342, 18342},
                            new Item[] {new Item(4151, 1)}),

                    new Log("Forest Overseer",
                            LogType.KILLING, new int[] {79, 902, 897, 898, 899, 900, 901, 904},
                            new Item[] {new Item(4151, 1)}),

                    new Log("Forest Guardian",
                            LogType.KILLING, new int[] {905, 918, 54219, 906, 907, 908, 910, 911},
                            new Item[] {new Item(4151, 1)}),

                    new Log("Forest Beast",
                            LogType.KILLING, new int[] {917, 912, 913, 914, 915, 916},
                            new Item[] {new Item(4151, 1)}),

                    new Log("Mech Gentleman",
                            LogType.KILLING, new int[] {4151, 4151, 4151},
                            new Item[] {new Item(4151, 1)}),

                    new Log("Forgotten Soul",
                            LogType.KILLING, new int[] {4151, 4151, 4151},
                            new Item[] {new Item(4151, 1)}),

                    new Log("Zombified Thrall",
                            LogType.KILLING, new int[] {4151, 4151, 4151},
                            new Item[] {new Item(4151, 1)}),

                    new Log("Skeletal Thrall",
                            LogType.KILLING, new int[] {4151, 4151, 4151},
                            new Item[] {new Item(4151, 1)}),

                    new Log("Ghostly Thrall",
                            LogType.KILLING, new int[] {4151, 4151, 4151},
                            new Item[] {new Item(4151, 1)}),

                    new Log("Manic Engineer",
                            LogType.KILLING, new int[] {4151, 4151, 4151},
                            new Item[] {new Item(4151, 1)}),

                    new Log("Fire Monster",
                            LogType.KILLING, new int[] {4151, 4151, 4151},
                            new Item[] {new Item(4151, 1)}),

                    new Log("Pyrefiend",
                            LogType.KILLING, new int[] {4151, 4151, 4151},
                            new Item[] {new Item(4151, 1)}),

                    new Log("Fire Wave",
                            LogType.KILLING, new int[] {4151, 4151, 4151},
                            new Item[] {new Item(4151, 1)}),

                    new Log("Fire Titan",
                            LogType.KILLING, new int[] {4151, 4151, 4151},
                            new Item[] {new Item(4151, 1)}),

                    new Log("Infernal Pyrelord",
                            LogType.KILLING, new int[] {4151, 4151, 4151},
                            new Item[] {new Item(4151, 1)})
            }
    ),

    RAIDS(new Log[] {}),

    CLUES(new Log[] {}),

    MINIGAMES(new Log[] {}),

    OTHER(new Log[] {})

    ;

    private final Log[] logs;

    CollectionLogTab(Log[] logNames) {
        this.logs = logNames;
    }

    public Log[] getLogs() {
        return logs;
    }

    public static final int SIZE;
    public static final List<Log> ALL_LOGS = new ArrayList<>();

    static {
        for(CollectionLogTab tab : values()) {
            ALL_LOGS.addAll(Arrays.asList(tab.logs));
        }
        SIZE = ALL_LOGS.size();
    }

    public static Optional<Log> getLogByName(String name) {
        return ALL_LOGS
                .parallelStream()
                .filter(log -> log.getName().equalsIgnoreCase(name))
                .findFirst();
    }
}
