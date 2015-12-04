package de.superioz.cr.common.inventory;

import de.superioz.cr.util.Utilities;
import de.superioz.library.minecraft.server.common.inventory.InventoryContent;
import de.superioz.library.minecraft.server.common.item.InteractableSimpleItem;
import org.bukkit.entity.Player;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class ArenaMultiToolInventory {

    @InventoryContent
    InteractableSimpleItem defaultItem = new InteractableSimpleItem(11, Utilities.ItemStacks.MULTITOOL_STACK,
            event -> {
                Player player = event.getPlayer();
                player.setItemInHand(Utilities.ItemStacks.MULTITOOL_STACK.getWrappedStack());

                event.cancelEvent();
                event.closeInventory();
            });

    @InventoryContent
    InteractableSimpleItem shovelItem = new InteractableSimpleItem(13, Utilities.ItemStacks.MULTITOOL_STACK_SHOVEL,
            event -> {
                Player player = event.getPlayer();
                player.setItemInHand(Utilities.ItemStacks.MULTITOOL_STACK_SHOVEL.getWrappedStack());

                event.cancelEvent();
                event.closeInventory();
            });

    @InventoryContent
    InteractableSimpleItem pickaxeItem = new InteractableSimpleItem(14, Utilities.ItemStacks.MULTITOOL_STACK_PICKAXE,
            event -> {
                Player player = event.getPlayer();
                player.setItemInHand(Utilities.ItemStacks.MULTITOOL_STACK_PICKAXE.getWrappedStack());

                event.cancelEvent();
                event.closeInventory();
            });

    @InventoryContent
    InteractableSimpleItem hoeItem = new InteractableSimpleItem(15, Utilities.ItemStacks.MULTITOOL_STACK_HOE,
            event -> {
                Player player = event.getPlayer();
                player.setItemInHand(Utilities.ItemStacks.MULTITOOL_STACK_HOE.getWrappedStack());

                event.cancelEvent();
                event.closeInventory();
            });

}
