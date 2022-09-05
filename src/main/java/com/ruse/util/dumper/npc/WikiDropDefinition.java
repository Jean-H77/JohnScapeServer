package com.ruse.util.dumper.npc;

import java.util.ArrayList;

import com.ruse.model.Item;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.definitions.NpcDropItem;
import lombok.extern.java.Log;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

@Log
public class WikiDropDefinition {

    private static final String DROPS = " drops ";
    private static final String WITH_RARITY = "with rarity ";
    private static final String IN_QUANTITY = "in quantity ";

    private final Document document;

    public WikiDropDefinition(Document document) {
        this.document = document;
    }

    public boolean isRareDropTable() {
        return document.getElementById("Rare_drop_table") != null;
    }

    public ArrayList<NpcDropItem> getItemDrops() {
        ArrayList<String> dropStrings = new ArrayList<>();
        ArrayList<NpcDropItem> dropObjects = new ArrayList<>();
        Elements drops = document.getElementsByClass("wikitable sortable filterable item-drops autosort=4,a");
        drops.forEach(e -> e.getElementsByTag("tbody")
                .forEach(e2 -> e2.getElementsByTag("tr")
                        .forEach(e3 -> e3.getElementsByClass("inventory-image")
                                .forEach(e4 -> e4.getElementsByTag("img")
                                        .forEach(e5 -> dropStrings.add(e5.attr("alt")))))));

        dropStrings.forEach(string -> {
            final String _string = string;
            try {
                boolean noted = string.contains("(noted)");
                string = string.replaceAll("\\(noted\\)", "").toLowerCase().trim();
                String itemName = string.substring(string.indexOf(DROPS) + DROPS.length(), string.indexOf(WITH_RARITY) - 1);
                String rarityString = string.substring(string.indexOf(WITH_RARITY) + WITH_RARITY.length(), string.indexOf(IN_QUANTITY) - 1).replaceAll(",", "");
                String amountString = string.substring(string.indexOf(IN_QUANTITY) + IN_QUANTITY.length());
                itemName = correctItemName(itemName);
                ItemDefinition itemDefinition = ItemDefinition.getDefinitionForName(itemName);

                if (itemDefinition == null) {
                    //log.warning(String.format("No item definition for item: %s", itemName));
                    return;
                }

                if (ignoreItem(itemName)) {
                    return;
                }


                int rarity = 65535;
                int minAmount;
                int maxAmount;
                int itemId = itemDefinition.getId();
                int corrected = correctItemId(itemName) == -1 ? correctItemId(itemId) : correctItemId(itemName);
                if (corrected != -1) {
                    itemId = corrected;
                }

                if (noted) {
                    if (Item.getNoted(itemId) != itemId) {
                        itemId = Item.getNoted(itemId);
                    } else {
                        //log.warning(String.format("Cannot note item [%s]", itemName));
                    }
                }

                switch (rarityString) {
                    case "always":
                        rarity = 0;
                        break;
                    case "common":
                        rarity = -1;
                        break;
                    case "uncommon":
                        rarity = -2;
                        break;
                    case "rare":
                        rarity = -3;
                        break;
                    case "very rare":
                        rarity = -4;
                        break;
                    case "unknown":
                        //log.warning(String.format("Unknown rarity [%s]", _string));
                        break;
                    default:
                        int sub = rarityString.indexOf("/");
                        if (sub != -1) {
                            double a = Double.parseDouble(rarityString.substring(0, sub));
                            double b = Double.parseDouble(rarityString.substring(sub + 1));
                            rarity = (int) Math.ceil(b / a);
                        } else {
                            //log.warning(String.format("No rarity can be found [%s]", _string));
                            return;
                        }
                        break;
                }

                if (amountString.equals("unknown")) {
                    //log.warning(String.format("Unknown quantity [%s]", _string));
                    return;
                } else if (amountString.contains("-")) {
                    int sub = amountString.indexOf("-");
                    minAmount = Integer.parseInt(amountString.substring(0, sub).replaceAll(",", ""));
                    maxAmount = Integer.parseInt(amountString.substring(sub + 1).replaceAll(",", ""));
                } else if (amountString.contains(",")) {
                    String[] split = amountString.split(",");
                    minAmount = Integer.parseInt(split[0].replaceAll(",", "").trim());
                    maxAmount = Integer.parseInt(split[split.length - 1].replaceAll(",", "").trim());
                } else if (amountString.contains(";")) {
                    String[] split = amountString.split(";");
                    minAmount = Integer.parseInt(split[0].replaceAll(",", "").trim());
                    maxAmount = Integer.parseInt(split[split.length - 1].replaceAll(",", "").trim());
                } else {
                    minAmount = maxAmount = Integer.parseInt(amountString);
                }

                if (minAmount >= maxAmount) {
                    dropObjects.add(new NpcDropItem(itemName, itemId, new int[] {minAmount}, rarity));
                } else {
                    dropObjects.add(new NpcDropItem(itemName, itemId, new int[] {minAmount, maxAmount - minAmount}, rarity));
                }

                //log.info(String.format("[Name=%s, ItemId=%s, Min=%s Max=%s Rarity=1/%s]", itemName, itemId, minAmount, maxAmount, rarity));
            } catch (Exception e) {
                //log.severe(String.format("Error while parsing drop: %s", _string));
                e.printStackTrace();
            }
        });

        // Lowest chance filtered to top of the list
        dropObjects.sort((d1, d2) -> d2.getChance() - d1.getChance());
        return dropObjects;
    }

    private static String correctItemName(String name) {
        if (name.contains("potion")) {
            for (int doseInt = 1; doseInt <= 4; doseInt++) {
                String dose = "(" + doseInt + ")";
                if (name.contains(dose)) {
                    return name.replace(dose, " " + dose);
                }
            }
        }

        if (name.equalsIgnoreCase("runite bar"))
            return "Rune bar";
        if (name.equalsIgnoreCase("adamantite bar"))
            return "Adamant bar";
        if (name.equalsIgnoreCase("tooth half of key"))
            return "tooth half of a key";
        if (name.equalsIgnoreCase("loop half of key"))
            return "loop half of a key";
        return name;
    }

    private static boolean ignoreItem(String name) {
        // Hard code ignore items
        return false;
    }

    private static int correctItemId(int id) {
        switch (id) {
            case 617: // Coins
                return 995;
        }
        return id;
    }

    private static int correctItemId(String name) {
        // Hard code stuff here that's specific like clue scrolls

        switch (name) {
            case "coins":
                return 995;
        }
        return -1;
    }

}
