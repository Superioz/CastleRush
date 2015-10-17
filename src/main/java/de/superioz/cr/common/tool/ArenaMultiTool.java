package de.superioz.cr.common.tool;

import de.superioz.cr.common.inventory.ArenaMultiToolInventory;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.util.Utilities;
import de.superioz.library.minecraft.server.items.ItemTool;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class ArenaMultiTool extends ItemTool {

    public ArenaMultiToolHoe hoe = new ArenaMultiToolHoe();
    public ArenaMultiToolPickaxe pickaxe = new ArenaMultiToolPickaxe();
    public ArenaMultiToolShovel shovel = new ArenaMultiToolShovel();

    public ArenaMultiTool(){
        super(Utilities.ItemStacks.MULTITOOL_STACK, event -> {
            Action action = event.action();
            Player player = event.player();

            event.cancel();
            if(checkAction(action, player))
                return;

            CastleRush.getChatMessager().send("&6You can't do anything with this. Use Sneak+Rightclick", player);
        });
    }

    public static boolean checkAction(Action action, Player player){
        if(action == Action.RIGHT_CLICK_AIR
                || action == Action.RIGHT_CLICK_BLOCK
                && player.isSneaking()){
            ArenaMultiToolInventory inv = new ArenaMultiToolInventory();
            inv.load();
            player.openInventory(inv.build());
            return true;
        }
        return false;
    }

    // ====================================================================================

    public class ArenaMultiToolShovel extends ItemTool {
        public ArenaMultiToolShovel(){
            super(Utilities.ItemStacks.MULTITOOL_STACK_SHOVEL, event -> {
                Action action = event.action();
                Player player = event.player();
                Block block = event.clickedBlock();

                if(checkAction(action, player))
                    return;
            });
        }
    }

    public class ArenaMultiToolPickaxe extends ItemTool {
        public ArenaMultiToolPickaxe(){
            super(Utilities.ItemStacks.MULTITOOL_STACK_PICKAXE, event -> {
                Action action = event.action();
                Player player = event.player();
                Block block = event.clickedBlock();

                if(checkAction(action, player))
                    return;
            });
        }
    }

    public class ArenaMultiToolHoe extends ItemTool {
        public ArenaMultiToolHoe(){
            super(Utilities.ItemStacks.MULTITOOL_STACK_HOE, event -> {
                Action action = event.action();
                Player player = event.player();
                Block block = event.clickedBlock();

                if(checkAction(action, player))
                    return;
            });
        }
    }

}
