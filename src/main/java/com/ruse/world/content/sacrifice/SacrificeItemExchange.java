package com.ruse.world.content.sacrifice;

import com.ruse.model.Item;
import com.ruse.model.container.ItemContainer;
import com.ruse.model.container.impl.Inventory;
import com.ruse.model.entity.character.player.Player;
import com.ruse.world.content.dialogue.DialogueChain.DialogueChain;
import com.ruse.world.content.dialogue.DialogueChain.Options;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SacrificeItemExchange {

    public static final int INTERFACE_ID = 41198;
    private static final int CAPACITY = 25;
    private static final int TOKEN = 1961;
    private static final int BONUS_ITEM = 7930;
    private static final int BONUS_PERCENTAGE = 25;

    private ItemContainer inventoryCopy;
    private List<Item> addedItems;

    private final Player player;

    public SacrificeItemExchange(Player player) {
        this.player = player;
    }

    public void open() {
        deepCopy();
        addedItems = new ArrayList<>();
        player.getPacketSender()
                .sendInterfaceSet(INTERFACE_ID, 41269)
                .sendItemContainer(inventoryCopy, 41270)
                .sendItemContainer(addedItems, 41217)
                .sendItemOnInterface(41215, TOKEN, 0)
                .sendItemOnInterface(41204, BONUS_ITEM, player.getInventory().contains(BONUS_ITEM) ? 1 : 0)
                .sendString(41206, player.getInventory().contains(BONUS_ITEM) ? "+"+BONUS_PERCENTAGE+".0%" : "+0.0")
                .sendString(41207, "(+0)");
    }

    public void deepCopy() {
        Inventory toCopy = player.getInventory();
        inventoryCopy = new Inventory(player);

        for(int i = 0; i < toCopy.capacity(); i++) {
            Item temp = toCopy.get(i);
            if(temp.getId() == -1) {
                continue;
            }

            inventoryCopy.set(i, new Item(temp.getId(), temp.getAmount()));
        }
    }

    public void addItem(Item item, int slot, int amount) {
        if(item.getDefinition().isNoted()) {
            player.getPacketSender().sendMessage("@red@You can only sacrifice unnoted items");
            return;
        }

        if(item.getDefinition().getSacrificeTokens() == 0) {
            player.getPacketSender().sendMessage("@red@You cannot sacrifice this item.");
            return;
        }

        if(addedItems.size() == CAPACITY) {
            player.getPacketSender().sendMessage("@red@Unable to add more items.");
            return;
        }

        int invAmount = inventoryCopy.getAmountForSlot(slot);

        if(invAmount == 0) {
            return;
        }

        if(amount > invAmount) {
            amount = invAmount;
        }

        inventoryCopy.delete(new Item(item.getId(), amount), slot);

        addedItems.add(item);
        refresh();
    }

    public void checkPrice(Item item) {
        if(item.getDefinition().getSacrificeTokens() == 0) {
            player.getPacketSender().sendMessage("@red@This item has no price.");
            return;
        }
        player.getPacketSender().sendMessage("@red@This item can be exchanged for " + item.getDefinition().getSacrificeTokens() + " sacrifice tokens.");
    }

    public int getBonus() {
        return (int) ((((BONUS_PERCENTAGE / 100.0) * calculateTotalAmount())));
    }

    public void removeItem(int slot, int amount) {
        if(addedItems.isEmpty()) {
            return;
        }

        if(slot >= addedItems.size()) {
            return;
        }

        Item toRemove = addedItems.get(slot);

        if(toRemove == null) {
            return;
        }

        if(amount > toRemove.getAmount()) {
            amount = toRemove.getAmount();
        }

        if(amount == toRemove.getAmount()) {
            inventoryCopy.add(toRemove);
            addedItems.remove(slot);
            refresh();
            return;
        }

        toRemove.decrementAmountBy(amount);
        inventoryCopy.add(new Item(toRemove.getId(), amount));
        refresh();
    }

    public void refresh() {
        player.getPacketSender()
                .sendItemContainer(inventoryCopy, 41270)
                .sendItemContainer(addedItems, 41217)
                .sendItemOnInterface(41215, 1961, calculateTotalAmount())
                .sendString(41207, player.getInventory().contains(BONUS_ITEM) ? "(+"+ (getBonus()) +")" : "(+0)");
    }

    public int calculateTotalAmount() {
        int sum = 0;
        for(Item item : addedItems) {
            sum += item.getDefinition().getSacrificeTokens();

        }
        return sum;
    }

    public void exchange() {
        if(!player.getInventory().containsWithAmount(addedItems)) {
            return;
        }

        DialogueChain dialogueChain = DialogueChain.create(player);
        Inventory inventory = player.getInventory();
        int bonus = getBonus();
        AtomicBoolean useBonus = new AtomicBoolean(false);

        if(inventory.contains(BONUS_ITEM)) {
            dialogueChain.addPart(new Options((p, c, o) -> {
                if (o == 1) {
                    useBonus.set(true);
                }
            }, "Select an option", "Use Sacrifice Booster @blu@(+" + bonus + ")", "No thanks."));
        }

        int total = useBonus.get() ? calculateTotalAmount() + bonus : calculateTotalAmount();
        dialogueChain.addPart(new Options((p, c, o) -> {
            if(o == 1) {
                c.setRemoveInterface(false);

                for(Item item : addedItems) {
                    inventory.delete(item.getId(), item.getAmount());
                }

                if(useBonus.get()) {
                    inventory.delete(BONUS_ITEM, 1);
                }

                inventory.add(TOKEN, total);
                open();
            }
        }, "Select an option","Complete exchange", "Nevermind"));

        dialogueChain.start();
    }
}
