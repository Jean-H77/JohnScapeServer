package com.ruse.world.content.combat.item;

import com.ruse.model.Item;
import com.ruse.model.Items;
import com.ruse.model.entity.character.player.Player;
import com.ruse.util.Misc;

public class RecoilRing {

    public static final int MAX_CHARGES = 100_000;
    public static final String SUFFERING_CHARGES_ATTRIBUTE = "ring_of_suffering_charges";

    public static boolean itemOnItem(Player player, Item item1, Item item2) {
        boolean isRingOfSuffering = item2.getId() == Items.RING_OF_SUFFERING || item2.getId() == Items.RING_OF_SUFFERING_R;
        if (item1.getId() == Items.RING_OF_RECOIL && isRingOfSuffering || item1.getId() == Items.RING_OF_RECOIL_NOTED && isRingOfSuffering) {
            addSufferingCharges(player, item2, item1);
            return true;
        }
        return false;
    }

    public static void checkCharges(Player player, int slot) {
        Item item = player.getInventory().get(slot);
        player.getPacketSender().sendMessage("Your " + item.getDefinition().getName() + " has " + item.getAttributes().getInt(SUFFERING_CHARGES_ATTRIBUTE, 0)
                + " charges left.");
    }

    private static boolean addSufferingCharges(Player player, Item item, Item charge) {
        int current = item.getAttributes().getInt(SUFFERING_CHARGES_ATTRIBUTE, 0);
        int charges = charge.getAmount() * 40;

        if (charges + current > MAX_CHARGES) {
            player.getPacketSender().sendMessage("You can only have " + Misc.insertCommasToNumber(MAX_CHARGES) + " charges.");
            return false;
        }

        item.getAttributes().setInt(SUFFERING_CHARGES_ATTRIBUTE, charges + item.getAttributes().getInt(SUFFERING_CHARGES_ATTRIBUTE, 0));
        if (item.getId() != Items.RING_OF_SUFFERING_R) {
            item.setId(Items.RING_OF_SUFFERING_R);
        }
        player.getInventory().delete(charge);
        player.getInventory().refreshItems();
        player.getPacketSender().sendMessage("You add " + Misc.insertCommasToNumber(charges) + " charges to your ring, you now have " + (current + charges) + ".");
        return true;
    }

}
