package de.superioz.cr.common.tool;

import de.superioz.cr.util.PluginItems;
import de.superioz.library.bukkit.common.inventory.InventoryContent;
import de.superioz.library.bukkit.common.item.InteractableSimpleItem;
import org.bukkit.entity.Player;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class ArenaMultiToolInventory {

    @InventoryContent
    InteractableSimpleItem defaultItem = new InteractableSimpleItem(11, PluginItems.MULTITOOL_STACK,
            event -> {
                Player player = event.getPlayer();
                player.setItemInHand(PluginItems.MULTITOOL_STACK.getWrappedStack());

                event.cancelEvent();
                event.closeInventory();
            });

    @InventoryContent
    InteractableSimpleItem shovelItem = new InteractableSimpleItem(13, PluginItems.MULTITOOL_STACK_SHOVEL,
            event -> {
                Player player = event.getPlayer();
                player.setItemInHand(PluginItems.MULTITOOL_STACK_SHOVEL.getWrappedStack());

                event.cancelEvent();
                event.closeInventory();
            });

    @InventoryContent
    InteractableSimpleItem pickaxeItem = new InteractableSimpleItem(14, PluginItems.MULTITOOL_STACK_PICKAXE,
            event -> {
                Player player = event.getPlayer();
                player.setItemInHand(PluginItems.MULTITOOL_STACK_PICKAXE.getWrappedStack());

                event.cancelEvent();
                event.closeInventory();
            });

    @InventoryContent
    InteractableSimpleItem hoeItem = new InteractableSimpleItem(15, PluginItems.MULTITOOL_STACK_HOE,
            event -> {
                Player player = event.getPlayer();
                player.setItemInHand(PluginItems.MULTITOOL_STACK_HOE.getWrappedStack());

                event.cancelEvent();
                event.closeInventory();
            });

}
