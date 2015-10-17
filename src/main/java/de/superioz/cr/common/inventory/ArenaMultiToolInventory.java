package de.superioz.cr.common.inventory;

import de.superioz.cr.util.Utilities;
import de.superioz.library.minecraft.server.common.inventory.VirtualInventory;
import de.superioz.library.minecraft.server.items.interact.ItemInteractable;
import org.bukkit.entity.Player;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class ArenaMultiToolInventory extends VirtualInventory {

    public ArenaMultiToolInventory(){
        super("MultiTool - Choice", Size.THREE_ROWS);
    }

    public void load(){
        ItemInteractable defaultItem = new ItemInteractable(Utilities.ItemStacks.MULTITOOL_STACK,
                super.getRaw(), event -> {
            Player player = event.getPlayer();
            player.setItemInHand(Utilities.ItemStacks.MULTITOOL_STACK);
        });

        ItemInteractable axeItem = new ItemInteractable(Utilities.ItemStacks.MULTITOOL_STACK_AXE,
                super.getRaw(), event -> {
            Player player = event.getPlayer();
            player.setItemInHand(Utilities.ItemStacks.MULTITOOL_STACK_AXE);
        });

        ItemInteractable shovelItem = new ItemInteractable(Utilities.ItemStacks.MULTITOOL_STACK_SHOVEL,
                super.getRaw(), event -> {
            Player player = event.getPlayer();
            player.setItemInHand(Utilities.ItemStacks.MULTITOOL_STACK_SHOVEL);
        });

        ItemInteractable pickaxeItem = new ItemInteractable(Utilities.ItemStacks.MULTITOOL_STACK_PICKAXE,
                super.getRaw(), event -> {
            Player player = event.getPlayer();
            player.setItemInHand(Utilities.ItemStacks.MULTITOOL_STACK_PICKAXE);
        });

        ItemInteractable hoeItem = new ItemInteractable(Utilities.ItemStacks.MULTITOOL_STACK_HOE,
                super.getRaw(), event -> {
            Player player = event.getPlayer();
            player.setItemInHand(Utilities.ItemStacks.MULTITOOL_STACK_HOE);
        });

        super.setItem(11, defaultItem);
        super.setItem(12, axeItem);
        super.setItem(13, shovelItem);
        super.setItem(14, pickaxeItem);
        super.setItem(15, hoeItem);
    }

}
